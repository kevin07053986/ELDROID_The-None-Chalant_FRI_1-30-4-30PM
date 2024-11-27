package com.acosta.eldriod.repository

import com.acosta.eldriod.network.ApiService
import retrofit2.Response

class SampleRepository(private val apiService: ApiService) {

    suspend fun ping(): Response<Any> {
        return apiService.ping()
    }
}