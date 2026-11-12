package com.grupo9.data.api

import com.grupo9.model.ReservaRemota
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservaApi {

    @GET("api/reservas")
    suspend fun obtenerReservas(): List<ReservaRemota>

    @GET("api/reservas/{id}")
    suspend fun obtenerReserva(@Path("id") id: Long): ReservaRemota

    @POST("api/reservas")
    suspend fun crearReserva(@Body reserva: ReservaRemota): ReservaRemota
}