package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.api.ProductoApi
import com.ban.paoliciassweet.data.api.RetrofitProducto
import com.ban.paoliciassweet.data.local.dao.ProductoDao
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.data.local.seed.productosIniciales
import kotlinx.coroutines.flow.Flow

// Único que sabe de dónde vienen los productos:
// el backend (catalogo-service) es la fuente de verdad y Room guarda una copia para usar sin conexión.
class ProductoRepository(
    private val dao: ProductoDao,
    private val api: ProductoApi = RetrofitProducto.api
) {

    // La pantalla observa Room: cuando llega el catálogo del backend, la lista se actualiza sola
    fun obtenerTodos(): Flow<List<ProductoEntity>> = dao.obtenerTodos()

    // Trae el catálogo del backend y lo copia en Room.
    // Si no hay conexión y Room está vacía, carga los productos de ejemplo. Devuelve true si hubo conexión.
    suspend fun sincronizar(): Boolean {
        return try {
            val remotos = api.obtenerProductos()
            dao.reemplazarTodos(remotos.map { it.aEntidad() })
            true
        } catch (e: Exception) {
            if (dao.contar() == 0) dao.insertarTodos(productosIniciales)
            false
        }
    }

    suspend fun obtenerPorId(id: Int): ProductoEntity? {
        dao.obtenerPorId(id)?.let { return it }
        return try {
            api.obtenerProducto(id.toLong()).aEntidad().also { dao.insertarTodos(listOf(it)) }
        } catch (e: Exception) {
            null
        }
    }
}
