package com.mab.buwisbuddyph.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.new_Message
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageAdapter(private val context: Context, private val messages: List<new_Message>) :
    RecyclerView.Adapter<NewMessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleImageView: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        val lastChatTextView: TextView = itemView.findViewById(R.id.lastChatTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.new_item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        Picasso.get().load(message.avatarUrl).into(holder.circleImageView)
        holder.fullNameTextView.text = message.fullName
        holder.lastChatTextView.text = message.last_chat
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
