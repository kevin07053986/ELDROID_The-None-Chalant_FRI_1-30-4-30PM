package com.acosta.eldriod.budget

import com.google.gson.annotations.SerializedName

data class BudgetResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("budget_amount") val budgetAmount: Double
)
