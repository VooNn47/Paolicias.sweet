package com.ban.paoliciassweet.model

import com.google.gson.annotations.SerializedName

// ---- APIs externas (de terceros, no son microservicios propios) ----

// Feriado de Chile según Nager.Date: GET https://date.nager.at/api/v3/PublicHolidays/{año}/CL
data class Feriado(
    val date: String,       // "2026-12-25"
    val localName: String,  // "Navidad"
    val name: String        // "Christmas Day"
)

// Tipo de cambio según ExchangeRate-API: GET https://open.er-api.com/v6/latest/CLP
// rates["USD"] = cuántos dólares vale 1 peso chileno
data class TipoCambio(
    val result: String,
    val rates: Map<String, Double>,
    @SerializedName("time_last_update_utc") val actualizado: String?
)
