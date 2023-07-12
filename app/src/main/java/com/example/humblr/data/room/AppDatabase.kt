package com.example.humblr.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Subreddit

@Database(
    entities = [Subreddit::class, Comment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subredditDao(): SubredditDao

    abstract fun commentDao(): CommentDao

    companion object {

        fun create(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
    }
}
