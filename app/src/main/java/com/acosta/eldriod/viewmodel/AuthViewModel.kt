package com.acosta.eldriod.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun login(loginRequest: LoginRequest) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val res: Response<LoginResponse> = authRepository.login(loginRequest)
                if(res.isSuccessful) {
                    Log.d("DATA FROM LOGIN", res.body().toString())
                } else {
                    Log.d("SERVER LOGIN", res.toString())
                }
            }
        } catch(e: Exception) {
            Log.d("ERROR LOGIN", e.toString())
        }
    }
}