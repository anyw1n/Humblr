package com.example.humblr.ui.screens.subreddit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.humblr.data.Api
import com.example.humblr.data.CommentRepository
import com.example.humblr.data.SubredditRepository
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.common.SubredditsRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SubredditViewModel @Inject constructor(
    private val api: Api,
    private val subredditRepository: SubredditRepository,
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = savedStateHandle[SubredditsRoutes.SubredditId]!!

    var uiState by mutableStateOf(SubredditUiState())
        private set

    init {
        loadSubreddit()
    }

    private fun loadSubreddit() = viewModelScope.launch {
        uiState = runCatching { api.getComments(id) }.fold(
            onSuccess = { response ->
                uiState.copy(
                    loading = false,
                    subreddit = response.first().data.children.first().data as? Subreddit,
                    comments = response.last().data.children.mapNotNull { it.data as? Comment }
                )
            },
            onFailure = {
                uiState.copy(
                    loading = false,
                    error = it.localizedMessage ?: "Ошибка"
                )
            }
        )
    }

    fun subscribe() = viewModelScope.launch {
        val subreddit = uiState.subreddit ?: return@launch
        runCatching {
            api.subscribe(if (subreddit.noFollow) "sub" else "unsub", subreddit.subreddit)
        }.fold(
            onSuccess = { loadSubreddit() },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun saveSubreddit() = viewModelScope.launch {
        val subreddit = uiState.subreddit ?: return@launch
        runCatching { api.save(subreddit.name) }.fold(
            onSuccess = {
                subredditRepository.save(subreddit)
                loadSubreddit()
            },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun save(name: String) = viewModelScope.launch {
        runCatching { api.save(name) }.fold(
            onSuccess = { loadSubreddit() },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun unsave(name: String) = viewModelScope.launch {
        runCatching { api.unsave(name) }.fold(
            onSuccess = { loadSubreddit() },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun vote(name: String, direction: Int) = viewModelScope.launch {
        runCatching { api.vote(name, direction) }.fold(
            onSuccess = { loadSubreddit() },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun download(comment: Comment) = viewModelScope.launch { commentRepository.save(comment) }

    fun errorShown() {
        uiState = uiState.copy(error = null)
    }
}
