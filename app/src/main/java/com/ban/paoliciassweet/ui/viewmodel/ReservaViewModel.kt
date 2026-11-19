package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.repository.CotizacionRepository
import com.ban.paoliciassweet.data.repository.ProductoRepository
import com.ban.paoliciassweet.data.repository.ReservaRepository
import com.ban.paoliciassweet.model.Cotizacion
import com.ban.paoliciassweet.model.NuevaReserva
import com.ban.paoliciassweet.model.ReservaModel
import com.ban.paoliciassweet.model.SeleccionEvento
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.util.validarFecha
import com.ban.paoliciassweet.ui.util.validarHorario
import com.ban.paoliciassweet.ui.util.validarInvitados
import com.ban.paoliciassweet.ui.util.validarNombreEvento
import com.ban.paoliciassweet.util.mensajeDeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReservaViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ReservaModel())
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var productosElegidos by mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    // Desglose del precio que calcula el backend antes de enviar
    var cotizacion by mutableStateOf<Cotizacion?>(null)
        private set

    var cotizacionError by mutableStateOf<String?>(null)
        private set

    private var jobCotizacion: Job? = null

    private val productoRepositorio = ProductoRepository(AppDatabase.getDatabase(application).productoDao())
    private val reservaRepositorio = ReservaRepository()
    private val cotizacionRepositorio = CotizacionRepository()

    fun onNombreEventoChanged(nombre: String) {
        uiState = uiState.copy(nombreEvento = nombre, nombreEventoError = null)
    }

    fun onFechaChanged(fecha: String) {
        uiState = uiState.copy(fecha = fecha, fechaError = null)
    }

    fun onHorarioChanged(horario: String) {
        uiState = uiState.copy(horario = horario, horarioError = null)
    }

    fun onInvitadosChanged(invitados: String) {
        uiState = uiState.copy(invitados = invitados, invitadosError = null)
        actualizarCotizacion()
    }

    fun onObservacionesChanged(observaciones: String) {
        uiState = uiState.copy(observaciones = observaciones)
    }

    fun cargarProductosElegidos() {
        viewModelScope.launch {
            productosElegidos = SeleccionEvento.productoIds.mapNotNull { productoRepositorio.obtenerPorId(it) }
            actualizarCotizacion()
        }
    }

    // Pide el desglose al backend cuando la cantidad de invitados es válida.
    // Espera 400 ms para no llamar al servidor con cada tecla.
    private fun actualizarCotizacion() {
        jobCotizacion?.cancel()
        val cantidad = uiState.invitados.trim().toIntOrNull()
        if (validarInvitados(uiState.invitados) != null || cantidad == null) {
            cotizacion = null
            cotizacionError = null
            return
        }
        jobCotizacion = viewModelScope.launch {
            delay(400)
            try {
                cotizacion = cotizacionRepositorio.cotizar(cantidad, productosElegidos.map { it.id.toLong() })
                cotizacionError = null
            } catch (e: Exception) {
                cotizacion = null
                cotizacionError = mensajeDeError(e)
            }
        }
    }

    // onExito se llama solo si el servidor guardó la reserva (ahí vibra el teléfono)
    fun guardarReserva(onExito: () -> Unit) {
        if (cargando) return // evita el doble envío
        val state = uiState
        successMessage = null
        errorMessage = null

        val nombreError = validarNombreEvento(state.nombreEvento)
        val fechaError = validarFecha(state.fecha)
        val horarioError = validarHorario(state.horario)
        val invitadosError = validarInvitados(state.invitados)

        if (nombreError != null || fechaError != null || horarioError != null || invitadosError != null) {
            uiState = state.copy(
                nombreEventoError = nombreError,
                fechaError = fechaError,
                horarioError = horarioError,
                invitadosError = invitadosError
            )
            return
        }

        val nueva = NuevaReserva(
            nombreEvento = state.nombreEvento.trim(),
            fecha = state.fecha,
            horario = state.horario,
            invitados = state.invitados.trim().toInt(),
            observaciones = state.observaciones.trim(),
            correoCliente = Sesion.correo,
            productoIds = productosElegidos.map { it.id.toLong() }
        )

        viewModelScope.launch {
            cargando = true
            try {
                val creada = reservaRepositorio.crear(nueva)
                successMessage = "¡Reserva enviada! Total ${formatoPrecio(creada.total)}. " +
                        "Paga el abono de ${formatoPrecio(creada.abono)} y adjunta el comprobante en Mi Agenda."
                // Formulario limpio: un segundo toque ya no duplica la reserva
                uiState = ReservaModel()
                SeleccionEvento.limpiar()
                productosElegidos = emptyList()
                cotizacion = null
                onExito()
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e, "No se pudo enviar la reserva")
            } finally {
                cargando = false
            }
        }
    }
}
