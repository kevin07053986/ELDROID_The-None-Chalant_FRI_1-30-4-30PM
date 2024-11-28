package com.acosta.eldriod.network

import com.acosta.eldriod.calendar.Event
import com.acosta.eldriod.models.Budget
import com.acosta.eldriod.models.Expense
import com.acosta.eldriod.models.Server
import com.acosta.eldriod.models.User
import com.acosta.eldriod.signin.LoginRequest
import com.acosta.eldriod.signin.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.Call

interface ApiService {

    @POST("mock-login") // Updated endpoint
    suspend fun login(@Body loginRequest: LoginRequest): Response<Server<LoginResponse>>

//    @POST("register")
//    suspend fun register(@Body user: com.acosta.eldriod.models.User): Response<Server<Any>>

    @POST("register")
    suspend fun register(@Body user: User): Response<Server<Any>>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Server<Any>>

    @GET("api/budget/{userId}")
    fun getBudget(@Path("userId") userId: String): Call<Budget>

    @PUT("api/budget/{userId}")
    fun updateBudget(@Path("userId") userId: String, @Body budget: Budget): Call<Void>

    @POST("api/expense/{userId}")
    fun addExpense(@Path("userId") userId: String, @Body expense: Expense): Call<Void>
}
