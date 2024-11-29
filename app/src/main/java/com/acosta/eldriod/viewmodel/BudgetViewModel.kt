package com.acosta.eldriod.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acosta.eldriod.budget.BudgetRequest
import com.acosta.eldriod.budget.BudgetResponse
import com.acosta.eldriod.models.Expense
import com.acosta.eldriod.network.RetrofitInstance
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel : ViewModel() {

    private val apiService = RetrofitInstance.createService(ApiService::class.java)
    private val budgetRepository = BudgetRepository(apiService)

    val budgetResponse = MutableLiveData<BudgetResponse>()
    val errorMessage = MutableLiveData<String>()
    val remainingBudget = MutableLiveData<Double>()
    val expenseList = MutableLiveData<List<Expense>>(emptyList())

    fun postBudget(budgetRequest: BudgetRequest) {
        viewModelScope.launch {
            try {
                val result = budgetRepository.storeBudget(budgetRequest)
                result.onSuccess { response ->
                    budgetResponse.postValue(response)
                    remainingBudget.postValue(response.budgetAmount) // Initialize remaining budget
                }.onFailure { error ->
                    errorMessage.postValue(error.message ?: "Unknown error occurred")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error posting budget: ${e.message}")
            }
        }
    }

    fun fetchBudget(userId: String) {
        viewModelScope.launch {
            try {
                val result = budgetRepository.getBudget(userId)
                result.onSuccess { response ->
                    budgetResponse.postValue(response)
                    remainingBudget.postValue(response.budgetAmount) // Update the remaining budget
                }.onFailure { error ->
                    errorMessage.postValue(error.message ?: "Failed to fetch budget")
                }
            } catch (e: Exception) {
                errorMessage.postValue("Error fetching budget: ${e.message}")
            }
        }
    }

    fun updateLocalBudgetAndExpenses(expense: Expense) {
        val currentBudget = remainingBudget.value ?: 0.0
        if (expense.amount <= currentBudget) {
            val updatedBudget = currentBudget - expense.amount
            remainingBudget.postValue(updatedBudget)

            val updatedExpenseList = expenseList.value.orEmpty().toMutableList()
            updatedExpenseList.add(expense)
            expenseList.postValue(updatedExpenseList)
        } else {
            errorMessage.postValue("Expense exceeds remaining budget")
        }
    }
}
