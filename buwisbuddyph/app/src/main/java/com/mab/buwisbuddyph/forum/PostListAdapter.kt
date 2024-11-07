package com.mab.buwisbuddyph.forum

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post

class PostListAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val tag = "PostAdapter"

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.postTitle)
        val descTextView: TextView = itemView.findViewById(R.id.postDesc)
        val upVotesTextView: TextView = itemView.findViewById(R.id.upVotes)
        val downVotesTextView: TextView = itemView.findViewById(R.id.downVotes)

        init {
            itemView.findViewById<View>(R.id.upVotePost).setOnClickListener { onUpVoteClicked(adapterPosition) }
            itemView.findViewById<View>(R.id.downVotePost).setOnClickListener { onDownVoteClicked(adapterPosition) }
            itemView.findViewById<View>(R.id.commentButton).setOnClickListener { onCommentClicked(adapterPosition) }
        }

        private fun onCommentClicked(position: Int) {
            val post = posts[position]
            val intent = Intent(itemView.context, PostActivity::class.java)
            intent.putExtra("postId", post.postID)
            itemView.context.startActivity(intent)
        }

        private fun onUpVoteClicked(position: Int) {
            val post = posts[position]
            val userId = auth.currentUser?.uid ?: return

            if (userId !in post.postUpVotedBy) {
                post.postUpVotes++
                post.postUpVotedBy.add(userId)
                notifyItemChanged(position)
                updateVotesInFirestore(post)
            }
        }

        private fun onDownVoteClicked(position: Int) {
            val post = posts[position]
            val userId = auth.currentUser?.uid ?: return

            if (userId !in post.postDownVotedBy) {
                post.postDownVotes++
                post.postDownVotedBy.add(userId)
                notifyItemChanged(position)
                updateVotesInFirestore(post)
            }
        }

        private fun updateVotesInFirestore(post: Post) {
            val postRef = db.collection("posts").document(post.postID)

            postRef
                .update(
                    "postUpVotes", post.postUpVotes,
                    "postDownVotes", post.postDownVotes,
                    "postUpVotedBy", post.postUpVotedBy,
                    "postDownVotedBy", post.postDownVotedBy
                )
                .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(tag, "Error updating document", e) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.forum_list_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = posts[position]
        holder.titleTextView.text = currentItem.postTitle
        holder.descTextView.text = currentItem.postDescription
        holder.upVotesTextView.text = currentItem.postUpVotes.toString()
        holder.downVotesTextView.text = currentItem.postDownVotes.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostActivity::class.java)
            intent.putExtra("postId", currentItem.postID)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun setPosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun getItemCount() = posts.size
}
