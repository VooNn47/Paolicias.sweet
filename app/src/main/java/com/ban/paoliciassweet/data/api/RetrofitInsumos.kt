package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.BuildConfig

// insumos-service
object RetrofitInsumos {
    val api: InsumoApi by lazy {
        crearRetrofit(BuildConfig.INSUMOS_URL).create(InsumoApi::class.java)
    }
}
