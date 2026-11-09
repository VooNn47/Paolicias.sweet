package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ReservaEntity
import com.ban.paoliciassweet.data.repository.ProductoRepository
import com.ban.paoliciassweet.data.repository.ReservaRepository
import com.ban.paoliciassweet.model.ReservaModel
import com.ban.paoliciassweet.model.SeleccionEvento
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.util.calcularCotizacion
import com.ban.paoliciassweet.ui.util.validarFecha
import com.ban.paoliciassweet.ui.util.validarHorario
import com.ban.paoliciassweet.ui.util.validarInvitados
import com.ban.paoliciassweet.ui.util.validarNombreEvento
import kotlinx.coroutines.launch

class ReservaViewmodel : ViewModel() {

    var uiState by mutableStateOf(ReservaModel())
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var productosElegidos by mutableStateOf<List<String>>(emptyList())
        private set


    private fun reservaRepositorio(context: Context) =
        ReservaRepository(AppDatabase.getDatabase(context).reservaDao())

    private fun productoRepositorio(context: Context) =
        ProductoRepository(AppDatabase.getDatabase(context).productoDao())


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
    }

    fun onObservacionesChanged(observaciones: String) {
        uiState = uiState.copy(observaciones = observaciones)
    }

    fun cargarProductosElegidos(context: Context) {
        viewModelScope.launch {
            val repo = productoRepositorio(context)
            productosElegidos = SeleccionEvento.productoIds.mapNotNull { id ->
                repo.obtenerPorId(id)?.nombre
            }
        }
    }

    fun guardarReserva(context: Context) {
        val state = uiState

        
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
            successMessage = null
            return
        }


        val cantidad = state.invitados.trim().toInt()

        viewModelScope.launch {
            val elegidos = SeleccionEvento.productoIds.mapNotNull { id ->
                productoRepositorio(context).obtenerPorId(id)
            }
            val cotizacion = calcularCotizacion(cantidad, elegidos)

            val nuevaReserva = ReservaEntity(
                nombreEvento = state.nombreEvento,
                fecha = state.fecha,
                horario = state.horario,
                invitados = state.invitados.trim(),
                observaciones = state.observaciones,
                correoCliente = Sesion.correo,
                productos = SeleccionEvento.productoIds.joinToString(","),
                total = cotizacion.total
            )

            reservaRepositorio(context).guardar(nuevaReserva)

            successMessage = "¡Reserva guardada exitosamente!"
        }
    }
}