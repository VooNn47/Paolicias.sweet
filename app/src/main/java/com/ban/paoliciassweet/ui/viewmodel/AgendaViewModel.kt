package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.repository.ReservaRepository
import com.ban.paoliciassweet.model.AgendaModel
import com.ban.paoliciassweet.model.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AgendaViewmodel : ViewModel() {

    var uiState by mutableStateOf(AgendaModel())

    var errorMessage by mutableStateOf<String?>(null)
        private set


    private fun repositorio(context: Context) =
        ReservaRepository(AppDatabase.getDatabase(context).reservaDao())


    fun cargarReservas(context: Context) {
        viewModelScope.launch {
            val lista = repositorio(context).obtenerPorCliente(Sesion.correo)

            uiState = uiState.copy(reservas = lista)
        }
    }


    fun guardarComprobante(context: Context, reservaId: Int, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val archivo = File(
                    context.filesDir,
                    "comprobante_${reservaId}_${System.currentTimeMillis()}.jpg"
                )
                context.contentResolver.openInputStream(uri)?.use { entrada ->
                    archivo.outputStream().use { salida ->
                        entrada.copyTo(salida)
                    }
                }

                repositorio(context).adjuntarComprobante(reservaId, archivo.absolutePath)

                errorMessage = null
                cargarReservas(context)
            } catch (e: Exception) {
                errorMessage = "No se pudo guardar el comprobante."
            }
        }
    }
}