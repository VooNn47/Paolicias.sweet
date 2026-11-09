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

    // Solo las reservas del cliente con sesión iniciada (WHERE correoCliente = :correo),
    // ordenadas por fecha real del evento y luego por horario
    @Query(
        "SELECT * FROM reservas WHERE correoCliente = :correo " +
                "ORDER BY substr(fecha, 7, 4) || substr(fecha, 4, 2) || substr(fecha, 1, 2) ASC, horario ASC"
    )
    suspend fun obtenerReservasPorCliente(correo: String): List<ReservaEntity>

    // La fecha se guarda como texto "dd/MM/yyyy". Ordenar ese texto tal cual
    // ordena por día primero, así que la reordenamos como yyyyMMdd con substr
    // (substr(texto, posición, largo)) y desempatamos por horario.
    @Query(
        "SELECT * FROM reservas " +
                "ORDER BY substr(fecha, 7, 4) || substr(fecha, 4, 2) || substr(fecha, 1, 2) ASC, horario ASC"
    )
    suspend fun obtenerTodas(): List<ReservaEntity>

    @Query("UPDATE reservas SET estadoAbono = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE reservas SET pagada = 1, estadoAbono = 'Pagado' WHERE id = :id AND pagada = 0")
    suspend fun marcarReservaPagada(id: Int): Int

    @Query("UPDATE reservas SET comprobante = :ruta, estadoAbono = 'En revisión' WHERE id = :id")
    suspend fun adjuntarComprobante(id: Int, ruta: String)

    // La administradora acepta el comprobante: el abono queda "Pagado 50%" y se guarda la fecha
    @Query("UPDATE reservas SET estadoAbono = 'Pagado 50%', fechaPago = :fechaPago WHERE id = :id")
    suspend fun validarAbono(id: Int, fechaPago: String)

    // La administradora rechaza el comprobante: vuelve a "Pendiente" y se borra la ruta
    // para que el cliente tenga que adjuntar uno nuevo
    @Query("UPDATE reservas SET estadoAbono = 'Pendiente', comprobante = '', fechaPago = '' WHERE id = :id")
    suspend fun rechazarAbono(id: Int)
}