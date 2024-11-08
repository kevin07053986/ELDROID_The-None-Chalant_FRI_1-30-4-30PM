package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.home.HomeActivity
import com.mab.buwisbuddyph.viewmodel.SignInViewModel

class SignInActivity : AppCompatActivity() {
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setupObservers()

        // Login button click
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailET).text.toString()
            val password = findViewById<EditText>(R.id.passwordET).text.toString()
            viewModel.loginUser(email, password)
        }
    }

    private fun setupObservers() {
        viewModel.userLiveData.observe(this, Observer { user ->
            if (user != null) {
                // Navigate to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        })

        viewModel.errorLiveData.observe(this, Observer { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        })
    }
}
