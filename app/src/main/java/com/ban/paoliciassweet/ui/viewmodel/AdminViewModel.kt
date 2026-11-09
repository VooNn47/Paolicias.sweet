package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ReservaEntity
import com.ban.paoliciassweet.model.PanelAdminModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminViewModel : ViewModel() {

    var uiState by mutableStateOf(PanelAdminModel())
        private set

    var reservas by mutableStateOf<List<ReservaEntity>>(emptyList())
        private set

    fun onFiltroChanged(filtro: String) {
        uiState = uiState.copy(filtro = filtro)
    }

    fun reservasFiltradas(): List<ReservaEntity> {
        return if (uiState.filtro == "Todas") reservas
        else reservas.filter { it.estadoAbono == uiState.filtro }
    }

    fun cargarReservas(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            reservas = db.reservaDao().obtenerTodas()
        }
    }

    // Acepta el comprobante: estado "Pagado 50%" y fecha de hoy como fechaPago (dd/MM/yyyy)
    fun validarAbono(context: Context, id: Int) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            db.reservaDao().validarAbono(id, hoy)
            reservas = db.reservaDao().obtenerTodas()
        }
    }

    // Rechaza el comprobante: vuelve a "Pendiente" y el cliente debe adjuntar otro
    fun rechazarAbono(context: Context, id: Int) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            db.reservaDao().rechazarAbono(id)
            reservas = db.reservaDao().obtenerTodas()
        }
    }
}