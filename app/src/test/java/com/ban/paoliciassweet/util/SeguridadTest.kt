package com.ban.paoliciassweet.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del cifrado de contraseñas que usa el login local (SQLite)
class SeguridadTest {

    @Test
    fun laClaveNoSeGuardaEnTextoPlano() {
        val guardada = Seguridad.cifrarClave("secreta1")
        assertFalse(guardada.contains("secreta1"))
    }

    @Test
    fun claveCorrecta_esAceptada() {
        val guardada = Seguridad.cifrarClave("secreta1")
        assertTrue(Seguridad.claveCorrecta("secreta1", guardada))
    }

    @Test
    fun claveIncorrecta_esRechazada() {
        val guardada = Seguridad.cifrarClave("secreta1")
        assertFalse(Seguridad.claveCorrecta("Secreta1", guardada))
    }

    @Test
    fun mismaClave_daHashDistinto_porLaSal() {
        assertNotEquals(Seguridad.cifrarClave("secreta1"), Seguridad.cifrarClave("secreta1"))
    }

    @Test
    fun valorGuardadoInvalido_noRompeElLogin() {
        assertFalse(Seguridad.claveCorrecta("secreta1", "texto-plano-antiguo"))
    }
}
