package com.mab.buwisbuddyph.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post

class ForumFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var allPosts: MutableList<Post> // To store all posts for search filtering

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)
        Log.d("forumFragment", "forum fragment loaded")
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.forumPostList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postListAdapter = PostListAdapter(mutableListOf())
        recyclerView.adapter = postListAdapter

        loadPosts()

        val createPost: ImageView = view.findViewById(R.id.createPost)
        createPost.setOnClickListener {
            onCreatePost(it)
        }

        val searchView: SearchView = view.findViewById(R.id.search_accountants)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText ?: "")
                return true
            }
        })

        return view
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("postTimestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                allPosts = mutableListOf()
                for (document in querySnapshot.documents) {
                    val post = document.toObject(Post::class.java)
                    post?.let {
                        allPosts.add(it)
                    }
                }
                postListAdapter.setPosts(allPosts)
                Log.d(TAG, "Posts successfully loaded and set to adapter")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting posts", exception)
            }
    }

    private fun filterPosts(query: String) {
        val filteredPosts = allPosts.filter { post ->
            post.postTitle.contains(query, ignoreCase = true) ||
                    post.postDescription.contains(query, ignoreCase = true)
        }
        postListAdapter.setPosts(filteredPosts)
    }

    companion object {
        private const val TAG = "ForumFragment"
    }

    private fun onCreatePost(view: View) {
        Log.d(TAG, "onCreatePost called")
        val postFragment = PostFragment()
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, postFragment)
            commit()
        }
        Log.d(TAG, "PostFragment has been committed")
    }
}
