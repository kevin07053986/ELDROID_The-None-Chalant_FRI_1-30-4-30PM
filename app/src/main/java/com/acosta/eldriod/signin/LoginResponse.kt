package com.acosta.eldriod.signin

data class LoginResponse(
    val token: String,
    val userId: String,
    val userFullName: String
)

