package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.MessageListAdapter2
import com.mab.buwisbuddyph.dataclass.new_Message

class TrashActivity : AppCompatActivity(), MessageListAdapter2.OnRefreshListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageListAdapter2
    private var messages: MutableList<new_Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_fragment_trash)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        val backButton: ImageView = findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            finish()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageListAdapter2(messages, this)
        recyclerView.adapter = messageAdapter
    }

    override fun onRequestRefresh() {
        fetchInboxMessages()
    }

    override fun onResume() {
        super.onResume()
        messages.clear()
        fetchInboxMessages()
    }

    private fun fetchInboxMessages() {
        val currentUserID = auth.currentUser?.uid ?: return

        db.collection("Chats")
            .whereEqualTo("person_1", currentUserID)
            .whereEqualTo("is_trashed", true)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.getString("person_2")?.let {
                        db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
                            val avatarImage = userDocu.getString("userProfileImage") ?: ""
                            val fullName = userDocu.getString("userFullName") ?: ""
                            val lastMessage = document.getString("last_message") ?: ""
                            val chatId = document.id
                            val newMessage = new_Message(avatarImage, fullName, lastMessage, chatId)
                            messages.add(newMessage)
                            messageAdapter.notifyDataSetChanged()
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
            }
        db.collection("Chats")
            .whereEqualTo("person_2", currentUserID)
            .whereEqualTo("is_trashed", true)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.getString("person_1")?.let {
                        db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
                            val avatarImage = userDocu.getString("userProfileImage") ?: ""
                            val fullName = userDocu.getString("userFullName") ?: ""
                            val lastMessage = document.getString("last_message") ?: ""
                            val chatId = document.id
                            val newMessage = new_Message(avatarImage, fullName, lastMessage, chatId)
                            messages.add(newMessage)
                            messageAdapter.notifyDataSetChanged()
                        }
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}
