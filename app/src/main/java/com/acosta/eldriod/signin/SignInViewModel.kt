package com.acosta.eldriod.signin

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitInstance.createService(ApiService::class.java)
    val loginResponse = MutableLiveData<LoginResponse>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()

    fun login(email: String, password: String, sharedPreferences: SharedPreferences) {
        isLoading.value = true

        val loginRequest = LoginRequest(email, password)
        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        loginResponse.value = it
                        if (it.token.isNotEmpty()) { // Check if token is valid
                            val editor = sharedPreferences.edit()
                            editor.putString("userEmail", email)
                            editor.apply()
                        }
                    }
                } else {
                    errorMessage.value = "Login failed: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}

