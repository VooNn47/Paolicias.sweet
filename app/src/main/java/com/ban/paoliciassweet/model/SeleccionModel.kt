package com.ban.paoliciassweet.model

import androidx.compose.runtime.mutableStateListOf

// Productos que el cliente va eligiendo para su evento (igual que Sesion, vive en memoria).
object SeleccionEvento {
    val productoIds = mutableStateListOf<Int>()

    fun alternar(id: Int) {
        if (id in productoIds) productoIds.remove(id) else productoIds.add(id)
    }

    fun limpiar() {
        productoIds.clear()
    }
}