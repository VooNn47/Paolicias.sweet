package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.local.dao.ProductoDao
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.local.seed.productosIniciales
import kotlinx.coroutines.flow.Flow


class ProductoRepository(private val dao: ProductoDao) {


    suspend fun cargarProductosIniciales() {
        if (dao.contar() == 0) {
            dao.insertarTodos(productosIniciales)
        }
    }


    fun obtenerTodos(): Flow<List<ProductoEntity>> {
        return dao.obtenerTodos()
    }


    suspend fun obtenerPorId(id: Int): ProductoEntity? {
        return dao.obtenerPorId(id)
    }
}