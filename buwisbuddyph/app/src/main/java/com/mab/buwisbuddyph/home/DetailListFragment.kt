package com.mab.buwisbuddyph.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.DocumentAdapter
import com.mab.buwisbuddyph.dataclass.Document

class DetailListFragment : Fragment() {

    private lateinit var documentRecyclerView: RecyclerView
    private lateinit var adapter: DocumentAdapter
    private lateinit var userId: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        documentRecyclerView = view.findViewById(R.id.documentRecyclerView)
        documentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DocumentAdapter(requireContext())
        documentRecyclerView.adapter = adapter

        // Return icon to go back to HomeFragment
        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchUserDocuments()
    }

    private fun fetchUserDocuments() {
        val userDocumentsRef = db.collection("users").document(userId).collection("userDocuments")

        userDocumentsRef.get()
            .addOnSuccessListener { documents ->
                val userDocumentsList = mutableListOf<Document>()
                for (document in documents) {
                    val documentId = document.getString("documentID") ?: ""
                    val documentImgLink = document.getString("documentImgLink") ?: ""
                    userDocumentsList.add(Document(documentId, documentImgLink))
                }
                adapter.setDocuments(userDocumentsList)
            }
            .addOnFailureListener { exception ->
                Log.e("DocumentListFragment", "Error fetching documents", exception)
            }
    }
}
