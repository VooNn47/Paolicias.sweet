package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.model.CalculoInsumos
import com.ban.paoliciassweet.model.NuevaReserva
import com.ban.paoliciassweet.model.ReservaRemota
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// reservas-service
interface ReservaApi {

    // Mi Agenda: ?correo=...   Panel admin: ?estado=...   (sin filtros = todas)
    @GET("api/reservas")
    suspend fun obtenerReservas(
        @Query("correo") correo: String? = null,
        @Query("estado") estado: String? = null
    ): List<ReservaRemota>

    @POST("api/reservas")
    suspend fun crearReserva(@Body reserva: NuevaReserva): ReservaRemota

    @Multipart
    @POST("api/reservas/{id}/comprobante")
    suspend fun subirComprobante(
        @Path("id") id: Long,
        @Query("correo") correo: String,
        @Part archivo: MultipartBody.Part
    ): ReservaRemota

    @GET("api/reservas/{id}/comprobante")
    suspend fun obtenerComprobante(@Path("id") id: Long): ResponseBody

    // Botón "Calcular insumos" del panel admin
    @GET("api/reservas/{id}/insumos")
    suspend fun calcularInsumos(@Path("id") id: Long): CalculoInsumos

    @PATCH("api/reservas/{id}/validar")
    suspend fun validarAbono(@Path("id") id: Long): ReservaRemota

    @PATCH("api/reservas/{id}/rechazar")
    suspend fun rechazarAbono(@Path("id") id: Long): ReservaRemota

    @PATCH("api/reservas/{id}/completar")
    suspend fun completarReserva(@Path("id") id: Long): ReservaRemota

    // El cliente elimina su reserva (el backend borra también el comprobante)
    @DELETE("api/reservas/{id}")
    suspend fun eliminarReserva(@Path("id") id: Long)
}
