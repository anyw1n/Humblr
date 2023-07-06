package com.example.humblr.ui.screens.subreddit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.humblr.data.Api
import com.example.humblr.ui.common.SubredditsRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val api: Api,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = savedStateHandle[SubredditsRoutes.SubredditId]!!
}
