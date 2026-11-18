package com.ban.paoliciassweet.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Login y registro en SQLite (Room), para los dos roles.
// password guarda "sal:hash" (PBKDF2), nunca la contraseña en texto plano.
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val rol: String = Roles.CLIENTE
)

object Roles {
    const val CLIENTE = "CLIENTE"
    const val ADMIN = "ADMIN"

    // Correo con el que se registra la administradora
    const val CORREO_ADMIN = "admin@paolicias.cl"
}
