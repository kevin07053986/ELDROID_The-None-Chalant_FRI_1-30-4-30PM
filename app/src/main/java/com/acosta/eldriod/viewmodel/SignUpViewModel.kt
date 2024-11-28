package com.acosta.eldriod.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.models.Server
import com.acosta.eldriod.models.User
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    val registrationStatus = MutableLiveData<String>()

    private val apiService = RetrofitInstance.createService(ApiService::class.java)

    fun registerUser(user: User) {
        viewModelScope.launch {
            try {
                val response: Response<Server<Any>> = apiService.register(user)

                if (response.isSuccessful) {
                    registrationStatus.postValue("Registration Successful")
                } else {
                    registrationStatus.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                registrationStatus.postValue("Error: ${e.localizedMessage}")
            }
        }
    }
}
