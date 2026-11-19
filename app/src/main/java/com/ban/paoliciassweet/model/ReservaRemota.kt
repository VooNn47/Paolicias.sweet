package com.ban.paoliciassweet.model

// Reserva tal como la devuelve reservas-service.
// Fechas "dd/MM/yyyy" y horario "HH:mm". El total y el abono los calcula el servidor.
data class ReservaRemota(
    val id: Long,
    val nombreEvento: String,
    val fecha: String,
    val horario: String,
    val invitados: Int,
    val observaciones: String?,
    val correoCliente: String,
    val estadoAbono: String,
    val productos: String?,
    val tarifaServicio: Int?,
    val total: Int,
    val abono: Int,
    val tieneComprobante: Boolean,
    val fechaPago: String?
)

// Lo que la app envía para crear una reserva (POST /api/reservas)
data class NuevaReserva(
    val nombreEvento: String,
    val fecha: String,
    val horario: String,
    val invitados: Int,
    val observaciones: String,
    val correoCliente: String,
    val productoIds: List<Long>
)

// Desglose de precio antes de reservar (POST /api/cotizacion)
data class CotizacionRequest(val invitados: Int, val productoIds: List<Long>)

data class Cotizacion(
    val invitados: Int,
    val servicio: Int,
    val lineas: List<Linea>,
    val total: Int,
    val abono: Int
) {
    data class Linea(
        val productoId: Long,
        val nombre: String,
        val unidades: Int,
        val precioUnitario: Int,
        val subtotal: Int
    )
}

// Estados del abono (mismos textos que el backend)
object EstadoAbono {
    const val PENDIENTE = "Pendiente"
    const val EN_REVISION = "En revisión"
    const val PAGADO = "Pagado 50%"
    const val COMPLETADO = "Completado"
}
