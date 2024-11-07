package com.mab.buwisbuddyph.dataclass

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
