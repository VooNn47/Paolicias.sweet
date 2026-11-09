package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.local.dao.ReservaDao
import com.ban.paoliciassweet.data.local.entity.ReservaEntity


class ReservaRepository(private val dao: ReservaDao) {


    suspend fun guardar(reserva: ReservaEntity) {
        dao.guardarReserva(reserva)
    }


    suspend fun obtenerPorCliente(correo: String): List<ReservaEntity> {
        return dao.obtenerReservasPorCliente(correo)
    }


    suspend fun adjuntarComprobante(id: Int, ruta: String) {
        dao.adjuntarComprobante(id, ruta)
    }


    suspend fun obtenerTodas(): List<ReservaEntity> {
        return dao.obtenerTodas()
    }


    suspend fun validarAbono(id: Int, fechaPago: String) {
        dao.validarAbono(id, fechaPago)
    }

    
    suspend fun rechazarAbono(id: Int) {
        dao.rechazarAbono(id)
    }
}