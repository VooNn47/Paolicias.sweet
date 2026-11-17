package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.repository.ReservaRepository
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

    // El ViewModel no habla con Room: pide los datos al Repository
    private fun repositorio(context: Context) =
        ReservaRepository(AppDatabase.getDatabase(context).reservaDao())

    fun onFiltroChanged(filtro: String) {
        uiState = uiState.copy(filtro = filtro)
    }

    fun reservasFiltradas(): List<ReservaEntity> {
        return if (uiState.filtro == "Todas") reservas
        else reservas.filter { it.estadoAbono == uiState.filtro }
    }

    fun cargarReservas(context: Context) {
        viewModelScope.launch {
            reservas = repositorio(context).obtenerTodas()
        }
    }


    fun validarAbono(context: Context, id: Int) {
        viewModelScope.launch {
            val repo = repositorio(context)
            val hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            repo.validarAbono(id, hoy)
            reservas = repo.obtenerTodas()
        }
    }


    fun rechazarAbono(context: Context, id: Int) {
        viewModelScope.launch {
            val repo = repositorio(context)
            repo.rechazarAbono(id)
            reservas = repo.obtenerTodas()
        }
    }
}