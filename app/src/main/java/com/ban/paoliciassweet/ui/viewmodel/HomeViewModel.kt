package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.local.seed.productosIniciales
import com.ban.paoliciassweet.model.HomeModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeModel())
        private set

    // Productos tal como vienen de la base de datos
    var productos by mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    private var job: Job? = null

    // Las categorías salen de los productos guardados, no de una lista fija
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

    fun cargarProductos(context: Context) {
        job?.cancel()
        job = viewModelScope.launch {
            val dao = AppDatabase.getDatabase(context).productoDao()

            // Primera vez: la tabla está vacía, se cargan los productos iniciales
            if (dao.contar() == 0) {
                dao.insertarTodos(productosIniciales)
            }

            dao.obtenerTodos().collect { lista ->
                productos = lista
            }
        }
    }

    fun onBusquedaChanged(busqueda: String) {
        uiState = uiState.copy(busqueda = busqueda)
    }

    fun onCategoriaChanged(categoria: String) {
        uiState = uiState.copy(categoria = categoria)
    }
}