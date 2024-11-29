package com.acosta.eldriod.repository

import com.acosta.eldriod.budget.BudgetRequest
import com.acosta.eldriod.budget.BudgetResponse
import com.acosta.eldriod.network.ApiService

class BudgetRepository(private val apiService: ApiService) {

    suspend fun storeBudget(budgetRequest: BudgetRequest): Result<BudgetResponse> {
        return try {
            val response = apiService.storeBudget(budgetRequest)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception(RepositoryMessages.EMPTY_RESPONSE))
            } else {
                Result.failure(Exception("${RepositoryMessages.POST_BUDGET_FAILURE}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("${RepositoryMessages.ERROR_POSTING_BUDGET}: ${e.message}"))
        }
    }

    suspend fun getBudget(userId: String): Result<BudgetResponse> {
        return try {
            val response = apiService.getBudget(userId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception(RepositoryMessages.EMPTY_RESPONSE))
            } else {
                Result.failure(Exception("${RepositoryMessages.FETCH_BUDGET_FAILURE}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("${RepositoryMessages.ERROR_FETCHING_BUDGET}: ${e.message}"))
        }
    }
}
