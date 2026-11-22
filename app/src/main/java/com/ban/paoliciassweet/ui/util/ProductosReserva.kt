package com.ban.paoliciassweet.ui.util

import com.ban.paoliciassweet.data.local.entity.ProductoEntity

// La reserva guarda solo los ids de sus productos ("3,7"). Para mostrarlos se buscan en el catálogo
// y las unidades se calculan igual que en la cotización: invitados ÷ porciones, hacia arriba.

// "3, 7" -> [3, 7]   (ignora lo que no sea un número)
fun idsProductos(productos: String?): List<Int> =
    productos.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }

// "Kuchen de Frambuesa × 2, Pie de Limón × 2"
// Si la reserva no tiene productos: "Sin productos (solo servicio)".
// Si un producto ya no está en el catálogo se muestra por su número.
fun textoProductos(productos: String?, invitados: Int, catalogo: Map<Int, ProductoEntity>): String {
    val ids = idsProductos(productos)
    if (ids.isEmpty()) return "Sin productos (solo servicio)"
    return ids.joinToString(", ") { id ->
        val producto = catalogo[id] ?: return@joinToString "Producto #$id"
        val porciones = maxOf(1, producto.porciones)
        val unidades = (invitados + porciones - 1) / porciones
        "${producto.nombre} × $unidades"
    }
}
