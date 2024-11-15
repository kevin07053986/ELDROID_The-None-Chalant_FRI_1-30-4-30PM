package com.mab.buwisbuddyph.accountant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
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

class AccountantProfileFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accountantId = arguments?.getString("userID") ?: ""

        recyclerView = view.findViewById(R.id.reviewListRV)
        adapter = ReviewAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        if (accountantId.isNotEmpty()) {
            firestore.collection("users").document(accountantId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<User>()
                    user?.let {
                        displayUserDetails(it, view)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Accountant Profile", "Error fetching user details: ", exception)
                }
        }

        if (accountantId.isNotEmpty()) {
            fetchReviews(accountantId)
        }

        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Navigate back, you may need to adjust this for your navigation setup
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun displayUserDetails(user: User, view: View) {
        val accountantProfileImage = view.findViewById<CircleImageView>(R.id.accountantProfileImage)
        val accountantFullName = view.findViewById<TextView>(R.id.accountantFullName)

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
