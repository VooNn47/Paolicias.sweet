package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.api.CambioApi
import com.ban.paoliciassweet.data.api.FeriadosApi
import com.ban.paoliciassweet.data.api.RetrofitExternas
import com.ban.paoliciassweet.model.Feriado
import com.ban.paoliciassweet.util.Externas
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// Único que sabe de dónde vienen los datos externos (feriados y dólar).
// Guarda las respuestas en memoria para no llamar a la API cada vez.
// Si la API externa falla, devuelve null y la app sigue funcionando sin ese dato.
object ExternasRepository {

    private val feriadosApi: FeriadosApi = RetrofitExternas.feriados
    private val cambioApi: CambioApi = RetrofitExternas.cambio

    private val feriadosPorAnio = mutableMapOf<Int, List<Feriado>>()
    private var tasaDolar: Double? = null
    private val mutex = Mutex()

    // Feriados del año (desde la caché si ya se pidieron)
    suspend fun feriados(anio: Int): List<Feriado>? = mutex.withLock {
        feriadosPorAnio[anio] ?: try {
            feriadosApi.feriados(anio).also { feriadosPorAnio[anio] = it }
        } catch (e: Exception) {
            null
        }
    }

    // Feriado de una fecha "dd/MM/yyyy", o null si no es feriado (o si no hay conexión)
    suspend fun feriadoDe(fecha: String): Feriado? {
        val anio = Externas.anio(fecha) ?: return null
        val lista = feriados(anio) ?: return null
        return Externas.buscarFeriado(lista, fecha)
    }

    // Dólares por 1 peso chileno (tasa del día), o null si la API no responde
    suspend fun tasaDolar(): Double? = mutex.withLock {
        tasaDolar ?: try {
            cambioApi.desdePesos().rates["USD"]?.also { tasaDolar = it }
        } catch (e: Exception) {
            null
        }
    }
}
