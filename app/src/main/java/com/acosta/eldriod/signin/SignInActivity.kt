package com.acosta.eldriod.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.acosta.eldriod.R
import com.google.android.material.snackbar.Snackbar

class SignInActivity : AppCompatActivity() {

    private val signInViewModel: SignInViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpTV = findViewById<TextView>(R.id.signUpTV)
        val forgotPasswordTV = findViewById<TextView>(R.id.forgotPasswordTV)

        loginButton.setOnClickListener { view -> onLoginClick(view) }

        observeViewModel()
    }

    private fun observeViewModel() {
        signInViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                // Show loading indicator if needed
            } else {
                // Hide loading indicator if needed
            }
        }

        signInViewModel.loginResponse.observe(this) { response ->
            if (response.token.isNotEmpty()) { // Check if token is valid
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
            } else {
                Snackbar.make(findViewById(R.id.loginButton), "Login failed", Snackbar.LENGTH_SHORT).show()
            }
        }

        signInViewModel.errorMessage.observe(this) { errorMessage ->
            Snackbar.make(findViewById(R.id.loginButton), errorMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onLoginClick(view: View) {
        val email = findViewById<EditText>(R.id.emailET).text.toString()
        val password = findViewById<EditText>(R.id.passwordET).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        signInViewModel.login(email, password, sharedPreferences)
    }
}
