package com.example.humblr.ui.screens.favorite

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.humblr.R
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.common.ErrorLoadState
import com.example.humblr.ui.common.LoadingLoadState
import com.example.humblr.ui.common.SubredditItem
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import java.text.SimpleDateFormat
import java.util.Date

data class FavoriteUiState(
    val loading: Boolean = true,
    val type: FavoriteType = FavoriteType.Subreddits,
    val filter: FavoriteFilter = FavoriteFilter.All,
    val refresh: Boolean = false,
    val error: String? = null
)

enum class CommentsType { Saved, Other }

enum class FavoriteType(@StringRes val title: Int) {
    Subreddits(R.string.subreddits), Comments(R.string.comments)
}

enum class FavoriteFilter(@StringRes val title: Int) {
    All(R.string.all), Saved(R.string.saved)
}

@Composable
fun FavoriteScreen(
    showSnackbar: (String) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val viewModel = hiltViewModel<FavoriteViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.error) {
        if (uiState.error == null) return@LaunchedEffect
        showSnackbar(uiState.error)
        viewModel.errorShown()
    }

    Column(modifier = Modifier.padding(start = 29.dp, top = 25.dp, end = 31.dp)) {
        Surface(
            modifier = Modifier.height(26.dp),
            shape = RoundedCornerShape(50),
            color = if (isSystemInDarkTheme()) Palette.current.background else Color.White
        ) {
            Row {
                FavoriteType.values().forEach {
                    Surface(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable {
                            viewModel.setType(it)
                        },
                        shape = RoundedCornerShape(50),
                        color = if (it == uiState.type) {
                            Palette.current.primary
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(it.title),
                                textAlign = TextAlign.Center,
                                style = TextStyles.title,
                                color = if (it == uiState.type || isSystemInDarkTheme()) {
                                    Color.White
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.padding(top = 8.dp).height(26.dp),
            shape = RoundedCornerShape(50),
            color = if (isSystemInDarkTheme()) Palette.current.background else Color.White
        ) {
            Row {
                FavoriteFilter.values().forEach {
                    Surface(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable {
                            viewModel.setFilter(it)
                        },
                        shape = RoundedCornerShape(50),
                        color = if (it == uiState.filter) {
                            Palette.current.primary
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(it.title),
                                textAlign = TextAlign.Center,
                                style = TextStyles.title,
                                color = if (it == uiState.filter || isSystemInDarkTheme()) {
                                    Color.White
                                } else {
                                    Color.Black.copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (uiState.loading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            return
        }

        var subreddits: LazyPagingItems<Subreddit>? = null
        var comments: LazyPagingItems<Comment>? = null

        when (uiState.type) {
            FavoriteType.Subreddits -> subreddits = when (uiState.filter) {
                FavoriteFilter.All -> viewModel.allSubreddits
                FavoriteFilter.Saved -> viewModel.localSubreddits
            }?.collectAsLazyPagingItems()

            FavoriteType.Comments -> comments = when (uiState.filter) {
                FavoriteFilter.All -> viewModel.allComments
                FavoriteFilter.Saved -> viewModel.localComments
            }?.collectAsLazyPagingItems()
        }

        LaunchedEffect(uiState.refresh) {
            if (!uiState.refresh) return@LaunchedEffect
            when (uiState.type) {
                FavoriteType.Subreddits -> subreddits?.refresh()
                FavoriteType.Comments -> comments?.refresh()
            }
            viewModel.refreshed()
        }

        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            when (uiState.type) {
                FavoriteType.Subreddits -> {
                    subreddits ?: return@LazyColumn

                    items(
                        count = subreddits.itemCount,
                        key = subreddits.itemKey(key = { it.name }),
                        contentType = subreddits.itemContentType()
                    ) { index ->
                        subreddits[index]?.let {
                            SubredditItem(it, { viewModel.subscribe(it) }, onSubredditClick)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    when (val state = subreddits.loadState.refresh) {
                        is LoadState.Error -> item {
                            ErrorLoadState(state.error, subreddits::retry)
                        }

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
                        is LoadState.Error -> item {
                            ErrorLoadState(state.error, subreddits::retry)
                        }

                        is LoadState.Loading -> item { LoadingLoadState() }
                        else -> Unit
                    }
                }

                FavoriteType.Comments -> {
                    comments ?: return@LazyColumn

                    items(
                        count = comments.itemCount,
                        key = comments.itemKey(key = { it.name }),
                        contentType = comments.itemContentType()
                    ) { index ->
                        comments[index]?.let {
                            Surface(
                                modifier = Modifier.padding(top = 11.dp),
                                shape = RoundedCornerShape(5.dp),
                                color = if (isSystemInDarkTheme()) {
                                    Palette.current.background
                                } else {
                                    Color.White
                                }
                            ) {
                                Column(modifier = Modifier.padding(start = 14.dp, end = 21.dp)) {
                                    Row(modifier = Modifier.padding(top = 7.dp)) {
                                        Text(
                                            text = it.author,
                                            style = TextStyles.title.copy(
                                                color = Palette.current.primary
                                            ),
                                            modifier = Modifier.clickable { onUserClick(it.author) }
                                        )
                                        Text(
                                            text = SimpleDateFormat.getTimeInstance()
                                                .format(Date(it.created.toLong())),
                                            style = TextStyles.default.copy(
                                                color = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black.copy(alpha = 0.5f)
                                                }
                                            ),
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }
                                    Text(
                                        text = it.body,
                                        style = TextStyles.default,
                                        modifier = Modifier.padding(top = 13.dp)
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 11.dp, bottom = 18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                id = if (it.likes == true) {
                                                    R.drawable.up_filled
                                                } else {
                                                    R.drawable.up_outllined
                                                }
                                            ),
                                            contentDescription = null,
                                            tint = if (isSystemInDarkTheme()) {
                                                Color.White
                                            } else {
                                                Color.Black.copy(
                                                    alpha = 0.5f
                                                )
                                            },
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clickable {
                                                    viewModel.vote(
                                                        it.name,
                                                        if (it.likes == true) 0 else 1
                                                    )
                                                }
                                        )
                                        Text(
                                            text = it.score.toString(),
                                            style = TextStyles.default.copy(
                                                color = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black.copy(
                                                        alpha = 0.5f
                                                    )
                                                }
                                            ),
                                            modifier = Modifier.padding(horizontal = 2.dp)
                                        )
                                        Icon(
                                            painter = painterResource(
                                                id = if (it.likes == false) {
                                                    R.drawable.down_filled
                                                } else {
                                                    R.drawable.down_outlined
                                                }
                                            ),
                                            contentDescription = null,
                                            tint = if (isSystemInDarkTheme()) {
                                                Color.White
                                            } else {
                                                Color.Black.copy(
                                                    alpha = 0.5f
                                                )
                                            },
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clickable {
                                                    viewModel.vote(
                                                        it.name,
                                                        if (it.likes == false) 0 else -1
                                                    )
                                                }
                                        )
                                        Row(
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .clickable { viewModel.download(it) },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.download),
                                                contentDescription = null,
                                                tint = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black.copy(alpha = 0.5f)
                                                },
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = stringResource(R.string.download),
                                                modifier = Modifier.padding(start = 2.dp),
                                                style = TextStyles.default.copy(
                                                    color = if (isSystemInDarkTheme()) {
                                                        Color.White
                                                    } else {
                                                        Color.Black.copy(alpha = 0.5f)
                                                    }
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (it.saved) {
                                            Icon(
                                                Icons.Filled.Favorite,
                                                contentDescription = null,
                                                tint = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black.copy(alpha = 0.5f)
                                                },
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { viewModel.unsave(it.name) }
                                            )
                                        } else {
                                            Icon(
                                                painter = painterResource(id = R.drawable.favorite),
                                                contentDescription = null,
                                                tint = if (isSystemInDarkTheme()) {
                                                    Color.White
                                                } else {
                                                    Color.Black.copy(alpha = 0.5f)
                                                },
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable { viewModel.save(it.name) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    when (val state = comments.loadState.refresh) {
                        is LoadState.Error -> item { ErrorLoadState(state.error, comments::retry) }
                        is LoadState.Loading -> item { LoadingLoadState() }
                        is LoadState.NotLoading -> if (comments.itemCount == 0) {
                            item {
                                Text(
                                    text = stringResource(R.string.no_items_found),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(all = 16.dp)
                                )
                            }
                        }
                    }

                    when (val state = comments.loadState.append) {
                        is LoadState.Error -> item { ErrorLoadState(state.error, comments::retry) }
                        is LoadState.Loading -> item { LoadingLoadState() }
                        else -> Unit
                    }
                }
            }
        }
    }
}
