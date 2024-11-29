package com.acosta.eldriod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.acosta.eldriod.models.User
import com.acosta.eldriod.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val userFullNameET: EditText = findViewById(R.id.userFullNameET)
        val birthDateET: EditText = findViewById(R.id.birthDateET)
        val userEmailET: EditText = findViewById(R.id.userEmailET)
        val userPasswordET: EditText = findViewById(R.id.userPasswordET)
        val userPasswordConfirmationET: EditText = findViewById(R.id.userPasswordConfirmationET)
        val spinner: Spinner = findViewById(R.id.spinner)
        val createAccountButton: Button = findViewById(R.id.createAccountButton)

        signUpViewModel.registrationStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()

            if (status == getString(R.string.regsuccess)) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }


        createAccountButton.setOnClickListener {
            val fullName = userFullNameET.text.toString()
            val birthDate = birthDateET.text.toString()
            val email = userEmailET.text.toString()
            val password = userPasswordET.text.toString()
            val passwordConfirmation = userPasswordConfirmationET.text.toString()
            val accountType = spinner.selectedItem.toString()

            if (password != passwordConfirmation) {
                Toast.makeText(this, getString(R.string.notmatching), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                name = fullName,
                dob = birthDate,
                email = email,
                password = password,
                accountType = accountType
            )


            signUpViewModel.registerUser(user)
        }
    }
}
