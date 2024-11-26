package com.acosta.eldriod.signin

import com.acosta.eldriod.models.User

data class LoginResponse(
    val token: String,
    val user: User
)

