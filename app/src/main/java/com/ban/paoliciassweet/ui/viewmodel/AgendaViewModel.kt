package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.model.AgendaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class AgendaViewmodel : ViewModel() {

    var uiState by mutableStateOf(AgendaModel())

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun cargarReservas(reservas: AgendaModel) {
        uiState = reservas
        errorMessage = null
    }

    fun mostrarError(mensaje: String) {
        errorMessage = mensaje
    }

    fun cargarReserva(context: Context) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val reserva = db.reservaDao().obtenerReservas()

            uiState = uiState.copy(reserva = reserva)
        }
    }

    // Copia la imagen elegida al almacenamiento de la app y guarda su ruta en la reserva
    fun guardarComprobante(context: Context, uri: Uri) {
        val reserva = uiState.reserva ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val archivo = File(
                    context.filesDir,
                    "comprobante_${reserva.id}_${System.currentTimeMillis()}.jpg"
                )
                context.contentResolver.openInputStream(uri)?.use { entrada ->
                    archivo.outputStream().use { salida ->
                        entrada.copyTo(salida)
                    }
                }

                val db = AppDatabase.getDatabase(context)
                db.reservaDao().adjuntarComprobante(reserva.id, archivo.absolutePath)

                errorMessage = null
                cargarReserva(context)
            } catch (e: Exception) {
                errorMessage = "No se pudo guardar el comprobante."
            }
        }
    }

    // (se elimina cuando quitemos el saldo)
    fun pagarReserva(context: Context, precio: Int) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)

            val saldoGuardado = db.saldoDao().obtenerSaldo()

            if (saldoGuardado != null && saldoGuardado.monto >= precio) {
                val reserva = uiState.reserva

                if (reserva != null && !reserva.pagada) {
                    val resultado = db.saldoDao().descontarSaldo(precio)

                    if (resultado > 0) {
                        db.reservaDao().marcarReservaPagada(reserva.id)
                        cargarReserva(context)
                    }
                } else {
                    errorMessage = "La reserva ya está pagada o no existe."
                }
            } else {
                errorMessage = "No tienes saldo suficiente para pagar la reserva."
            }
        }
    }
}