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
import kotlinx.coroutines.launch

class ProductoDetalleViewModel(application: Application) : AndroidViewModel(application) {

    var producto by mutableStateOf<ProductoEntity?>(null)
        private set

    var noEncontrado by mutableStateOf(false)
        private set

    private val repositorio = ProductoRepository(AppDatabase.getDatabase(application).productoDao())

    fun cargar(id: Int) {
        viewModelScope.launch {
            producto = repositorio.obtenerPorId(id)
            noEncontrado = producto == null
        }
    }
}
