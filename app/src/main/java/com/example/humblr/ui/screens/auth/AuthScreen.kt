package com.example.humblr.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

data class AuthUiState(
    val userLoggedIn: Boolean = false
)

@Composable
fun AuthScreen(complete: () -> Unit) {
    val viewModel = hiltViewModel<AuthViewModel>()

    LaunchedEffect(viewModel.uiState.userLoggedIn) {
        if (viewModel.uiState.userLoggedIn) complete()
    }
}
