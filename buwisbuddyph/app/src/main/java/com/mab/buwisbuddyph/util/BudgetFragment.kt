package com.mab.buwisbuddyph.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.home.HomeFragment

class BudgetFragment : Fragment() {

    private lateinit var budgetET: EditText
    private lateinit var enterBudgetButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        budgetET = view.findViewById(R.id.budget_ET)
        enterBudgetButton = view.findViewById(R.id.enterBudgetButton)

        enterBudgetButton.setOnClickListener {
            val budgetStr = budgetET.text.toString()
            if (budgetStr.isNotEmpty()) {
                val budget = budgetStr.toDouble()
                saveBudgetToFirebase(budget)

                // Navigate back to HomeFragment or another fragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, HomeFragment()) // Adjust container ID as needed
                    .addToBackStack(null)
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Enter a valid budget", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveBudgetToFirebase(budget: Double) {
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            val data = mapOf(
                "userBudget" to budget,
                "userExpenseList" to emptyList<Map<String, String>>()
            )
            userRef.update(data)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Budget set and expenses cleared successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error saving budget: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }
}
