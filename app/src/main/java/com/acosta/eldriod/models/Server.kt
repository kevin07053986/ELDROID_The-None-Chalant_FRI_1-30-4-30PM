package com.acosta.eldriod.models

data class Server<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

