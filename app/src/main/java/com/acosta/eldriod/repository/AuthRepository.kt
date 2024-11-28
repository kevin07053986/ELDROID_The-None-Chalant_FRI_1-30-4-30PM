package com.acosta.eldriod.repository

import com.acosta.eldriod.models.Server
import com.acosta.eldriod.models.User
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.signin.LoginRequest
import com.acosta.eldriod.signin.LoginResponse
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): Response<Server<LoginResponse>> {
        return apiService.login(loginRequest)
    }

    suspend fun register(user: User): Response<Server<Any>> {
        return apiService.register(user)
    }
}
