package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.repository.InsumoRepository
import com.ban.paoliciassweet.model.Insumo
import com.ban.paoliciassweet.util.mensajeDeError
import kotlinx.coroutines.launch

// Pantalla "Insumos" de la administradora: inventario y ajuste de stock
class InsumosViewModel(application: Application) : AndroidViewModel(application) {

    var insumos by mutableStateOf<List<Insumo>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    // Insumo que se está editando (abre el diálogo) y el texto del campo
    var editando by mutableStateOf<Insumo?>(null)
        private set

    var stockTexto by mutableStateOf("")
        private set

    var stockError by mutableStateOf<String?>(null)
        private set

    private val repositorio = InsumoRepository()

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            try {
                insumos = repositorio.listar()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = mensajeDeError(e, "No se pudo cargar el inventario")
            } finally {
                cargando = false
            }
        }
    }

    fun editar(insumo: Insumo) {
        editando = insumo
        stockTexto = insumo.stock.toString().removeSuffix(".0").replace('.', ',')
        stockError = null
    }

    fun onStockChanged(texto: String) {
        stockTexto = texto
        stockError = null
    }

    fun cancelarEdicion() {
        editando = null
    }

    // Se acepta coma o punto como separador decimal
    fun guardarStock() {
        val insumo = editando ?: return
        val valor = stockTexto.trim().replace(',', '.').toDoubleOrNull()
        stockError = when {
            stockTexto.isBlank() -> "Ingresa el stock"
            valor == null -> "Ingresa un número (ej: 2,5)"
            valor < 0 -> "El stock no puede ser negativo"
            else -> null
        }
        if (stockError != null || valor == null) return

        viewModelScope.launch {
            try {
                val actualizado = repositorio.actualizarStock(insumo.id, valor)
                insumos = insumos.map { if (it.id == actualizado.id) actualizado else it }
                editando = null
                mensaje = "Stock de ${actualizado.nombre} actualizado"
            } catch (e: Exception) {
                stockError = mensajeDeError(e, "No se pudo guardar el stock")
            }
        }
    }

    fun mensajeMostrado() {
        mensaje = null
    }
}
