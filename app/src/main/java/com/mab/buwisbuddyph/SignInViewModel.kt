package com.mab.buwisbuddyph.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mab.buwisbuddyph.model.User
import com.mab.buwisbuddyph.repository.UserRepository
import kotlinx.coroutines.launch

class SignInViewModel(private val userRepository: UserRepository) : ViewModel() {
    val userLiveData = MutableLiveData<User?>()
    val errorLiveData = MutableLiveData<String>()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            if (result.isSuccess) {
                userLiveData.postValue(result.getOrNull())
            } else {
                errorLiveData.postValue(result.exceptionOrNull()?.message)
            }
        }
    }
}
