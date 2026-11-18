package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.local.dao.UserDao
import com.ban.paoliciassweet.data.local.entity.Roles
import com.ban.paoliciassweet.data.local.entity.UserEntity
import com.ban.paoliciassweet.util.Seguridad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Autenticación local con SQLite (Room) para cliente y administradora.
class UserRepository(private val dao: UserDao) {

    // Devuelve false si el correo ya existe
    suspend fun registrar(nombre: String, correo: String, clave: String): Boolean =
        withContext(Dispatchers.Default) {
            if (dao.getUserByEmail(correo) != null) return@withContext false
            val rol = if (correo == Roles.CORREO_ADMIN) Roles.ADMIN else Roles.CLIENTE
            dao.registerUser(
                UserEntity(name = nombre, email = correo, password = Seguridad.cifrarClave(clave), rol = rol)
            )
            true
        }

    // Devuelve el usuario si el correo y la contraseña coinciden, o null
    suspend fun iniciarSesion(correo: String, clave: String): UserEntity? =
        withContext(Dispatchers.Default) {
            dao.getUserByEmail(correo)?.takeIf { Seguridad.claveCorrecta(clave, it.password) }
        }

    suspend fun buscarPorCorreo(correo: String): UserEntity? {
        return dao.getUserByEmail(correo)
    }
}
