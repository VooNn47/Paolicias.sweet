package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.BuildConfig

// reservas-service
object RetrofitReserva {
    val api: ReservaApi by lazy {
        crearRetrofit(BuildConfig.RESERVAS_URL).create(ReservaApi::class.java)
    }
}
