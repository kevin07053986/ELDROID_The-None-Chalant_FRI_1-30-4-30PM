package com.acosta.eldriod.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acosta.eldriod.models.Budget
import com.acosta.eldriod.models.Expense
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetViewModel : ViewModel() {

    private val _budget = MutableLiveData<Budget>()
    val budget: LiveData<Budget> get() = _budget

    private val _expenseList = MutableLiveData<List<Expense>>()
    val expenseList: LiveData<List<Expense>> get() = _expenseList

    // Create ApiService instance using RetrofitInstance
    private val apiService: ApiService by lazy {
        RetrofitInstance.createService(ApiService::class.java)
    }

    fun fetchBudget(userId: String) {
        apiService.getBudget(userId).enqueue(object : Callback<Budget> {
            override fun onResponse(call: Call<Budget>, response: Response<Budget>) {
                if (response.isSuccessful) {
                    _budget.value = response.body()
                } else {
                    Log.e("BudgetViewModel", "Error fetching budget: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Budget>, t: Throwable) {
                Log.e("BudgetViewModel", "Network error: ${t.message}")
            }
        })
    }

    fun updateBudget(userId: String, budget: Budget) {
        apiService.updateBudget(userId, budget).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _budget.value = budget
                } else {
                    Log.e("BudgetViewModel", "Error updating budget: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BudgetViewModel", "Network error: ${t.message}")
            }
        })
    }

    fun addExpense(userId: String, expense: Expense) {
        apiService.addExpense(userId, expense).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchBudget(userId) // Refresh the budget after adding expense
                } else {
                    Log.e("BudgetViewModel", "Error adding expense: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BudgetViewModel", "Network error: ${t.message}")
            }
        })
    }
}
