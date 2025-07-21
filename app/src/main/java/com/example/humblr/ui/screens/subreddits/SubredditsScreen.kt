package com.example.humblr.ui.screens.subreddits

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.humblr.R
import com.example.humblr.ui.common.ErrorLoadState
import com.example.humblr.ui.common.LoadingLoadState
import com.example.humblr.ui.common.SubredditItem
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles

data class SubredditsUiState(
    val type: SubredditsType = SubredditsType.New,
    val joinSubredditIndex: Int? = null,
    val error: String? = null
)

sealed class SubredditsType {
    @get:StringRes
    abstract val title: Int

    object Popular : SubredditsType() {
        override val title = R.string.popular
    }

    object New : SubredditsType() {
        override val title = R.string.new_subreddit
    }

    data class Search(val query: String) : SubredditsType() {
        override val title = R.string.search
    }

    data class Saved(val username: String) : SubredditsType() {
        override val title = R.string.saved
    }
}

@Composable
fun SubredditsScreen(showSnackbar: (String) -> Unit, onSubredditClick: (String) -> Unit) {
    val viewModel = hiltViewModel<SubredditsViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.error) {
        if (uiState.error == null) return@LaunchedEffect
        showSnackbar(uiState.error)
        viewModel.errorShown()
    }

    LaunchedEffect(uiState.joinSubredditIndex) {
        if (uiState.joinSubredditIndex == null) return@LaunchedEffect
        viewModel.subscribed()
    }

    Column(modifier = Modifier.padding(horizontal = 30.dp)) {
        SearchBar {
            viewModel.changeType(
                if (it.isNotBlank()) SubredditsType.Search(it) else SubredditsType.New
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            listOf(SubredditsType.New, SubredditsType.Popular).forEach {
                Text(
                    text = stringResource(it.title),
                    style = TextStyles.default.copy(
                        color = if (uiState.type == it) {
                            Palette.current.primary
                        } else {
                            if (isSystemInDarkTheme()) {
                                Color.White
                            } else {
                                Color.Black.copy(alpha = 0.5f)
                            }
                        },
                        fontWeight = if (uiState.type == it) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.changeType(it) }
                )
            }
        }

        val subreddits = viewModel.subreddits.collectAsLazyPagingItems()

        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            items(
                count = subreddits.itemCount,
                key = subreddits.itemKey(key = { it.name }),
                contentType = subreddits.itemContentType()
            ) { index ->
                subreddits[index]?.let {
                    SubredditItem(it, { viewModel.subscribe(it, index) }, onSubredditClick)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            when (val state = subreddits.loadState.refresh) {
                is LoadState.Error -> item { ErrorLoadState(state.error, subreddits::retry) }
                is LoadState.Loading -> item { LoadingLoadState() }
                is LoadState.NotLoading -> if (subreddits.itemCount == 0) {
                    item {
                        Text(
                            text = stringResource(R.string.no_items_found),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(all = 16.dp)
                        )
                    }
                }
            }

            when (val state = subreddits.loadState.append) {
                is LoadState.Error -> item { ErrorLoadState(state.error, subreddits::retry) }
                is LoadState.Loading -> item { LoadingLoadState() }
                else -> Unit
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(top = 27.dp),
        value = query,
        onValueChange = { query = it },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null
            )
        },
        placeholder = { Text(text = stringResource(R.string.search)) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(query)
            keyboardController?.hide()
            focusManager.clearFocus()
        }),
        shape = RoundedCornerShape(50),
        colors = if (isSystemInDarkTheme()) {
            TextFieldDefaults.colors(
                unfocusedContainerColor = Palette.current.background,
                unfocusedIndicatorColor = Palette.current.primary,
                focusedContainerColor = Palette.current.background,
                focusedIndicatorColor = Palette.current.primary
            )
        } else {
            TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                unfocusedIndicatorColor = Palette.current.primary,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Palette.current.primary
            )
        }
    )
}
