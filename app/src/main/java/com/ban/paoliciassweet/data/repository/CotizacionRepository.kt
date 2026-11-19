package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.api.CotizacionApi
import com.ban.paoliciassweet.data.api.RetrofitCotizacion
import com.ban.paoliciassweet.model.Cotizacion
import com.ban.paoliciassweet.model.CotizacionRequest

// Único que sabe de dónde viene el precio del evento: cotizacion-service (backend)
class CotizacionRepository(private val api: CotizacionApi = RetrofitCotizacion.api) {

    suspend fun cotizar(invitados: Int, productoIds: List<Long>): Cotizacion =
        api.cotizar(CotizacionRequest(invitados, productoIds))
}
