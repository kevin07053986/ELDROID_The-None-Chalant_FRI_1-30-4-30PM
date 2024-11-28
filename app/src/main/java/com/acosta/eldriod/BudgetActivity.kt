package com.acosta.eldriod

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.acosta.eldriod.models.Budget
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
        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        enterBudgetButton.setOnClickListener {
            val budgetStr = budgetET.text.toString()
            if (budgetStr.isNotEmpty()) {
                val budget = budgetStr.toDouble()
                val userId = "user-id"
                budgetViewModel.updateBudget(userId, Budget(budget))
            } else {
                Toast.makeText(this, "Enter a valid budget", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
