package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.repository.ProductoRepository
import com.ban.paoliciassweet.model.HomeModel
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(HomeModel())
        private set

    var productos by mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    // Aviso cuando no hay conexión y se muestra el catálogo guardado
    var aviso by mutableStateOf<String?>(null)
        private set

    private val repositorio = ProductoRepository(AppDatabase.getDatabase(application).productoDao())

    val categorias: List<String>
        get() = listOf("Todos") + productos.map { it.categoria }.distinct()

    val productosFiltrados: List<ProductoEntity>
        get() {
            val estado = uiState
            return productos.filter { p ->
                val categoriaOk = estado.categoria == "Todos" || estado.categoria == p.categoria
                val busquedaOk = estado.busqueda.isBlank() ||
                        p.nombre.contains(estado.busqueda.trim(), ignoreCase = true)
                categoriaOk && busquedaOk
            }
        }

    init {
        // La lista observa Room; cuando llega el catálogo del backend se actualiza sola
        viewModelScope.launch {
            repositorio.obtenerTodos().collect { productos = it }
        }
        cargarProductos()
    }

    fun cargarProductos() {
        if (cargando) return
        viewModelScope.launch {
            cargando = true
            val conectado = repositorio.sincronizar()
            aviso = if (conectado) null else "Sin conexión: se muestra el catálogo guardado"
            cargando = false
        }
    }

    fun onBusquedaChanged(busqueda: String) {
        uiState = uiState.copy(busqueda = busqueda)
    }

    fun onCategoriaChanged(categoria: String) {
        uiState = uiState.copy(categoria = categoria)
    }
}
