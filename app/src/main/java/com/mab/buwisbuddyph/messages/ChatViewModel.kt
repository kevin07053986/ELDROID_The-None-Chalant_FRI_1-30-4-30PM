package com.mab.buwisbuddyph.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mab.buwisbuddyph.dataclass.ChatMessage

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    fun loadMessages(chatID: String) {
        db.collection("Chats").document(chatID).collection("Messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                val messages = snapshots?.documents?.map { it.toObject(ChatMessage::class.java)!! }
                _chatMessages.postValue(messages ?: emptyList())
            }
    }

    fun sendMessage(message: String, chatID: String) {
        val timestamp = System.currentTimeMillis()
        val senderId = auth.currentUser?.uid ?: return
        val chatMessage = ChatMessage(senderId, message, timestamp)

        db.collection("Chats").document(chatID).collection("Messages")
            .add(chatMessage)
            .addOnFailureListener { e -> /* Handle error */ }
    }
}