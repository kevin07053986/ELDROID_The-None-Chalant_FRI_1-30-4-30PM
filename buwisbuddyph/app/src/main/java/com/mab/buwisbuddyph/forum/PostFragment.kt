package com.mab.buwisbuddyph.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post

class PostFragment : Fragment() {

    private lateinit var postTitleEditText: EditText
    private lateinit var postDescEditText: EditText
    private lateinit var postContentEditText: EditText
    private lateinit var postButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        postTitleEditText = view.findViewById(R.id.postTitle)
        postDescEditText = view.findViewById(R.id.postDesc)
        postContentEditText = view.findViewById(R.id.postContent)
        postButton = view.findViewById(R.id.postButton)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        postButton.setOnClickListener {
            savePost()
        }

        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun savePost() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val postTitle = postTitleEditText.text.toString().trim()
                    val postDesc = postDescEditText.text.toString().trim()
                    val postContent = postContentEditText.text.toString().trim()

                    if (postTitle.isEmpty() || postDesc.isEmpty() || postContent.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val postId = db.collection("posts").document().id
                    val post = Post(
                        postTitle = postTitle,
                        postDescription = postDesc,
                        postContent = postContent,
                        postID = postId,
                        postTimestamp = Timestamp.now(),
                        postPosterID = userId
                    )

                    db.collection("posts")
                        .add(post)
                        .addOnSuccessListener { documentReference ->
                            documentReference.update("postID", documentReference.id)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Post saved successfully", Toast.LENGTH_SHORT).show()
                                    postTitleEditText.text.clear()
                                    postDescEditText.text.clear()
                                    postContentEditText.text.clear()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(requireContext(), "Failed to update post", Toast.LENGTH_SHORT).show()
                                    Log.e(TAG, "Error updating post with generated postID", exception)
                                }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Failed to save post", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Error saving post", exception)
                        }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting user document", exception)
            }
    }

    companion object {
        private const val TAG = "PostFragment"
    }
}
