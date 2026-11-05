package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import kotlinx.coroutines.launch

class ProductoDetalleViewModel : ViewModel() {

    var producto by mutableStateOf<ProductoEntity?>(null)
        private set

    fun cargar(context: Context, id: Int) {
        viewModelScope.launch {
            producto = AppDatabase.getDatabase(context).productoDao().obtenerPorId(id)
        }
    }
}