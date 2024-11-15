package com.mab.buwisbuddyph.messages

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.R

class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var btnInbox: Button
    private lateinit var btnTrash: Button
    private lateinit var btnArchive: Button
    private var inboxBadge: Int = 0
    private var trashBadge: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnInbox = view.findViewById(R.id.btnInbox)
        btnTrash = view.findViewById(R.id.btnTrash)
        btnArchive = view.findViewById(R.id.btnArchive)

        btnInbox.text = "Inbox (${inboxBadge})"
        btnTrash.text = "Trash (${trashBadge})"

        view.findViewById<View>(R.id.btnNewMessage).setOnClickListener {
            val intent = Intent(requireContext(), CreateMessageFragment::class.java)
            startActivity(intent)
        }
        view.findViewById<View>(R.id.btnArchive).setOnClickListener {
            // Navigate to Archive activity or fragment
        }

        btnInbox.setOnClickListener {
            val intent = Intent(requireContext(), InboxFragment::class.java)
            startActivity(intent)
        }

        btnTrash.setOnClickListener {
            val intent = Intent(requireContext(), TrashFragment::class.java)
            startActivity(intent)
        }
        btnArchive.setOnClickListener {
            val intent = Intent(requireContext(), ArchiveFragment::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateBadgeCounts()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBadgeCounts() {
        val currentUserID = auth.currentUser?.uid ?: return

        var unreadInInbox = 0
        var unreadInTrash = 0

        db.collection("Chats")
            .whereEqualTo("person_1", currentUserID)
            .get()
            .addOnSuccessListener { chatDocuments ->

                for (document in chatDocuments) {
                    val isTrash = document.getBoolean("is_trashed") ?: false
                    if (!isTrash) {
                        unreadInInbox++
                    } else if (isTrash) {
                        unreadInTrash++
                    }
                }

                db.collection("Chats")
                    .whereEqualTo("person_2", currentUserID)
                    .get()
                    .addOnSuccessListener { chatDocuments ->

                        for (document in chatDocuments) {
                            val isTrash = document.getBoolean("is_trashed") ?: false
                            if (!isTrash) {
                                unreadInInbox++
                            } else if (isTrash) {
                                unreadInTrash++
                            }
                        }

                        btnInbox.text = "Inbox ($unreadInInbox)"
                        btnTrash.text = "Trash ($unreadInTrash)"
                    }
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}
