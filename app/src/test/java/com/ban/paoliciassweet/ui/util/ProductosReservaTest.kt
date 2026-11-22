package com.ban.paoliciassweet.ui.util

import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import org.junit.Assert.assertEquals
import org.junit.Test

// Pruebas de cómo se muestran los productos de una reserva (panel admin y Mi Agenda)
class ProductosReservaTest {

    private fun producto(id: Int, nombre: String, porciones: Int) =
        ProductoEntity(id, nombre, "", "Pasteles", 20000, porciones, "")

    private val catalogo = mapOf(
        4 to producto(4, "Kuchen de Frambuesa", 8),
        11 to producto(11, "Pastel de Choclo", 1)
    )

    @Test
    fun idsProductos_leeLaListaDelServidor() {
        assertEquals(listOf(4, 11), idsProductos("4,11"))
        assertEquals(listOf(4, 11), idsProductos(" 4 , 11 "))
        assertEquals(emptyList<Int>(), idsProductos(""))
        assertEquals(emptyList<Int>(), idsProductos(null))
    }

    @Test
    fun textoProductos_calculaLasUnidadesComoLaCotizacion() {
        // 10 invitados y el kuchen rinde 8 -> 2 unidades; el pastel de choclo es individual -> 10
        assertEquals(
            "Kuchen de Frambuesa × 2, Pastel de Choclo × 10",
            textoProductos("4,11", 10, catalogo)
        )
    }

    @Test
    fun textoProductos_sinProductos_esSoloServicio() {
        assertEquals("Sin productos (solo servicio)", textoProductos("", 20, catalogo))
        assertEquals("Sin productos (solo servicio)", textoProductos(null, 20, catalogo))
    }

    @Test
    fun textoProductos_productoFueraDelCatalogo_muestraSuNumero() {
        assertEquals("Producto #99", textoProductos("99", 5, catalogo))
    }
}
