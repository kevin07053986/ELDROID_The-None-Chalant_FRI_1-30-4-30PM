package com.mab.buwisbuddyph.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Post



class SearchForumFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var searchButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostListAdapter
    private val posts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_forum, container, false)

        firestore = FirebaseFirestore.getInstance()
        searchButton = view.findViewById(R.id.search_forum_button)
        searchEditText = view.findViewById(R.id.search_input)
        recyclerView = view.findViewById(R.id.forumSearchRV)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostListAdapter(posts)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPosts(query)
            }
        }

        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
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
                Log.e("SearchForumFragment", "Error getting documents: ", exception)
                Toast.makeText(requireContext(), "Error getting search results. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
