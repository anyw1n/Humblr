package com.example.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.screens.subreddits.SubredditsType

class SubredditPagingSource(
    private val api: Api,
    private val type: SubredditsType
) : PagingSource<String, Subreddit>() {

    override fun getRefreshKey(state: PagingState<String, Subreddit>) =
        state.anchorPosition?.let { state.closestItemToPosition(it)?.name }

    override suspend fun load(params: LoadParams<String>) = runCatching {
        val subreddits = when (type) {
            SubredditsType.Popular -> api.getPopularSubreddits(params.key)
            SubredditsType.New -> api.getNewSubreddits(params.key)
            is SubredditsType.Search -> api.searchSubreddits(params.key, type.query)
        }.data.children.map { it.data }
        LoadResult.Page(
            data = subreddits,
            prevKey = null,
            nextKey = if (subreddits.isNotEmpty()) subreddits.last().name else null
        )
    }.getOrElse { LoadResult.Error(it) }
}
