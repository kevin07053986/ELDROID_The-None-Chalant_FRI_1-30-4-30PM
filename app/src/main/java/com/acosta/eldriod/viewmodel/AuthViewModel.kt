package com.acosta.eldriod.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.models.Server
import com.acosta.eldriod.models.User
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import com.acosta.eldriod.repository.AuthRepository
import com.acosta.eldriod.signin.LoginRequest
import com.acosta.eldriod.signin.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel: ViewModel() {
    private val authRepository = AuthRepository(RetrofitInstance.createService(ApiService::class.java))

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() =  _loginResponse

    fun login(loginRequest: LoginRequest) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response: Response<Server<LoginResponse>> = authRepository.login(loginRequest)
                if(response.isSuccessful) {
                    Log.d("DATA FROM LOGIN", response.body().toString())
                    _loginResponse.postValue(response.body()?.data)
                } else {
                    Log.d("SERVER LOGIN", response.toString())
                }
            }
        } catch(e: Exception) {
            Log.d("ERROR LOGIN", e.toString())
        }
    }

    fun register(user: User) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response: Response<Server<Any>> = authRepository.register(user)
                if(response.isSuccessful) {
                    Log.d("REGISTER SUCCESS", response.body().toString())
                }else {
                    Log.d("ERROR REGISTER USER", response.toString())
                }
            }
        } catch (e: Exception) {
            Log.d("ERROR REGISTER", e.toString())
        }
    }
}