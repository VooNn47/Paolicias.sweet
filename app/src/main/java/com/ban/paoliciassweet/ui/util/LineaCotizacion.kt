package com.ban.paoliciassweet.ui.util

import com.ban.paoliciassweet.data.local.entity.ProductoEntity

data class LineaCotizacion(val nombre: String, val unidades: Int, val subtotal: Int)

data class Cotizacion(
    val servicio: Int,
    val lineas: List<LineaCotizacion>,
    val total: Int
) {
    val abono: Int get() = total / 2
}

private val TARIFAS = listOf(
    30000, 50000, 80000, 100000, 120000,
    140000, 160000, 180000, 200000, 220000
)

// 1-10 -> 30.000, 11-20 -> 50.000, ... 91-100 -> 220.000
fun tarifaServicio(invitados: Int): Int {
    if (invitados < 1 || invitados > 100) return 0
    return TARIFAS[(invitados - 1) / 10]
}

fun calcularCotizacion(invitados: Int, productos: List<ProductoEntity>): Cotizacion {
    val lineas = productos.map { p ->
        val porciones = maxOf(1, p.porciones)
        val unidades = (invitados + porciones - 1) / porciones
        LineaCotizacion(p.nombre, unidades, unidades * p.precio)
    }
    val servicio = tarifaServicio(invitados)
    return Cotizacion(servicio, lineas, servicio + lineas.sumOf { it.subtotal })
}