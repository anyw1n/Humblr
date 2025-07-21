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
        val after = if (params !is LoadParams.Refresh) params.key else null
        val subreddits = when (type) {
            SubredditsType.Popular -> api.getPopularSubreddits(after)
            SubredditsType.New -> api.getNewSubreddits(after)
            is SubredditsType.Search -> api.searchSubreddits(after, type.query)
            is SubredditsType.Saved -> api.getUserSaved(type.username, "links", after)
        }.data.children.map { it.data as Subreddit }
        LoadResult.Page(
            data = subreddits,
            prevKey = null,
            nextKey = if (subreddits.isNotEmpty()) subreddits.last().name else null
        )
    }.getOrElse { LoadResult.Error(it) }
}
