package com.example.humblr.data.model

data class Comment(
    val author: String,
    val body: String,
    val createdUtc: Double,
    val score: Int
) : Thing()
