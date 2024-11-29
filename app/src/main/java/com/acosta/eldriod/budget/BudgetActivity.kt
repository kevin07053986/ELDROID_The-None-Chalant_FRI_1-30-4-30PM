package com.acosta.eldriod.budget

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acosta.eldriod.R
import com.acosta.eldriod.viewmodel.BudgetViewModel

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetET: EditText
    private lateinit var enterBudgetButton: Button
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        budgetET = findViewById(R.id.budget_ET)
        enterBudgetButton = findViewById(R.id.enterBudgetButton)
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_LONG).show()
            return
        }

        observeViewModel()

        enterBudgetButton.setOnClickListener {
            val budgetStr = budgetET.text.toString()
            if (budgetStr.isNotEmpty()) {
                val budgetAmount = budgetStr.toDouble()
                postBudget(userId, budgetAmount)
            } else {
                Toast.makeText(this, "Enter a valid budget amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postBudget(userId: Int, amount: Double) {
        val budgetRequest = BudgetRequest(
            title = "User Budget",
            amount = amount,
            userId = userId
        )
        budgetViewModel.postBudget(budgetRequest)
    }

    private fun observeViewModel() {
        budgetViewModel.budgetResponse.observe(this, Observer { response ->
            Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
            budgetET.text.clear()
        })

        budgetViewModel.errorMessage.observe(this, Observer { error ->
            Toast.makeText(this, "Error saving budget: $error", Toast.LENGTH_SHORT).show()
        })
    }
}
