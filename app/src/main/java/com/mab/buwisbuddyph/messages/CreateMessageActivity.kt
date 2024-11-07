package com.mab.buwisbuddyph.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.UserAdapter
import com.mab.buwisbuddyph.dataclass.new_User

class CreateMessageActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var users: List<new_User>
    private lateinit var filteredUsers: MutableList<new_User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_message)

        db = FirebaseFirestore.getInstance()
        userRecyclerView = findViewById(R.id.userListRV)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        users = mutableListOf()
        filteredUsers = mutableListOf()
        userAdapter = UserAdapter(filteredUsers) { user ->
            if (userId != null) {
                startChatWithUser(user, userId)
            }
        }
        val backButton: ImageView = findViewById(R.id.back_icon)
        backButton.setOnClickListener {
            finish()
        }
        userRecyclerView.adapter = userAdapter

        if (userId != null) {
            loadUsers(userId)
        }

        val toEditText = findViewById<EditText>(R.id.searchUserET)
        toEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (userId != null) {
                    filterUsers(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
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
                Log.w("CreateMessageActivity", "Error getting users: ", exception)
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
                    val intent = Intent(this, ChatActivity::class.java)
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
                                val intent = Intent(this, ChatActivity::class.java)
                                intent.putExtra("chatID", chatID)
                                startActivity(intent)
                            } else {
                                createNewChat(currentUserID, otherUserID)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CreateMessageActivity", "Error checking chat existence", e)
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
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("chatID", documentReference.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("CreateMessageActivity", "Error creating new chat", e)
            }
    }
}
