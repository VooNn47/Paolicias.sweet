package com.ban.paoliciassweet.model

// Insumo del inventario (insumos-service)
data class Insumo(
    val id: Long,
    val nombre: String,
    val unidad: String,
    val stock: Double
)

// Para ajustar el stock de un insumo
data class StockRequest(val stock: Double)

// Resultado de "Calcular insumos" de una reserva
data class CalculoInsumos(
    val alcanza: Boolean,
    val insumos: List<InsumoNecesario>
) {
    data class InsumoNecesario(
        val insumoId: Long,
        val nombre: String,
        val unidad: String,
        val necesario: Double,
        val stock: Double,
        val faltante: Double
    )
}
