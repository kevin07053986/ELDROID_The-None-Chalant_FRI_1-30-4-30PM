package com.acosta.eldriod.network

import com.acosta.eldriod.models.Server
import com.acosta.eldriod.models.User
import com.acosta.eldriod.signin.LoginRequest
import com.acosta.eldriod.signin.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("meow")
    suspend fun ping(): Response<Any>

    @POST("mock-login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Server<LoginResponse>>

    @POST("register")
    suspend fun register(@Body user: User): Response<Server<Any>>

    @POST("login") // Endpoint to call
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse> // Passing the loginRequest as the body
}
