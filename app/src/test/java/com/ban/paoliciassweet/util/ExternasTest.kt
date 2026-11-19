package com.ban.paoliciassweet.util

import com.ban.paoliciassweet.model.Feriado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la lógica de las APIs externas (feriados y dólar), sin conexión a internet
class ExternasTest {

    private val feriados2026 = listOf(
        Feriado("2026-09-18", "Independencia Nacional", "Independence Day"),
        Feriado("2026-12-25", "Navidad", "Christmas Day")
    )

    @Test
    fun aFechaIso_convierteElFormatoDeLaApp() {
        assertEquals("2026-12-25", Externas.aFechaIso("25/12/2026"))
    }

    @Test
    fun aFechaIso_textoInvalido_esNull() {
        assertNull(Externas.aFechaIso("2026-12-25"))
        assertNull(Externas.aFechaIso("5/1/2026"))
        assertNull(Externas.aFechaIso("aa/bb/cccc"))
        assertNull(Externas.aFechaIso(""))
    }

    @Test
    fun anio_seObtieneDeLaFecha() {
        assertEquals(2027, Externas.anio("01/01/2027"))
    }

    @Test
    fun buscarFeriado_encuentraElDelDia() {
        assertEquals("Navidad", Externas.buscarFeriado(feriados2026, "25/12/2026")?.localName)
        assertEquals("Independencia Nacional", Externas.buscarFeriado(feriados2026, "18/09/2026")?.localName)
    }

    @Test
    fun buscarFeriado_diaHabil_esNull() {
        assertNull(Externas.buscarFeriado(feriados2026, "24/12/2026"))
    }

    @Test
    fun aDolares_usaLaTasaDelDia() {
        // Con 1 peso = 0,001 dólares, $215.000 son US$ 215
        assertEquals(215.0, Externas.aDolares(215000, 0.001), 0.0001)
    }

    @Test
    fun formatoDolar_usaComaDecimal() {
        assertEquals("US$ 228,50", Externas.formatoDolar(228.5))
    }

    @Test
    fun pesosPorDolar_esElInversoDeLaTasa() {
        assertEquals(1000, Externas.pesosPorDolar(0.001))
        assertEquals(0, Externas.pesosPorDolar(0.0))
    }
}
