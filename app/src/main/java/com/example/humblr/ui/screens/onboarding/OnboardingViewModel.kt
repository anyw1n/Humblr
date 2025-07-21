package com.example.humblr.ui.screens.onboarding

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.humblr.util.OnboardingCompleteKey
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: SharedPreferences
) : ViewModel() {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    fun complete() {
        prefs.edit {
            putBoolean(OnboardingCompleteKey, true)
        }
        uiState = uiState.copy(complete = true)
    }
}
