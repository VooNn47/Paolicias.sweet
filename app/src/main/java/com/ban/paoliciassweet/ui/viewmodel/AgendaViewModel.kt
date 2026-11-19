package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.repository.ExternasRepository
import com.ban.paoliciassweet.data.repository.ReservaRepository
import com.ban.paoliciassweet.model.AgendaModel
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.util.Imagenes
import com.ban.paoliciassweet.util.mensajeDeError
import kotlinx.coroutines.launch

class AgendaViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(AgendaModel())
        private set

    var cargando by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    // Id de la reserva cuyo comprobante se está subiendo (para mostrar el progreso en su tarjeta)
    var subiendo by mutableStateOf<Long?>(null)
        private set

    // Miniaturas de los comprobantes ya descargados, por id de reserva
    val imagenes = mutableStateMapOf<Long, ImageBitmap>()

    // Dólares por 1 peso (API externa ExchangeRate-API), para mostrar el precio en USD
    var tasaDolar by mutableStateOf<Double?>(null)
        private set

    private val repositorio = ReservaRepository()

    init {
        viewModelScope.launch { tasaDolar = ExternasRepository.tasaDolar() }
    }

    fun cargarReservas() {
        viewModelScope.launch {
            cargando = true
            try {
                uiState = uiState.copy(reservas = repositorio.misReservas(Sesion.correo))
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e, "No se pudieron cargar tus reservas")
            } finally {
                cargando = false
            }
        }
    }

    fun cargarImagen(id: Long) {
        if (imagenes.containsKey(id)) return
        viewModelScope.launch {
            try {
                Imagenes.miniatura(repositorio.comprobante(id))?.let { imagenes[id] = it }
            } catch (e: Exception) {
                // Sin miniatura: la tarjeta igual muestra el estado
            }
        }
    }

    // La foto viene de la cámara o de la galería: se achica y se sube al backend
    fun subirComprobante(reservaId: Long, uri: Uri) {
        if (subiendo != null) return
        viewModelScope.launch {
            subiendo = reservaId
            try {
                val jpeg = Imagenes.comprimirComprobante(getApplication(), uri)
                repositorio.subirComprobante(reservaId, Sesion.correo, jpeg)
                imagenes.remove(reservaId)
                errorMessage = null
                mensaje = "Comprobante enviado. Quedó En revisión."
                uiState = uiState.copy(reservas = repositorio.misReservas(Sesion.correo))
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e, "No se pudo subir el comprobante")
            } finally {
                subiendo = null
            }
        }
    }

    fun mensajeMostrado() {
        mensaje = null
    }
}
