package com.ban.paoliciassweet.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Convierte un error de red en un mensaje para mostrar al usuario.
// Si el backend respondió con { "mensaje": "..." }, se usa ese texto.
fun mensajeDeError(e: Throwable, porDefecto: String = "Ocurrió un error, intenta de nuevo"): String =
    when (e) {
        is IOException -> "Sin conexión con el servidor. Revisa tu internet e intenta de nuevo."
        is HttpException -> mensajeDelServidor(e) ?: "El servidor respondió con un error (${e.code()})"
        else -> porDefecto
    }

private fun mensajeDelServidor(e: HttpException): String? = try {
    val cuerpo = e.response()?.errorBody()?.string().orEmpty()
    val json = JSONObject(cuerpo)
    val errores = json.optJSONObject("errores")
    // Si hay errores por campo, se muestra el primero (es más útil que "Hay datos inválidos")
    if (errores != null && errores.length() > 0) {
        errores.getString(errores.keys().next())
    } else {
        json.optString("mensaje").takeIf { it.isNotBlank() }
    }
} catch (ex: Exception) {
    null
}
