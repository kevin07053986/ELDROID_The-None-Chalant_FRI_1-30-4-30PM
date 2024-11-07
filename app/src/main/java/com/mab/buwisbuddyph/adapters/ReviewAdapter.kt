package com.mab.buwisbuddyph.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Review
import de.hdodenhof.circleimageview.CircleImageView

class ReviewAdapter(private var reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.accountant_rating_list_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.userProfileImage.setImageResource(R.drawable.default_profile_img)
        holder.accountantRatingBar.rating = 3.5f
        holder.userReviews.text = review.reviewUserComment
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfileImage: CircleImageView = itemView.findViewById(R.id.userProfileImage)
        val accountantRatingBar: RatingBar = itemView.findViewById(R.id.accountantRatingBar)
        val userReviews: TextView = itemView.findViewById(R.id.userReviews)

    }
}
