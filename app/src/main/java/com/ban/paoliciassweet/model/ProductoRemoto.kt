package com.grupo9.model

data class ProductoRemoto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Int,
    val porciones: Int,
    val imagen: String
)