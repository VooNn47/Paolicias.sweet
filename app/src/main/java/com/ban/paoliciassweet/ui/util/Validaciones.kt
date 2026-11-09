package com.ban.paoliciassweet.ui.util

import android.util.Patterns

// Cada función devuelve el mensaje de error, o null si el dato es válido.
// Están aquí (y no dentro de cada ViewModel) para reutilizarlas en login, registro y reserva.

fun validarNombre(nombre: String): String? {
    val limpio = nombre.trim()
    return when {
        limpio.isEmpty() -> "Ingresa tu nombre completo"
        limpio.length < 3 -> "El nombre debe tener al menos 3 letras"
        !limpio.all { it.isLetter() || it.isWhitespace() } -> "El nombre solo puede tener letras"
        else -> null
    }
}

fun validarCorreo(correo: String): String? {
    val limpio = correo.trim()
    return when {
        limpio.isEmpty() -> "Ingresa tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(limpio).matches() -> "Correo no válido (ej: nombre@correo.cl)"
        else -> null
    }
}

fun validarClave(clave: String): String? {
    return when {
        clave.isEmpty() -> "Ingresa una contraseña"
        clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        else -> null
    }
}

fun validarRepetirClave(clave: String, repetida: String): String? {
    return when {
        repetida.isEmpty() -> "Repite la contraseña"
        clave != repetida -> "Las contraseñas no coinciden"
        else -> null
    }
}

// ---- Formulario de reserva ----

fun validarNombreEvento(nombre: String): String? {
    val limpio = nombre.trim()
    return when {
        limpio.isEmpty() -> "Ingresa el nombre del evento"
        limpio.length < 3 -> "El nombre del evento debe tener al menos 3 letras"
        else -> null
    }
}

fun validarFecha(fecha: String): String? {
    return if (fecha.isBlank()) "Elige la fecha del evento" else null
}

fun validarHorario(horario: String): String? {
    return if (horario.isBlank()) "Elige el horario del evento" else null
}

fun validarInvitados(invitados: String): String? {
    val limpio = invitados.trim()
    val cantidad = limpio.toIntOrNull()
    return when {
        limpio.isEmpty() -> "Ingresa la cantidad de invitados"
        cantidad == null || !limpio.all { it.isDigit() } -> "Ingresa solo números"
        cantidad < 1 || cantidad > 100 -> "Los invitados deben ser entre 1 y 100"
        else -> null
    }
}