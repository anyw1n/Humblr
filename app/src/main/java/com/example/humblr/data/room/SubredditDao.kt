package com.example.humblr.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.humblr.data.model.Subreddit

@Dao
interface SubredditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subreddit: Subreddit)

    @Query("SELECT * FROM subreddits")
    fun pagingSource(): PagingSource<Int, Subreddit>

    @Query("DELETE FROM subreddits")
    suspend fun deleteAll()
}
