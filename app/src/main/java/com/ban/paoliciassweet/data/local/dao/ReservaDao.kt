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

    @Query("SELECT * FROM reservas ORDER BY id DESC LIMIT 1")
    suspend fun obtenerReservas(): ReservaEntity?

    @Query("SELECT * FROM reservas ORDER BY fecha ASC")
    suspend fun obtenerTodas(): List<ReservaEntity>

    @Query("UPDATE reservas SET estadoAbono = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE reservas SET pagada = 1, estadoAbono = 'Pagado' WHERE id = :id AND pagada = 0")
    suspend fun marcarReservaPagada(id: Int): Int

    @Query("UPDATE reservas SET comprobante = :ruta, estadoAbono = 'En revisión' WHERE id = :id")
    suspend fun adjuntarComprobante(id: Int, ruta: String)
}