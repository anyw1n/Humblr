package com.example.humblr.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: Api
) {
    val friendsFlow = Pager(PagingConfig(25)) {
        FriendsPagingSource(api)
    }.flow
}
