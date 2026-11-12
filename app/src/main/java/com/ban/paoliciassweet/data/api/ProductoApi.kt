package com.grupo9.data.api

import com.grupo9.model.ProductoRemoto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductoApi {

    @GET("api/productos")
    suspend fun obtenerProductos(): List<ProductoRemoto>

    @GET("api/productos/{id}")
    suspend fun obtenerProducto(@Path("id") id: Long): ProductoRemoto

    @POST("api/productos")
    suspend fun crearProducto(@Body producto: ProductoRemoto): ProductoRemoto
}