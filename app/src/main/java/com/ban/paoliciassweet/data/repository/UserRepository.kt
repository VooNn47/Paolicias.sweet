package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.local.dao.UserDao
import com.ban.paoliciassweet.data.local.entity.UserEntity


class UserRepository(private val dao: UserDao) {


    suspend fun registrar(usuario: UserEntity) {
        dao.registerUser(usuario)
    }


    suspend fun buscarPorCorreo(correo: String): UserEntity? {
        return dao.getUserByEmail(correo)
    }
}