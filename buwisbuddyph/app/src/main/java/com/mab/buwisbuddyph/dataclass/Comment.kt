package com.mab.buwisbuddyph.dataclass

import com.google.firebase.Timestamp

data class Comment(
    val commentID: String = "",
    val commentUserID: String = "",
    val commentUserComment: String = "",
    val commentTimestamp: Timestamp? = null,
    var commentUserFullName: String? = null,
    var commentUserProfileImage: String? = null
)
