package com.example.humblr.ui.screens.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.humblr.data.Api
import com.example.humblr.data.CommentRepository
import com.example.humblr.data.model.Comment
import com.example.humblr.ui.common.SubredditsRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val api: Api,
    private val repository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val username: String = savedStateHandle[SubredditsRoutes.Username]!!

    var uiState by mutableStateOf(UserUiState())
        private set

    val comments by lazy { repository.getUserCommentsFlow(username).cachedIn(viewModelScope) }

    init {
        loadUser()
    }

    private fun loadUser() = viewModelScope.launch {
        uiState = runCatching { api.getUser(username) }.fold(
            onSuccess = { response ->
                uiState.copy(
                    loading = false,
                    user = response.data
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
        runCatching {
            api.subscribe(
                if (uiState.user?.isFriend == true) "unsub" else "sub",
                "u_$username"
            )
        }.fold(
            onSuccess = { loadUser() },
            onFailure = { uiState = uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun save(name: String) = viewModelScope.launch {
        uiState = runCatching { api.save(name) }.fold(
            onSuccess = { uiState.copy(refreshComments = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun unsave(name: String) = viewModelScope.launch {
        uiState = runCatching { api.unsave(name) }.fold(
            onSuccess = { uiState.copy(refreshComments = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun vote(name: String, direction: Int) = viewModelScope.launch {
        uiState = runCatching { api.vote(name, direction) }.fold(
            onSuccess = { uiState.copy(refreshComments = true) },
            onFailure = { uiState.copy(error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun download(comment: Comment) = viewModelScope.launch { repository.save(comment) }

    fun refreshed() {
        uiState = uiState.copy(refreshComments = false)
    }

    fun errorShown() {
        uiState = uiState.copy(error = null)
    }
}
