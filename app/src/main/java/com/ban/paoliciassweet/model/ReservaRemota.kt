package com.grupo9.model

data class ReservaRemota(
    val id: Long? = null,
    val nombreEvento: String,
    val fecha: String,
    val horario: String,
    val invitados: String,
    val observaciones: String = "",
    val correoCliente: String = "",
    val estadoAbono: String = "Pendiente",
    val productos: String = "",
    val total: Int = 0,
    val comprobante: String = "",
    val fechaPago: String = ""
)