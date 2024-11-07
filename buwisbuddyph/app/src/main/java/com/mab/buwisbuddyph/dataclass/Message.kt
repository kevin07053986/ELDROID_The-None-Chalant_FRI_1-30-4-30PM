package com.mab.buwisbuddyph.dataclass

data class Message(
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long
)

