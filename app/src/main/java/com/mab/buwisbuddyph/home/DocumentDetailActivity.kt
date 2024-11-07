package com.mab.buwisbuddyph.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.mab.buwisbuddyph.R


class DocumentDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)
        val documentImageView = findViewById<ImageView>(R.id.documentImageView)
        val documentImgLink = intent.getStringExtra("documentImgLink") ?: ""
        val returnIcon = findViewById<ImageView>(R.id.returnIcon)

        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@DocumentDetailActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        Glide.with(this)
            .load(documentImgLink)
            .into(documentImageView)
    }
}
