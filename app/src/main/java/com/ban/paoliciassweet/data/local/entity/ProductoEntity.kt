package com.ban.paoliciassweet.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Int,
    val porciones: Int,
    val imagen: String
)
