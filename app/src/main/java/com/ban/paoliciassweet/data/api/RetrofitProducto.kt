package com.ban.paoliciassweet.data.api

import com.ban.paoliciassweet.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Cliente HTTP compartido por los dos microservicios
internal val clienteHttp: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS) // subir el comprobante puede tardar
        .apply {
            // El detalle de las peticiones solo se muestra en debug, nunca en el APK firmado
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
            }
        }
        .build()
}

internal fun crearRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(clienteHttp)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// catalogo-service (la URL sale de BuildConfig: debug = emulador, release = EC2 con HTTPS)
object RetrofitProducto {
    val api: ProductoApi by lazy {
        crearRetrofit(BuildConfig.CATALOGO_URL).create(ProductoApi::class.java)
    }
}
