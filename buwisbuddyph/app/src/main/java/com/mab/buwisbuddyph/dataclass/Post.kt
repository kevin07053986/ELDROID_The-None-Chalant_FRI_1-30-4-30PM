package com.mab.buwisbuddyph.dataclass

import com.google.firebase.Timestamp
import org.w3c.dom.Comment

data class Post(
    val postTitle: String = "",
    val postDescription: String = "",
    val postPosterID: String = "",
    val postContent: String = "",
    val postID: String = "",
    val postTimestamp: Timestamp? = null,
    var postUpVotes: Int = 0,
    var postDownVotes: Int = 0,
    val postUpVotedBy: MutableList<String> = mutableListOf(),
    val postDownVotedBy: MutableList<String> = mutableListOf(),
    var postCommentsCount: Int = 0,
    val postCommentList: MutableList<Comment> = mutableListOf(),
) {
    constructor() : this("", "", "", "", "", null, 0, 0, mutableListOf(), mutableListOf(), 0, mutableListOf())
}

