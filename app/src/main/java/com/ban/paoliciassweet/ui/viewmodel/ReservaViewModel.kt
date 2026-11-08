package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ReservaEntity
import com.ban.paoliciassweet.model.ReservaModel
import com.ban.paoliciassweet.model.SeleccionEvento
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.util.calcularCotizacion
import kotlinx.coroutines.launch

class ReservaViewmodel : ViewModel() {

    var uiState by mutableStateOf(ReservaModel())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var productosElegidos by mutableStateOf<List<String>>(emptyList())
        private set

    fun onNombreEventoChanged(nombre: String) {
        uiState = uiState.copy(nombreEvento = nombre)
    }

    fun onFechaChanged(fecha: String) {
        uiState = uiState.copy(fecha = fecha)
    }

    fun onHorarioChanged(horario: String) {
        uiState = uiState.copy(horario = horario)
    }

    fun onInvitadosChanged(invitados: String) {
        uiState = uiState.copy(invitados = invitados)
    }

    fun onObservacionesChanged(observaciones: String) {
        uiState = uiState.copy(observaciones = observaciones)
    }

    fun cargarProductosElegidos(context: Context) {
        viewModelScope.launch {
            val dao = AppDatabase.getDatabase(context).productoDao()
            productosElegidos = SeleccionEvento.productoIds.mapNotNull { id ->
                dao.obtenerPorId(id)?.nombre
            }
        }
    }

    fun guardarReserva(context: Context) {
        val state = uiState

        // Validar campos obligatorios
        if (
            state.nombreEvento.isBlank() ||
            state.fecha.isBlank() ||
            state.horario.isBlank() ||
            state.invitados.isBlank()
        ) {
            errorMessage = "Favor completar todos los campos obligatorios."
            successMessage = null
            return
        }

        // Validar que los invitados sean un número entre 1 y 100
        val cantidad = state.invitados.trim().toIntOrNull()
        if (cantidad == null || cantidad < 1 || cantidad > 100) {
            errorMessage = "Los invitados deben ser un número entre 1 y 100."
            successMessage = null
            return
        }

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)

            val elegidos = SeleccionEvento.productoIds.mapNotNull { id ->
                db.productoDao().obtenerPorId(id)
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

            db.reservaDao().guardarReserva(nuevaReserva)

            errorMessage = null
            successMessage = "¡Reserva guardada exitosamente!"
        }
    }
}