package com.mab.buwisbuddyph.forum

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Comment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentListAdapter(private val commentList: MutableList<Comment>) : RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCache = mutableMapOf<String, UserCache>()

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfileImage: CircleImageView = itemView.findViewById(R.id.userProfileImage)
        val conversationTV: TextView = itemView.findViewById(R.id.conversationTV)
        val userFullName: TextView = itemView.findViewById(R.id.userFullName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = commentList[position]
        holder.conversationTV.text = currentItem.commentUserComment

        val userID = currentItem.commentUserID

        if (userCache.containsKey(userID)) {
            val cachedUser = userCache[userID]
            holder.userFullName.text = cachedUser?.fullName
            Picasso.get().load(cachedUser?.profileImageUrl).placeholder(R.drawable.default_profile_img).into(holder.userProfileImage)
        } else {
            db.collection("users").document(userID)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userFullName = document.getString("userFullName")
                        val userProfileImageUrl = document.getString("userProfileImage")

                        userCache[userID] = UserCache(userFullName, userProfileImageUrl)

                        holder.userFullName.text = userFullName
                        Picasso.get().load(userProfileImageUrl).placeholder(R.drawable.default_profile_img).into(holder.userProfileImage)
                    } else {
                        Log.d("CommentListAdapter", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("CommentListAdapter", "Error getting document", exception)
                }
        }
    }

    override fun getItemCount() = commentList.size

    fun updateData(newComments: List<Comment>) {
        commentList.clear()
        commentList.addAll(newComments)
        notifyDataSetChanged()
    }

    data class UserCache(val fullName: String?, val profileImageUrl: String?)
}
