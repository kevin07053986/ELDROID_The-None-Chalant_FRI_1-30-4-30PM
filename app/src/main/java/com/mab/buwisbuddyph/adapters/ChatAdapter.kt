package com.mab.buwisbuddyph.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.ChatMessage

class ChatAdapter(private val chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val chatBubbleLayout: LinearLayout = itemView.findViewById(R.id.chatBubbleLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.new_item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatMessages[position]

        holder.messageText.text = message.message

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            layoutParams.gravity = Gravity.END
            layoutParams.marginEnd = 20
            holder.chatBubbleLayout.setBackgroundResource(R.drawable.bubble)
        } else {
            layoutParams.gravity = Gravity.START
            layoutParams.marginStart = 20
            holder.chatBubbleLayout.setBackgroundResource(R.drawable.bubble_sender)
        }
        holder.chatBubbleLayout.layoutParams = layoutParams
    }

    override fun getItemCount() = chatMessages.size
}

