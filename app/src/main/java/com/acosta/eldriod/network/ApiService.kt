package com.acosta.eldriod.network

import com.acosta.eldriod.signin.LoginRequest
import com.acosta.eldriod.signin.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login") // Endpoint to call
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse> // Passing the loginRequest as the body
}
