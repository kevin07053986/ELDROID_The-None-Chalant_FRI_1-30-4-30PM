package com.acosta.eldriod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.acosta.eldriod.models.Expense
import com.acosta.eldriod.viewmodel.BudgetViewModel


class HomeFragment : Fragment() {

    private lateinit var createNewBudget: Button
    private lateinit var remainingBudgetEt: EditText
    private lateinit var expenseLabelET: EditText
    private lateinit var expenseET: EditText
    private lateinit var enterExpenseButton: Button
    private lateinit var listOfExpense: ListView
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        createNewBudget = view.findViewById(R.id.createNewBudget)
        remainingBudgetEt = view.findViewById(R.id.remainingBudgetEt)
        expenseLabelET = view.findViewById(R.id.expense_label_ET)
        expenseET = view.findViewById(R.id.expense_ET)
        enterExpenseButton = view.findViewById(R.id.enterExpenseButton)
        listOfExpense = view.findViewById(R.id.list_of_expense)

        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        // Observe the LiveData from the ViewModel
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            remainingBudgetEt.setText(budget.budgetAmount.toString())
        }

        budgetViewModel.expenseList.observe(viewLifecycleOwner) { expenseList ->
            val adapter = SimpleAdapter(
                requireContext(),
                expenseList.map {
                    mapOf("label" to it.label, "expense" to it.amount.toString())
                },
                android.R.layout.simple_list_item_2,
                arrayOf("label", "expense"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
            )
            listOfExpense.adapter = adapter
        }

        createNewBudget.setOnClickListener {
            // Handle budget creation here (navigate to BudgetActivity)
        }

        enterExpenseButton.setOnClickListener {
            val expenseLabel = expenseLabelET.text.toString()
            val expenseStr = expenseET.text.toString()

            if (expenseLabel.isNotEmpty() && expenseStr.isNotEmpty()) {
                val expense = expenseStr.toDouble()
                val currentBudget = budgetViewModel.budget.value?.budgetAmount ?: 0.0

                if (expense <= currentBudget) {
                    val userId = "user-id" // Get the user ID from session or authentication
                    val expenseItem = Expense(expenseLabel, expense)
                    budgetViewModel.addExpense(userId, expenseItem)
                } else {
                    Toast.makeText(requireContext(), "Expense exceeds remaining budget", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter a valid expense", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
