package com.mab.buwisbuddyph.sign_in

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.LoadingDialog
import com.mab.buwisbuddyph.R

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            onLoginClick(it)
        }

        val signUpTV = view.findViewById<TextView>(R.id.signUpTV)
        signUpTV.setOnClickListener {
            onSignUp()
        }

        val forgotPasswordTV = view.findViewById<TextView>(R.id.forgotPasswordTV)
        forgotPasswordTV.setOnClickListener {
            onForgotPassword()
        }
    }

    private fun onLoginClick(view: View) {
        val email = view.findViewById<EditText>(R.id.emailET).text.toString()
        val password = view.findViewById<EditText>(R.id.passwordET).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.loginLoadingDialog()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    db.collection("users")
                        .whereEqualTo("userEmail", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            loadingDialog.dismissDialog()
                            if (!documents.isEmpty) {
                                val userFullName = documents.first().getString("userFullName") ?: ""
                                val userProfileImage = documents.first().getString("userProfileImage") ?: ""

                                sharedPreferences.edit().apply {
                                    putString("userId", documents.first().id)
                                    putString("userFullName", userFullName)
                                    putString("userProfileImage", userProfileImage)
                                    apply()
                                }

                                val intent = Intent(requireContext(), HomeActivity::class.java)
                                ContextCompat.startActivity(requireContext(), intent, null)
                            } else {
                                Snackbar.make(view, "User document not found", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            loadingDialog.dismissDialog()
                            Snackbar.make(view, "Error retrieving user document: ${exception.message}", Snackbar.LENGTH_SHORT).show()
                        }
                } else {
                    loadingDialog.dismissDialog()
                    Snackbar.make(view, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun onSignUp() {
        val intent = Intent(requireContext(), SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun onForgotPassword() {
        val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}
