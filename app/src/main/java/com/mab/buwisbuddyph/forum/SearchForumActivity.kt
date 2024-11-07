package com.mab.buwisbuddyph.forum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post
import com.mab.buwisbuddyph.home.HomeActivity

class SearchForumActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostListAdapter
    private val posts: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_forum)

        firestore = FirebaseFirestore.getInstance()
        searchButton = findViewById(R.id.search_forum_button)
        searchEditText = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.forumSearchRV)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostListAdapter(posts)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPosts(query)
            }
        }

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@SearchForumActivity, HomeActivity::class.java)
            intent.putExtra("openForumFragment", true) // Add extra to specify opening ForumFragment
            startActivity(intent)
            finish()
        }

    }

    private fun searchPosts(query: String) {
        val lowercaseQuery = query.lowercase()

        firestore.collection("posts")
            .orderBy("postTitle")
            .startAt(lowercaseQuery)
            .endAt(lowercaseQuery + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val posts = documents.toObjects(Post::class.java)
                adapter.setPosts(posts)
            }
            .addOnFailureListener { exception ->
                Log.e("SearchForumActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Error getting search results. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

}
