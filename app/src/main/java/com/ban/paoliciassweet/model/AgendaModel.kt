package com.ban.paoliciassweet.model

import com.ban.paoliciassweet.data.local.entity.ReservaEntity

data class AgendaModel(
    val reservas: List<ReservaEntity> = emptyList()
)