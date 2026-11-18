package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.api.ReservaApi
import com.ban.paoliciassweet.data.api.RetrofitReserva
import com.ban.paoliciassweet.model.Cotizacion
import com.ban.paoliciassweet.model.CotizacionRequest
import com.ban.paoliciassweet.model.NuevaReserva
import com.ban.paoliciassweet.model.ReservaRemota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// Único que sabe de dónde vienen las reservas: reservas-service (backend).
// Así el cliente y la administradora ven los mismos datos desde cualquier teléfono.
class ReservaRepository(private val api: ReservaApi = RetrofitReserva.api) {

    suspend fun crear(reserva: NuevaReserva): ReservaRemota = api.crearReserva(reserva)

    suspend fun cotizar(invitados: Int, productoIds: List<Long>): Cotizacion =
        api.cotizar(CotizacionRequest(invitados, productoIds))

    suspend fun misReservas(correo: String): List<ReservaRemota> = api.obtenerReservas(correo = correo)

    // estado = null o "Todas" -> todas
    suspend fun todas(estado: String?): List<ReservaRemota> =
        api.obtenerReservas(estado = estado?.takeIf { it != "Todas" })

    suspend fun subirComprobante(id: Long, correo: String, jpeg: ByteArray): ReservaRemota {
        val parte = MultipartBody.Part.createFormData(
            "archivo", "comprobante_$id.jpg", jpeg.toRequestBody("image/jpeg".toMediaType())
        )
        return api.subirComprobante(id, correo, parte)
    }

    suspend fun comprobante(id: Long): ByteArray = withContext(Dispatchers.IO) {
        api.obtenerComprobante(id).use { it.bytes() }
    }

    suspend fun validarAbono(id: Long): ReservaRemota = api.validarAbono(id)

    suspend fun rechazarAbono(id: Long): ReservaRemota = api.rechazarAbono(id)

    suspend fun completar(id: Long): ReservaRemota = api.completarReserva(id)
}
