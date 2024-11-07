package com.mab.buwisbuddyph.dataclass

import com.google.firebase.Timestamp

data class Review(
    val reviewID: String = "",
    val reviewUserID: String = "",
    val reviewUserComment: String = "",
    val commentTimestamp: Timestamp? = null
)
