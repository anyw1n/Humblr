package com.example.humblr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subreddits")
data class Subreddit(
    @PrimaryKey val name: String,
    val id: String,
    val title: String,
    val noFollow: Boolean,
    val selftext: String,
    val url: String,
    val author: String,
    val numComments: Int,
    val subreddit: String
) : Thing()
