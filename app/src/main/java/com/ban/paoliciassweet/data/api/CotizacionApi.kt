package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.model.Cotizacion
import com.ban.paoliciassweet.model.CotizacionRequest
import retrofit2.http.Body
import retrofit2.http.POST

// cotizacion-service
interface CotizacionApi {

    // Desglose del precio (tarifa + productos + total + abono) antes de reservar
    @POST("api/cotizacion")
    suspend fun cotizar(@Body request: CotizacionRequest): Cotizacion
}
