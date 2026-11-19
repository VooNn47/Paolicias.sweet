package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.repository.ReservaRepository
import com.ban.paoliciassweet.model.EstadoAbono
import com.ban.paoliciassweet.model.PanelAdminModel
import com.ban.paoliciassweet.model.ReservaRemota
import com.ban.paoliciassweet.util.Imagenes
import com.ban.paoliciassweet.util.Notificaciones
import com.ban.paoliciassweet.util.mensajeDeError
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(PanelAdminModel())
        private set

    var reservas by mutableStateOf<List<ReservaRemota>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Id de la reserva que se está validando/rechazando (desactiva sus botones)
    var procesando by mutableStateOf<Long?>(null)
        private set

    val imagenes = mutableStateMapOf<Long, ImageBitmap>()

    // Para no repetir la notificación si la cantidad no cambió
    private var ultimoAviso = 0

    private val repositorio = ReservaRepository()

    fun onFiltroChanged(filtro: String) {
        uiState = uiState.copy(filtro = filtro)
        cargarReservas()
    }

    // El backend ya entrega la lista filtrada y ordenada por fecha
    fun reservasFiltradas(): List<ReservaRemota> = reservas

    fun cargarReservas() {
        viewModelScope.launch {
            cargando = true
            try {
                reservas = repositorio.todas(uiState.filtro)
                errorMessage = null
                revisarPendientes()
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e, "No se pudieron cargar las reservas")
            } finally {
                cargando = false
            }
        }
    }

    // Recurso nativo: notificación cuando hay comprobantes esperando validación
    private suspend fun revisarPendientes() {
        // Sin permiso no se cuenta como "avisado": se avisará cuando la administradora lo conceda
        if (!Notificaciones.tienePermiso(getApplication())) return
        val enRevision = if (uiState.filtro == EstadoAbono.EN_REVISION) reservas.size
        else repositorio.todas(EstadoAbono.EN_REVISION).size
        if (enRevision > ultimoAviso) {
            Notificaciones.avisarComprobantesPendientes(getApplication(), enRevision)
        }
        ultimoAviso = enRevision
    }

    fun cargarImagen(id: Long) {
        if (imagenes.containsKey(id)) return
        viewModelScope.launch {
            try {
                Imagenes.miniatura(repositorio.comprobante(id))?.let { imagenes[id] = it }
            } catch (e: Exception) {
                // La tarjeta muestra "No se pudo leer el comprobante"
            }
        }
    }

    fun validarAbono(id: Long, onExito: () -> Unit) = cambiarEstado(id, onExito) { repositorio.validarAbono(id) }

    fun rechazarAbono(id: Long, onExito: () -> Unit) = cambiarEstado(id, onExito) {
        repositorio.rechazarAbono(id).also { imagenes.remove(id) }
    }

    fun completar(id: Long, onExito: () -> Unit) = cambiarEstado(id, onExito) { repositorio.completar(id) }

    private fun cambiarEstado(id: Long, onExito: () -> Unit, accion: suspend () -> ReservaRemota) {
        if (procesando != null) return
        viewModelScope.launch {
            procesando = id
            try {
                accion()
                errorMessage = null
                onExito()
                reservas = repositorio.todas(uiState.filtro)
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e)
            } finally {
                procesando = null
            }
        }
    }
}
