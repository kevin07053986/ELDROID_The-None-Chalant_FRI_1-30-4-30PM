package com.mab.buwisbuddyph.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.UserAdapter
import com.mab.buwisbuddyph.dataclass.new_User

class CreateMessageFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var users: List<new_User>
    private lateinit var filteredUsers: MutableList<new_User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        userRecyclerView = view.findViewById(R.id.userListRV)
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        users = mutableListOf()
        filteredUsers = mutableListOf()
        userAdapter = UserAdapter(filteredUsers) { user ->
            userId?.let { startChatWithUser(user, it) }
        }

        view.findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        userRecyclerView.adapter = userAdapter

        userId?.let { loadUsers(it) }

        val searchUserET = view.findViewById<EditText>(R.id.searchUserET)
        searchUserET.addTextChangedListener { text ->
            userId?.let { filterUsers(text.toString()) }
        }
    }

    private fun loadUsers(userId: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(new_User::class.java)
                    if (user.userID != userId)
                        users = users + user
                }
                filteredUsers.clear()
                filteredUsers.addAll(users)
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("CreateMessageFragment", "Error getting users: ", exception)
            }
    }

    private fun filterUsers(query: String) {
        val lowerCaseQuery = query.toLowerCase()
        filteredUsers.clear()
        for (user in users) {
            if (user.userFullName.toLowerCase().contains(lowerCaseQuery)) {
                filteredUsers.add(user)
            }
        }
        userAdapter.notifyDataSetChanged()
    }

    private fun startChatWithUser(user: new_User, myId: String) {
        val currentUserID = myId
        val otherUserID = user.userID

        db.collection("Chats")
            .whereEqualTo("person_1", currentUserID)
            .whereEqualTo("person_2", otherUserID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val chatID = documents.documents[0].id
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra("chatID", chatID)
                    startActivity(intent)
                } else {
                    db.collection("Chats")
                        .whereEqualTo("person_2", currentUserID)
                        .whereEqualTo("person_1", otherUserID)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val chatID = documents.documents[0].id
                                val intent = Intent(requireContext(), ChatActivity::class.java)
                                intent.putExtra("chatID", chatID)
                                startActivity(intent)
                            } else {
                                createNewChat(currentUserID, otherUserID)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CreateMessageFragment", "Error checking chat existence", e)
            }
    }

    private fun createNewChat(currentUserID: String, otherUserID: String) {
        val chatData = hashMapOf(
            "person_1" to currentUserID,
            "person_2" to otherUserID,
            "is_trashed" to false,
            "is_read_person_1" to true,
            "is_read_person_2" to false,
            "last_message" to ""
        )
        db.collection("Chats")
            .add(chatData)
            .addOnSuccessListener { documentReference ->
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("chatID", documentReference.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("CreateMessageFragment", "Error creating new chat", e)
            }
    }
}
