package com.example.humblr.ui.screens.favorite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.humblr.data.Api
import com.example.humblr.data.CommentRepository
import com.example.humblr.data.SubredditRepository
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.screens.subreddits.SubredditsType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val api: Api,
    private val subredditRepository: SubredditRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    var uiState by mutableStateOf(FavoriteUiState())
        private set

    private var username: String? = null

    init {
        loadUsername()
    }

    private fun loadUsername() = viewModelScope.launch {
        uiState = runCatching { api.getMe().name }.fold(
            onSuccess = {
                username = it
                uiState.copy(loading = false)
            },
            onFailure = { uiState.copy(loading = false, error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun setType(type: FavoriteType) {
        uiState = uiState.copy(type = type)
    }

    fun setFilter(filter: FavoriteFilter) {
        uiState = uiState.copy(filter = filter)
    }

    val allSubreddits by lazy {
        username?.let { SubredditsType.Saved(it) }?.let {
            subredditRepository.getAllSubredditsFlow(it)
                .cachedIn(viewModelScope)
        }
    }

    val localSubreddits = subredditRepository.localSubredditsFlow.cachedIn(viewModelScope)

    val allComments by lazy {
        username?.let { commentRepository.getSavedCommentsFlow(it).cachedIn(viewModelScope) }
    }

    val localComments = commentRepository.localCommentsFlow.cachedIn(viewModelScope)

    fun subscribe(subreddit: Subreddit) = viewModelScope.launch {
        uiState = runCatching {
            api.subscribe(if (subreddit.noFollow) "sub" else "unsub", subreddit.subreddit)
        }.fold(
            onSuccess = { uiState.copy(refresh = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun save(name: String) = viewModelScope.launch {
        uiState = runCatching { api.save(name) }.fold(
            onSuccess = { uiState.copy(refresh = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun unsave(name: String) = viewModelScope.launch {
        uiState = runCatching { api.unsave(name) }.fold(
            onSuccess = { uiState.copy(refresh = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun vote(name: String, direction: Int) = viewModelScope.launch {
        uiState = runCatching { api.vote(name, direction) }.fold(
            onSuccess = { uiState.copy(refresh = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun download(comment: Comment) = viewModelScope.launch { commentRepository.save(comment) }

    fun refreshed() {
        uiState = uiState.copy(refresh = false)
    }

    fun errorShown() {
        uiState = uiState.copy(error = null)
    }
}
