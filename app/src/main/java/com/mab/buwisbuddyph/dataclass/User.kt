package com.mab.buwisbuddyph.model

import com.google.firebase.Timestamp

data class User(
    var userID: String = "",
    val userFullName: String = "",
    val userEmail: String = "",
    val userProfileImage: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val userAccountType: String = ""
)

