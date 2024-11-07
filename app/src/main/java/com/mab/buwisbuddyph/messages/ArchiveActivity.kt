package com.mab.buwisbuddyph.messages

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.adapters.MessageListAdapter3
import com.mab.buwisbuddyph.dataclass.new_Message

class ArchiveActivity : AppCompatActivity() {

    private val viewModel: ArchiveViewModel by viewModels()
    private lateinit var messageAdapter: MessageListAdapter3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_fragment_archive)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        messageAdapter = MessageListAdapter3(emptyList())
        recyclerView.adapter = messageAdapter

        findViewById<ImageView>(R.id.back_icon).setOnClickListener { finish() }

        viewModel.messages.observe(this) { messages ->
            messageAdapter.updateMessages(messages)
        }

        viewModel.fetchInboxMessages()
    }
}