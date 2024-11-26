package com.acosta.eldriod.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.acosta.eldriod.R
import com.acosta.eldriod.viewmodel.AuthViewModel
import com.acosta.eldriod.viewmodel.SampleViewModel
import com.google.android.material.snackbar.Snackbar

class SignInActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpTV = findViewById<TextView>(R.id.signUpTV)
        val forgotPasswordTV = findViewById<TextView>(R.id.forgotPasswordTV)

        loginButton.setOnClickListener { view -> onLoginClick(view) }

        // observe if loginResponse is being updated
        authViewModel.loginResponse.observe(this) { loginResponse ->

            // start intent dre, switch to dashboard activity
            Toast.makeText(this, loginResponse.user.name, Toast.LENGTH_LONG).show()
        }
    }


    private fun onLoginClick(view: View) {

        val email = findViewById<EditText>(R.id.emailET).text.toString()
        val password = findViewById<EditText>(R.id.passwordET).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        authViewModel.login(LoginRequest(email, password))
    }
}
