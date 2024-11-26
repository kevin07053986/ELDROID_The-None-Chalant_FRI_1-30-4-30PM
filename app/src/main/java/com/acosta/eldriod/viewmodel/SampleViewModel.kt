package com.acosta.eldriod.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import com.acosta.eldriod.repository.SampleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SampleViewModel: ViewModel() {
    private val sampleRepository = SampleRepository(RetrofitInstance.createService(ApiService::class.java))

    fun ping() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val res: Response<Any> = sampleRepository.ping()
                 if( res.isSuccessful) {
                     Log.d("SUCCESS", res.toString())
                 } else {
                     Log.d("ERROR PNG", res.toString())
                 }
            } catch (e: Exception) {
                Log.d("ERROR AT PPING", e.toString())
            }
        }
    }
}