package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.repository.ProductoRepository
import com.ban.paoliciassweet.model.HomeModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeModel())
        private set


    var productos by mutableStateOf<List<ProductoEntity>>(emptyList())
        private set

    private var job: Job? = null


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


    private fun repositorio(context: Context) =
        ProductoRepository(AppDatabase.getDatabase(context).productoDao())

    fun cargarProductos(context: Context) {
        job?.cancel()
        job = viewModelScope.launch {
            val repo = repositorio(context)


            repo.cargarProductosIniciales()

            repo.obtenerTodos().collect { lista ->
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