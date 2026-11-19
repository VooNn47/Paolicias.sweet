package com.ban.paoliciassweet.data.repository

import com.ban.paoliciassweet.data.api.InsumoApi
import com.ban.paoliciassweet.data.api.RetrofitInsumos
import com.ban.paoliciassweet.model.Insumo
import com.ban.paoliciassweet.model.StockRequest

// Único que sabe de dónde viene el inventario: insumos-service (backend)
class InsumoRepository(private val api: InsumoApi = RetrofitInsumos.api) {

    suspend fun listar(): List<Insumo> = api.obtenerInsumos()

    suspend fun actualizarStock(id: Long, stock: Double): Insumo = api.actualizarStock(id, StockRequest(stock))
}
