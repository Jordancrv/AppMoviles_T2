package com.example.juego.DB

data class Player(
    val id: Int = 0,
    val username: String,
    val email: String,
    val score: Int = 0,
    val maxScore: Int = 0,
    val qrToken: String? = null
)
