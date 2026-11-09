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
import kotlinx.coroutines.launch

class ProductoDetalleViewModel : ViewModel() {

    var producto by mutableStateOf<ProductoEntity?>(null)
        private set


    private fun repositorio(context: Context) =
        ProductoRepository(AppDatabase.getDatabase(context).productoDao())

    fun cargar(context: Context, id: Int) {
        viewModelScope.launch {
            producto = repositorio(context).obtenerPorId(id)
        }
    }
}