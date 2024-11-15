package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.ChatAdapter
import com.mab.buwisbuddyph.dataclass.ChatMessage
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var auth: FirebaseAuth
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var editText: EditText
    private lateinit var other_id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.new_fragment_chat, container, false) // Update layout name if needed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        editText = view.findViewById(R.id.messageEditText)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        chatAdapter = ChatAdapter(chatMessages)

        val backButton: ImageView = view.findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        val chatID = arguments?.getString("chatID")

        val avatar: CircleImageView = view.findViewById(R.id.avatar)
        val name: TextView = view.findViewById(R.id.name)

        chatID?.let {
            checkForArchivedText(it)
            loadMessages(it)
            fetchUserInfo(chatID, name)
        }

        val sendButton: Button = view.findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            val message = editText.text.toString()
            if (message.isNotEmpty()) {
                chatID?.let { id ->
                    sendMessage(message, id)
                    editText.text.clear() // Clear the input field after sending a message
                }
            }
        }

        // Handle back navigation with custom logic if needed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val textLeft = editText.text.toString().trim()
            saveToArchives(textLeft)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun checkForArchivedText(chatID: String) {
        db.collection("archives")
            .whereEqualTo("my_id", auth.currentUser?.uid)
            .whereEqualTo("chat_id", chatID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val archivedText = documents.documents[0].getString("text_left") ?: ""
                    editText.setText(archivedText)
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatFragment", "Error checking for archived text", e)
            }
    }


    private fun saveToArchives(textLeft: String) {
        if (textLeft.isEmpty()) {
            db.collection("archives").document(other_id).delete()
        } else {
            val archivesRef = db.collection("archives").document(other_id)
            val data = hashMapOf(
                "my_id" to auth.currentUser?.uid,
                "text_left" to textLeft,
                "chat_id" to arguments?.getString("chatID")
            )
            archivesRef.set(data)
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.e("ChatFragment", "Error saving to archives", e)
                }
        }
    }

    private fun sendMessage(message: String, chatID: String) {
        val timestamp = System.currentTimeMillis()
        val chatMessage = auth.uid?.let { uid ->
            ChatMessage(
                senderId = uid,
                message = message,
                timestamp = timestamp
            )
        }

        val chatRef = db.collection("Chats").document(chatID)
        chatRef.update("last_message", message, "is_trashed", false)
            .addOnSuccessListener {
                Log.d("ChatFragment", "Last message updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w("ChatFragment", "Error updating last message", e)
            }

        chatMessage?.let {
            chatRef.collection("Messages")
                .add(it)
                .addOnSuccessListener {
                    Log.d("ChatFragment", "Message sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.w("ChatFragment", "Error sending message", e)
                }
        }
    }

    private fun loadMessages(chatID: String) {
        db.collection("Chats").document(chatID).collection("Messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ChatFragment", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    chatMessages.clear()
                    for (doc in snapshots) {
                        val message = doc.toObject(ChatMessage::class.java)
                        chatMessages.add(message)
                    }
                    chatAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun fetchUserInfo(chatID: String, name: TextView) {
        db.collection("Chats").document(chatID).get().addOnSuccessListener { chatDocs ->
            val userField = if (chatDocs.get("person_1") == auth.uid) "person_2" else "person_1"
            chatDocs.getString(userField)?.let { userId ->
                db.collection("users").document(userId).get().addOnSuccessListener { userDoc ->
                    name.text = userDoc.getString("userFullName") ?: ""
                    other_id = userDoc.getString("userID").toString()
                }
            }
        }
    }
}
