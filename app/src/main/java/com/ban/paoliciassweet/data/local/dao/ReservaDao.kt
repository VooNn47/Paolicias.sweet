package com.ban.paoliciassweet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ban.paoliciassweet.data.local.entity.ReservaEntity

@Dao
interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun guardarReserva(reserva: ReservaEntity)


    @Query(
        "SELECT * FROM reservas WHERE correoCliente = :correo " +
                "ORDER BY substr(fecha, 7, 4) || substr(fecha, 4, 2) || substr(fecha, 1, 2) ASC, horario ASC"
    )
    suspend fun obtenerReservasPorCliente(correo: String): List<ReservaEntity>


    @Query(
        "SELECT * FROM reservas " +
                "ORDER BY substr(fecha, 7, 4) || substr(fecha, 4, 2) || substr(fecha, 1, 2) ASC, horario ASC"
    )
    suspend fun obtenerTodas(): List<ReservaEntity>

    @Query("UPDATE reservas SET estadoAbono = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE reservas SET comprobante = :ruta, estadoAbono = 'En revisión' WHERE id = :id")
    suspend fun adjuntarComprobante(id: Int, ruta: String)


    @Query("UPDATE reservas SET estadoAbono = 'Pagado 50%', fechaPago = :fechaPago WHERE id = :id")
    suspend fun validarAbono(id: Int, fechaPago: String)


    @Query("UPDATE reservas SET estadoAbono = 'Pendiente', comprobante = '', fechaPago = '' WHERE id = :id")
    suspend fun rechazarAbono(id: Int)
}