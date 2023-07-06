package com.example.humblr.ui.screens.subreddits

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.humblr.data.Api
import com.example.humblr.data.SubredditRepository
import com.example.humblr.data.model.Subreddit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SubredditsViewModel @Inject constructor(
    private val api: Api,
    private val repository: SubredditRepository
) : ViewModel() {
    var uiState by mutableStateOf(SubredditsUiState())
        private set

    val subreddits get() = repository.getSubredditsFlow(uiState.type).cachedIn(viewModelScope)

    fun changeType(type: SubredditsType) {
        uiState = uiState.copy(type = type)
    }

    fun subscribe(subreddit: Subreddit, index: Int) = viewModelScope.launch {
        val subscribe = subreddit.noFollow
        uiState = runCatching {
            api.subscribe(if (subscribe) "sub" else "unsub", subreddit.subreddit)
        }.fold(
            onSuccess = { uiState.copy(joinSubredditIndex = index) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun subscribed() { uiState = uiState.copy(joinSubredditIndex = null) }

    fun errorShown() {
        uiState = uiState.copy(error = null)
    }
}
