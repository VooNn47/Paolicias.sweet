package com.ban.paoliciassweet.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Cifrado de contraseñas con PBKDF2 (incluido en Android, no necesita librerías).
// Se guarda "sal:hash" en Base64. Cada usuario tiene una sal distinta.
object Seguridad {

    private const val ITERACIONES = 120_000
    private const val LARGO_BITS = 256

    fun cifrarClave(clave: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = derivar(clave, sal)
        val b64 = Base64.getEncoder()
        return b64.encodeToString(sal) + ":" + b64.encodeToString(hash)
    }

    fun claveCorrecta(clave: String, guardada: String): Boolean {
        val partes = guardada.split(":")
        if (partes.size != 2) return false
        return try {
            val sal = Base64.getDecoder().decode(partes[0])
            val esperado = Base64.getDecoder().decode(partes[1])
            // Comparación en tiempo constante
            MessageDigest.isEqual(derivar(clave, sal), esperado)
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun derivar(clave: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
