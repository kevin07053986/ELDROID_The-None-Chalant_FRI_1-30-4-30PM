package com.acosta.eldriod

import android.content.Context
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

        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (!userId.isNullOrEmpty()) {
            budgetViewModel.fetchBudget(userId)
        } else {
            Toast.makeText(requireContext(), getString(R.string.noUser), Toast.LENGTH_LONG).show()
        }

        budgetViewModel.remainingBudget.observe(viewLifecycleOwner) { budget ->
            remainingBudgetEt.setText(budget.toString())
        }

        budgetViewModel.expenseList.observe(viewLifecycleOwner) { expenses ->
            val adapter = SimpleAdapter(
                requireContext(),
                expenses.map { mapOf("label" to it.label, "expense" to it.amount.toString()) },
                android.R.layout.simple_list_item_2,
                arrayOf("label", "expense"),
                intArrayOf(android.R.id.text1, android.R.id.text2)
            )
            listOfExpense.adapter = adapter
        }


        enterExpenseButton.setOnClickListener {
            val expenseLabel = expenseLabelET.text.toString()
            val expenseStr = expenseET.text.toString()

            if (expenseLabel.isNotEmpty() && expenseStr.isNotEmpty()) {
                val expenseAmount = expenseStr.toDouble()
                val currentBudget = budgetViewModel.remainingBudget.value ?: 0.0

                if (expenseAmount <= currentBudget) {
                    val updatedBudget = currentBudget - expenseAmount
                    budgetViewModel.remainingBudget.value = updatedBudget

                    val newExpense = Expense(expenseLabel, expenseAmount)
                    val updatedExpenseList = budgetViewModel.expenseList.value.orEmpty().toMutableList()
                    updatedExpenseList.add(newExpense)
                    budgetViewModel.expenseList.value = updatedExpenseList

                    expenseLabelET.text.clear()
                    expenseET.text.clear()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.exceeds), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.validAmount), Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
