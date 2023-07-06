package com.example.humblr.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.humblr.data.CredentialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    credentialsRepository: CredentialsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val query: String = savedStateHandle["query"]!!

    init {
        val params = query
            .split("&")
            .associate { with(it.split("=")) { component1() to component2() } }
        credentialsRepository.token = params["access_token"]
        uiState = uiState.copy(userLoggedIn = true)
    }
}
