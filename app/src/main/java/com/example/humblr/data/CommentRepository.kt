package com.example.humblr.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.humblr.data.model.Comment
import com.example.humblr.data.room.AppDatabase
import com.example.humblr.ui.screens.favorite.CommentsType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val api: Api,
    db: AppDatabase
) {
    private val dao = db.commentDao()

    val localCommentsFlow = Pager(PagingConfig(25)) { dao.pagingSource() }.flow

    fun getUserCommentsFlow(username: String) = Pager(PagingConfig(25)) {
        CommentPagingSource(api, username, CommentsType.Other)
    }.flow

    fun getSavedCommentsFlow(username: String) = Pager(PagingConfig(25)) {
        CommentPagingSource(api, username, CommentsType.Saved)
    }.flow

    suspend fun save(comment: Comment) = dao.insert(comment)
}
