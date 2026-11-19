package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.BuildConfig

// cotizacion-service
object RetrofitCotizacion {
    val api: CotizacionApi by lazy {
        crearRetrofit(BuildConfig.COTIZACION_URL).create(CotizacionApi::class.java)
    }
}
