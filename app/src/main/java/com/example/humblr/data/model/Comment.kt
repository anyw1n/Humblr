package com.example.humblr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val name: String,
    val author: String,
    val body: String,
    val created: Double,
    val score: Int,
    val likes: Boolean?,
    val saved: Boolean
) : Thing()
