package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.home.HomeActivity

class NewPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

    }
    fun onNewPasswordSubmit(view : View){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}