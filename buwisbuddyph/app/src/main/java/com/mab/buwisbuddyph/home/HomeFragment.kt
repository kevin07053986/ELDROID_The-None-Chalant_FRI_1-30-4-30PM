package com.mab.buwisbuddyph.home

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.util.BudgetFragment

class HomeFragment : Fragment() {

    private lateinit var createNewBudget: Button
    private lateinit var remainingBudgetEt: EditText
    private lateinit var expenseLabelET: EditText
    private lateinit var expenseET: EditText
    private lateinit var enterExpenseButton: Button
    private lateinit var listOfExpense: ListView
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var budget = 0.0
    private val expenseList = mutableListOf<Map<String, String>>()

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

        remainingBudgetEt.isEnabled = false

        loadBudget()

        createNewBudget.setOnClickListener {
            val intent = Intent(requireContext(), BudgetFragment::class.java)
            startActivity(intent)
        }

        enterExpenseButton.setOnClickListener {
            val expenseLabel = expenseLabelET.text.toString()
            val expenseStr = expenseET.text.toString()
            if (expenseLabel.isNotEmpty() && expenseStr.isNotEmpty()) {
                val expense = expenseStr.toDouble()
                if (expense <= budget) {
                    budget -= expense
                    remainingBudgetEt.setText(budget.toString())
                    saveBudgetToFirebase(budget)
                    addExpenseToList(expenseLabel, expense)
                } else {
                    Toast.makeText(requireContext(), "Expense exceeds remaining budget", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter a valid expense", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadBudget() {
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.contains("userBudget")) {
                    budget = document.getDouble("userBudget") ?: 0.0
                    remainingBudgetEt.setText(budget.toString())
                }
                if (document != null && document.contains("userExpenseList")) {
                    val expenses = document.get("userExpenseList") as? List<Map<String, String>>
                    if (expenses != null) {
                        expenseList.clear()
                        expenseList.addAll(expenses)
                        updateExpenseListView()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load budget", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBudgetToFirebase(budget: Double) {
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            userRef.update("userBudget", budget)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    val data = hashMapOf("userBudget" to budget)
                    userRef.set(data)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Budget set successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error saving budget", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }

    private fun addExpenseToList(label: String, expense: Double) {
        val expenseMap = mapOf("label" to label, "expense" to expense.toString())
        expenseList.add(expenseMap)
        updateExpenseListView()
        saveExpensesToFirebase()
        expenseLabelET.text.clear()
        expenseET.text.clear()
    }

    private fun updateExpenseListView() {
        val adapter = SimpleAdapter(
            requireContext(),
            expenseList,
            android.R.layout.simple_list_item_2,
            arrayOf("label", "expense"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        listOfExpense.adapter = adapter
    }

    private fun saveExpensesToFirebase() {
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            userRef.update("userExpenseList", expenseList)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Expenses updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    userRef.set(mapOf("userExpenseList" to expenseList))
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Expenses saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Error saving expenses", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }
}
