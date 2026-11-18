package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.model.ProductoRemoto
import retrofit2.http.GET
import retrofit2.http.Path

// catalogo-service
interface ProductoApi {

    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoRemoto>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoRemoto
}
