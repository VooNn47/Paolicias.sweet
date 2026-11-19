package com.grupo9.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.api.RetrofitProducto
import com.ban.paoliciassweet.model.ProductoRemoto
import kotlinx.coroutines.launch

// ANTIGUO: pantalla de prueba de Retrofit. Ya no se usa (el catálogo real está en HomeViewModel).
// Se puede borrar junto con ProductoRemotoScreen.kt.
class ProductoViewModel : ViewModel() {

    private val _productos = MutableLiveData<List<ProductoRemoto>>()
    val productos: LiveData<List<ProductoRemoto>> = _productos

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                _productos.value = RetrofitProducto.api.obtenerProductos()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            }
        }
    }
}
