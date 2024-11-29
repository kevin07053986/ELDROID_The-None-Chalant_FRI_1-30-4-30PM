package com.acosta.eldriod.network

import com.acosta.eldriod.budget.BudgetRequest
import com.acosta.eldriod.budget.BudgetResponse
import com.acosta.eldriod.models.Budget
import com.acosta.eldriod.models.Event
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
import retrofit2.http.DELETE

interface ApiService {

    @POST("mock-login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Server<LoginResponse>>

//    @POST("register")
//    suspend fun register(@Body user: com.acosta.eldriod.models.User): Response<Server<Any>>

    @POST("register")
    suspend fun register(@Body user: User): Response<Server<Any>>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Server<Any>>

    @POST("budgets")
    suspend fun storeBudget(@Body budgetRequest: BudgetRequest): Response<BudgetResponse>
    @GET("budget/{userId}")
    suspend fun getBudget(@Path("userId") userId: String): Response<BudgetResponse>


    @GET("events")
    suspend fun getAllEvents(): List<Event>
    @POST("events")
    suspend fun createEvent(@Body event: Event): Event
    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: Int, @Body event: Event): Event
    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int): Response<Unit>
}
