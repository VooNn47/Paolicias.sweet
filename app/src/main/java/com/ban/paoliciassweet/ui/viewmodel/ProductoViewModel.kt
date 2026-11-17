package com.grupo9.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo9.data.api.RetrofitProducto
import com.grupo9.model.ProductoRemoto
import kotlinx.coroutines.launch

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