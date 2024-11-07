package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.ChatAdapter

class ChatActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var editText: EditText
    private lateinit var chatID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_activity_chat)

        chatID = intent.getStringExtra("chatID") ?: return
        editText = findViewById(R.id.messageEditText)

        chatAdapter = ChatAdapter(emptyList())
        findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val message = editText.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message, chatID)
                editText.text.clear()
            }
        }

        viewModel.chatMessages.observe(this) { messages ->
            chatAdapter.updateMessages(messages)
        }

        viewModel.loadMessages(chatID)
    }
}
