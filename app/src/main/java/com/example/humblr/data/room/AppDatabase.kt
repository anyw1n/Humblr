package com.example.humblr.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.humblr.data.model.Subreddit

@Database(
    entities = [Subreddit::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subredditDao(): SubredditDao

    companion object {

        fun create(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db").build()
    }
}
