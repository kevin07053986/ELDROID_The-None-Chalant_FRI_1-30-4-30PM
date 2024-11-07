package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.MessageListAdapter3
import com.mab.buwisbuddyph.dataclass.new_Message

class ArchiveActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageListAdapter3
    private var messages: MutableList<new_Message> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_fragment_archive)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        val backButton: ImageView = findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageListAdapter3(messages)
        recyclerView.adapter = messageAdapter
    }

    override fun onResume() {
        super.onResume()
        messages.clear()
        fetchInboxMessages() // Refresh messages when the activity is resumed
    }

    private fun fetchInboxMessages() {
        val currentUserID = auth.currentUser?.uid ?: return

        db.collection("archives")
            .whereEqualTo("my_id", currentUserID)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.id.let { db.collection("users").document(it).get().addOnSuccessListener { userDocu ->
                        val avatarImage = userDocu.getString("userProfileImage") ?: ""
                        val fullName = userDocu.getString("userFullName") ?: ""
                        val lastMessage = document.getString("text_left") ?: ""
                        val chatId = document.getString("chat_id")
                        val newMessage =
                            chatId?.let { it1 ->
                                new_Message(avatarImage,fullName, lastMessage,
                                    it1
                                )
                            }
                        if (newMessage != null) {
                            messages.add(newMessage)
                        }
                        messageAdapter.notifyDataSetChanged()
                    } }
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}
