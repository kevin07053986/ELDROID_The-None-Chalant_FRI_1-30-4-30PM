package com.acosta.eldriod.signin

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.models.User
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitInstance.createService(ApiService::class.java)
    val loginResponse = MutableLiveData<User>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String, sharedPreferences: SharedPreferences) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { serverResponse ->
                        val user = serverResponse.data.user
                        val token = serverResponse.data.token

                        user.id?.let {
                            sharedPreferences.edit()
                                .putString("token", token)
                                .putInt("user_id", it)
                                .putString("user_name", user.name)
                                .putString("user_email", user.email)
                                .putString("user_dob", user.dob)
                                .putString("user_accountType", user.accountType)
                                .apply()
                        }

                        loginResponse.postValue(user)
                    } ?: run {
                        errorMessage.postValue("Empty response from server.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage.postValue("Login failed: ${errorBody ?: response.message()}")
                }
            } catch (e: Exception) {
                //Log.e("SignInViewModel", "Exception during login: ${e.message}")
                errorMessage.postValue("Error: ${e.localizedMessage}")
            }
        }
    }
}