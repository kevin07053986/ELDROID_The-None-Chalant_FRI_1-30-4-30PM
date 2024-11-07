package com.mab.buwisbuddyph.accountant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.ReviewAdapter
import com.mab.buwisbuddyph.dataclass.Review
import com.mab.buwisbuddyph.dataclass.User
import de.hdodenhof.circleimageview.CircleImageView

class AccountantProfileActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountant_profile)

        val accountantId = intent.getStringExtra("userID") ?: ""

        recyclerView = findViewById(R.id.reviewListRV)
        adapter = ReviewAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        if (accountantId.isNotEmpty()) {
            firestore.collection("users").document(accountantId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    user?.let {
                        displayUserDetails(it)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Accountant Profile", "Error fetching user details: ", exception)
                }
        }

        if (accountantId.isNotEmpty()) {
            fetchReviews(accountantId)
        }

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@AccountantProfileActivity, AccountantHelpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun displayUserDetails(user: User) {
        val accountantProfileImage = findViewById<CircleImageView>(R.id.accountantProfileImage)
        val accountantFullName = findViewById<TextView>(R.id.accountantFullName)

        Glide.with(this)
            .load(user.userProfileImage)
            .placeholder(R.drawable.default_profile_img)
            .error(R.drawable.default_profile_img)
            .into(accountantProfileImage)
        accountantFullName.text = user.userFullName
    }

    private fun fetchReviews(accountantId: String) {
        val reviewsCollection = firestore.collection("reviews")
        reviewsCollection.whereEqualTo("reviewUserID", accountantId)
            .get()
            .addOnSuccessListener { documents ->
                val reviewsList = mutableListOf<Review>()
                for (document in documents) {
                    val review = document.toObject(Review::class.java)
                    reviewsList.add(review)
                }
                Log.d("Accountant Profile", "success!")
                adapter.updateData(reviewsList)
            }
            .addOnFailureListener { exception ->
                Log.d("Accountant Profile", "Error:", exception)
            }
    }
}
