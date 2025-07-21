package com.example.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.humblr.data.model.Comment
import com.example.humblr.ui.screens.favorite.CommentsType

class CommentPagingSource(
    private val api: Api,
    private val username: String,
    private val commentsType: CommentsType
) : PagingSource<String, Comment>() {

    override fun getRefreshKey(state: PagingState<String, Comment>) =
        state.anchorPosition?.let { state.closestItemToPosition(it)?.name }

    override suspend fun load(params: LoadParams<String>) = runCatching {
        val after = if (params !is LoadParams.Refresh) params.key else null
        val comments = when (commentsType) {
            CommentsType.Saved -> api.getUserSaved(username, "comments", after)
            CommentsType.Other -> api.getUserComments(username, after)
        }.data.children.map { it.data as Comment }
        LoadResult.Page(
            data = comments,
            prevKey = null,
            nextKey = if (comments.isNotEmpty()) comments.last().name else null
        )
    }.getOrElse { LoadResult.Error(it) }
}
