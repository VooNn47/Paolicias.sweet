package com.ban.paoliciassweet.util

import com.ban.paoliciassweet.model.Feriado
import java.text.NumberFormat
import java.util.Locale

// Lógica pura de las APIs externas (sin Android): se prueba con pruebas unitarias.
object Externas {

    // "25/12/2026" -> "2026-12-25"  (formato de Nager.Date). null si el texto no es una fecha válida.
    fun aFechaIso(fecha: String): String? {
        val partes = fecha.trim().split("/")
        if (partes.size != 3) return null
        val (dia, mes, anio) = partes
        if (dia.length != 2 || mes.length != 2 || anio.length != 4) return null
        if (!(dia + mes + anio).all { it.isDigit() }) return null
        return "$anio-$mes-$dia"
    }

    // Año de una fecha "dd/MM/yyyy" (para pedir los feriados de ese año)
    fun anio(fecha: String): Int? = aFechaIso(fecha)?.take(4)?.toIntOrNull()

    // Feriado que cae en esa fecha "dd/MM/yyyy", o null si es día hábil
    fun buscarFeriado(feriados: List<Feriado>, fecha: String): Feriado? {
        val iso = aFechaIso(fecha) ?: return null
        return feriados.firstOrNull { it.date == iso }
    }

    // Pesos -> dólares con la tasa del día (rates["USD"] = dólares por 1 peso)
    fun aDolares(pesos: Int, tasaUsdPorPeso: Double): Double = pesos * tasaUsdPorPeso

    // 228.5 -> "US$ 228,50"
    fun formatoDolar(valor: Double): String =
        "US$ " + NumberFormat.getInstance(Locale.forLanguageTag("es-CL")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }.format(valor)

    // Cuántos pesos vale 1 dólar (para mostrar "1 USD = $993")
    fun pesosPorDolar(tasaUsdPorPeso: Double): Int =
        if (tasaUsdPorPeso <= 0) 0 else Math.round(1 / tasaUsdPorPeso).toInt()
}
