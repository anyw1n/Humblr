package com.example.humblr.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.humblr.data.room.AppDatabase
import com.example.humblr.ui.screens.subreddits.SubredditsType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubredditRepository @Inject constructor(
    private val api: Api,
    db: AppDatabase
) {
    private val dao = db.subredditDao()

    fun getSubredditsFlow(subredditsType: SubredditsType) = Pager(PagingConfig(25)) {
        SubredditPagingSource(api, subredditsType)
    }.flow
}
