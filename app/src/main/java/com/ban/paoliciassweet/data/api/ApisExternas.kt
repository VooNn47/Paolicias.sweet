package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.model.Feriado
import com.ban.paoliciassweet.model.TipoCambio
import retrofit2.http.GET
import retrofit2.http.Path

// API externa 1: feriados de Chile (Nager.Date). Gratis, sin registro ni clave.
interface FeriadosApi {

    @GET("api/v3/PublicHolidays/{anio}/CL")
    suspend fun feriados(@Path("anio") anio: Int): List<Feriado>
}

// API externa 2: tipo de cambio (ExchangeRate-API, acceso abierto). Gratis, sin registro ni clave.
interface CambioApi {

    @GET("v6/latest/CLP")
    suspend fun desdePesos(): TipoCambio
}

// Clientes Retrofit de las APIs externas (usan el mismo OkHttp que los microservicios)
object RetrofitExternas {
    private const val URL_FERIADOS = "https://date.nager.at/"
    private const val URL_CAMBIO = "https://open.er-api.com/"

    val feriados: FeriadosApi by lazy { crearRetrofit(URL_FERIADOS).create(FeriadosApi::class.java) }
    val cambio: CambioApi by lazy { crearRetrofit(URL_CAMBIO).create(CambioApi::class.java) }
}
