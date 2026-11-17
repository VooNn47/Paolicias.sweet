package com.ban.paoliciassweet.model

data class ReservaModel(
    val nombreEvento: String = "",
    val fecha: String = "",
    val horario: String = "",
    val invitados: String = "",
    val observaciones: String = "",
    // Un mensaje de error por campo (null = el campo está bien)
    val nombreEventoError: String? = null,
    val fechaError: String? = null,
    val horarioError: String? = null,
    val invitadosError: String? = null
)