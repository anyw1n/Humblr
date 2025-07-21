package com.example.humblr.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.humblr.data.Api
import com.example.humblr.data.CredentialsRepository
import com.example.humblr.data.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: Api,
    private val credentialsRepository: CredentialsRepository,
    private val db: AppDatabase
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadMe()
    }

    private fun loadMe() = viewModelScope.launch {
        uiState = runCatching { api.getMe() }.fold(
            onSuccess = { uiState.copy(loading = false, me = it) },
            onFailure = { uiState.copy(loading = false, error = it.localizedMessage ?: "Ошибка") }
        )
    }

    fun clearSaved() = viewModelScope.launch(Dispatchers.IO) { db.clearAllTables() }

    fun logout() = viewModelScope.launch(Dispatchers.IO) {
        db.clearAllTables()
        credentialsRepository.token = null
        uiState = uiState.copy(logout = true)
    }

    fun errorShown() {
        uiState = uiState.copy(error = null)
    }
}
