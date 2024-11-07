package com.mab.buwisbuddyph.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.DocumentAdapter
import com.mab.buwisbuddyph.dataclass.Document

class DocumentListActivity : AppCompatActivity() {

    private lateinit var documentRecyclerView: RecyclerView
    private lateinit var adapter: DocumentAdapter
    private lateinit var userId: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        documentRecyclerView = findViewById(R.id.documentRecyclerView)
        documentRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DocumentAdapter(this)
        documentRecyclerView.adapter = adapter

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@DocumentListActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
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
                Log.e("DocumentListActivity", "Error fetching documents", exception)
            }
    }
}
