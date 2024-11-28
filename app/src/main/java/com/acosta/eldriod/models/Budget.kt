package com.acosta.eldriod.models

data class Budget(
    var budgetAmount: Double,
    var expenseList: List<Expense> = emptyList()
)

