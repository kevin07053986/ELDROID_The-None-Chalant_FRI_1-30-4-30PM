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
import com.mab.buwisbuddyph.adapters.MessageListAdapter3
import com.mab.buwisbuddyph.dataclass.new_Message

class ArchiveFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageListAdapter3
    private var messages: MutableList<new_Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_fragment_archive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.recyclerView)
        val backButton: ImageView = view.findViewById(R.id.back_icon)

        // Set up back button to navigate to the previous fragment
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        messageAdapter = MessageListAdapter3(messages)
        recyclerView.adapter = messageAdapter
    }

    override fun onResume() {
        super.onResume()
        messages.clear()
        fetchInboxMessages() // Refresh messages when the fragment is resumed
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
                                new_Message(avatarImage, fullName, lastMessage, it1)
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
