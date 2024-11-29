package com.acosta.eldriod.budget

data class BudgetRequest(
    val title: String,
    val amount: Double,
    val userId: Int
)
