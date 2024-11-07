package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mab.buwisbuddyph.R

class VerificationPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_password)

    }

    fun onVerifyCode(view: View){
        intent = Intent(this, NewPasswordActivity::class.java)
        startActivity(intent)
    }

    fun onSignUpClick(view: View) {
        intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}