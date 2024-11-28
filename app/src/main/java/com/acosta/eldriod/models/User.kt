package com.acosta.eldriod.models

data class User(
    val id: Int? = null,
    val name: String,
    val dob: String,
    val email: String,
    val password: String,
    val accountType: String
)
