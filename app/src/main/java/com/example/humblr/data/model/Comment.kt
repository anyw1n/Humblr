package com.example.humblr.data.model

data class Comment(
    val name: String,
    val author: String,
    val body: String,
    val created: Double,
    val score: Int,
    val likes: Boolean?,
    val saved: Boolean
) : Thing()
