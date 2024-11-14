package com.acosta.eldriod.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://127.0.0.1:8000/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Generic function to create a service class
    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
