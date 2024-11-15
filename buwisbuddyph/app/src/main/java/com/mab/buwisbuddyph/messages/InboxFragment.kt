package com.mab.buwisbuddyph.messages

import android.os.Bundle
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
import com.mab.buwisbuddyph.adapters.MessageListAdapter
import com.mab.buwisbuddyph.dataclass.new_Message

class InboxFragment : Fragment(), MessageListAdapter.OnRefreshListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageListAdapter
    private var messages: MutableList<new_Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.recyclerView)
        val backButton: ImageView = view.findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        messageAdapter = MessageListAdapter(messages, this)
        recyclerView.adapter = messageAdapter

        // Initial data fetch
        fetchInboxMessages()
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
            .whereEqualTo("is_trashed", false)
            .get()
            .addOnSuccessListener { chatDocuments ->
                messages.clear()
                for (document in chatDocuments) {
                    document.getString("person_2")?.let { userId ->
                        db.collection("users").document(userId).get().addOnSuccessListener { userDocu ->
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
            .addOnFailureListener {
                // Handle error if needed
            }

        db.collection("Chats")
            .whereEqualTo("person_2", currentUserID)
            .whereEqualTo("is_trashed", false)
            .get()
            .addOnSuccessListener { chatDocuments ->
                for (document in chatDocuments) {
                    document.getString("person_1")?.let { userId ->
                        db.collection("users").document(userId).get().addOnSuccessListener { userDocu ->
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
            .addOnFailureListener {
                // Handle error if needed
            }
    }
}
