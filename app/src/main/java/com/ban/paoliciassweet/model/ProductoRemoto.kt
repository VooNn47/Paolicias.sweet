package com.ban.paoliciassweet.model

import com.ban.paoliciassweet.data.local.entity.ProductoEntity

// Producto tal como lo devuelve catalogo-service (GET /api/productos)
data class ProductoRemoto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String?,
    val categoria: String,
    val precio: Int,
    val porciones: Int,
    val imagen: String?
) {
    // Se guarda en Room con el MISMO id del backend: así el detalle y la reserva usan los mismos ids
    fun aEntidad() = ProductoEntity(
        id = (id ?: 0L).toInt(),
        nombre = nombre,
        descripcion = descripcion.orEmpty(),
        categoria = categoria,
        precio = precio,
        porciones = porciones,
        imagen = imagen.orEmpty()
    )
}
