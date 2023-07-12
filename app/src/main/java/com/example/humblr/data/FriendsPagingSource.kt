package com.example.humblr.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.humblr.data.model.User

class FriendsPagingSource(
    private val api: Api
) : PagingSource<String, User>() {

    override fun getRefreshKey(state: PagingState<String, User>) =
        state.anchorPosition?.let { state.closestItemToPosition(it)?.name }

    override suspend fun load(params: LoadParams<String>) = runCatching {
        val after = if (params !is LoadParams.Refresh) params.key else null
        val friends = api.getFriends(after).data.children.map { it.data }
        LoadResult.Page(
            data = friends,
            prevKey = null,
            nextKey = if (friends.isNotEmpty()) friends.last().name else null
        )
    }.getOrElse { LoadResult.Error(it) }
}
