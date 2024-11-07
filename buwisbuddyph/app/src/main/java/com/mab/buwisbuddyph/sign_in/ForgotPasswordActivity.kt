package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mab.buwisbuddyph.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


    }

    fun onEmailVerification(view: View){
        val intent = Intent(this, VerificationPasswordActivity::class.java)
        startActivity(intent)
    }

}
