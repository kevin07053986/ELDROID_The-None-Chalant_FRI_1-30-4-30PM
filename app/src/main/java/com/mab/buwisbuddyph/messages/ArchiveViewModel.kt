package com.mab.buwisbuddyph.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.dataclass.new_Message

class ArchiveViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _messages = MutableLiveData<List<new_Message>>()
    val messages: LiveData<List<new_Message>> get() = _messages

    fun fetchInboxMessages() {
        val currentUserID = auth.currentUser?.uid ?: return

        db.collection("archives")
            .whereEqualTo("my_id", currentUserID)
            .get()
            .addOnSuccessListener { chatDocuments ->
                val fetchedMessages = mutableListOf<new_Message>()
                for (document in chatDocuments) {
                    document.id.let { id ->
                        db.collection("users").document(id).get().addOnSuccessListener { userDoc ->
                            val avatarImage = userDoc.getString("userProfileImage") ?: ""
                            val fullName = userDoc.getString("userFullName") ?: ""
                            val lastMessage = document.getString("text_left") ?: ""
                            val chatId = document.getString("chat_id")
                            val newMessage = chatId?.let { new_Message(avatarImage, fullName, lastMessage, it) }
                            newMessage?.let { fetchedMessages.add(it) }
                            _messages.postValue(fetchedMessages)
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}