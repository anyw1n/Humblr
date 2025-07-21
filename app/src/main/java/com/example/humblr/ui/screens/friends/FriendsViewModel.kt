package com.example.humblr.ui.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.humblr.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(repository: UserRepository) : ViewModel() {

    val flow = repository.friendsFlow.cachedIn(viewModelScope)
}
