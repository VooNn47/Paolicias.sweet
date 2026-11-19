package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.model.Insumo
import com.ban.paoliciassweet.model.StockRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// insumos-service (inventario de la administradora)
interface InsumoApi {

    @GET("api/insumos")
    suspend fun obtenerInsumos(): List<Insumo>

    @PATCH("api/insumos/{id}/stock")
    suspend fun actualizarStock(@Path("id") id: Long, @Body request: StockRequest): Insumo
}
