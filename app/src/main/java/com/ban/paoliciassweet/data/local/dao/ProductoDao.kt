package com.ban.paoliciassweet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

// Copia local del catálogo: se llena con lo que llega del backend y sirve sin conexión
@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY categoria, nombre")
    fun obtenerTodos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ProductoEntity?

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<ProductoEntity>)

    @Query("DELETE FROM productos")
    suspend fun borrarTodos()

    // Reemplaza el catálogo local por el del backend (todo o nada)
    @Transaction
    suspend fun reemplazarTodos(productos: List<ProductoEntity>) {
        borrarTodos()
        insertarTodos(productos)
    }
}
