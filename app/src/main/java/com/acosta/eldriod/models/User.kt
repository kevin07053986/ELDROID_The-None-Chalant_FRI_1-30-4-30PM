package com.acosta.eldriod.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String? = null,
    val dob: String,
    val accountType: String,
)
