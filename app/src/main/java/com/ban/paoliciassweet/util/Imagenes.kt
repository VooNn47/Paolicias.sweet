package com.ban.paoliciassweet.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object Imagenes {

    private const val LADO_MAXIMO = 1600

    // Lee la foto (cámara o galería), la achica y la devuelve como JPEG.
    // Una foto de cámara puede pesar 5+ MB; así queda en ~300 KB y sube rápido.
    suspend fun comprimirComprobante(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val bitmap = leerReducida(context, uri)

        ByteArrayOutputStream().use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, salida)
            bitmap.recycle()
            salida.toByteArray()
        }
    }

    private fun leerReducida(context: Context, uri: Uri): Bitmap {
        val resolver = context.contentResolver
        // Android 9+: ImageDecoder respeta la orientación EXIF (una foto vertical no queda girada)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val fuente = ImageDecoder.createSource(resolver, uri)
            return ImageDecoder.decodeBitmap(fuente) { decoder, info, _ ->
                decoder.setTargetSampleSize(calcularMuestreo(info.size.width, info.size.height, LADO_MAXIMO))
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
        val opciones = BitmapFactory.Options().apply {
            inSampleSize = calcularMuestreo(limites.outWidth, limites.outHeight, LADO_MAXIMO)
        }
        return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
            ?: throw IllegalArgumentException("No se pudo leer la imagen")
    }

    // Miniatura liviana para mostrar el comprobante en una tarjeta
    suspend fun miniatura(bytes: ByteArray): ImageBitmap? = withContext(Dispatchers.Default) {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
        val opciones = BitmapFactory.Options().apply {
            inSampleSize = calcularMuestreo(limites.outWidth, limites.outHeight, 800)
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opciones)?.asImageBitmap()
    }

    private fun calcularMuestreo(ancho: Int, alto: Int, maximo: Int): Int {
        var muestreo = 1
        while (ancho / (muestreo * 2) >= maximo || alto / (muestreo * 2) >= maximo) {
            muestreo *= 2
        }
        return muestreo
    }
}
