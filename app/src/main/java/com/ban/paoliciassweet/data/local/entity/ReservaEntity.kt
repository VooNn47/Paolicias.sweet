package com.ban.paoliciassweet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreEvento: String,
    val fecha: String,
    val horario: String,
    val invitados: String,
    val observaciones: String,
    val correoCliente: String = "",
    val estadoAbono: String = "Pendiente",
    val pagada: Boolean = false,
    val productos: String = "",
    val total: Int = 0,
    val comprobante: String = "",
    val fechaPago: String = ""
)