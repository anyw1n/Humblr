package com.example.humblr.ui.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.humblr.R
import com.example.humblr.data.model.User
import com.example.humblr.ui.common.ErrorLoadState
import com.example.humblr.ui.common.LoadingLoadState
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import java.text.SimpleDateFormat
import java.util.Date

data class UserUiState(
    val loading: Boolean = true,
    val user: User? = null,
    val refreshComments: Boolean = false,
    val error: String? = null
)

@Composable
fun UserScreen(showSnackbar: (String) -> Unit) {
    val viewModel = hiltViewModel<UserViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.error) {
        if (uiState.error == null) return@LaunchedEffect
        showSnackbar(uiState.error)
        viewModel.errorShown()
    }

    val user = uiState.user ?: return
    val comments = viewModel.comments.collectAsLazyPagingItems()

    LaunchedEffect(uiState.refreshComments) {
        if (!uiState.refreshComments) return@LaunchedEffect
        comments.refresh()
        viewModel.refreshed()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 23.dp, end = 37.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.padding(top = 25.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isSystemInDarkTheme()) Palette.current.background else Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 9.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.snoovatarImg,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(95.dp)
                    )
                    Surface(
                        modifier = Modifier.padding(start = 13.dp),
                        color = Palette.current.primary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(30)
                    ) {
                        Text(
                            text = user.name,
                            style = TextStyles.display,
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 11.dp).clickable { viewModel.subscribe() },
                shape = RoundedCornerShape(50),
                color = if (user.isFriend) Palette.current.secondary else Palette.current.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (user.isFriend) "В друзьях" else "Подписаться",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        painter = painterResource(
                            id = if (user.isFriend) R.drawable.joined else R.drawable.join
                        ),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        items(
            count = comments.itemCount,
            key = comments.itemKey(key = { it.name }),
            contentType = comments.itemContentType()
        ) { index ->
            comments[index]?.let {
                Surface(
                    modifier = Modifier.padding(top = 11.dp),
                    shape = RoundedCornerShape(5.dp),
                    color = if (isSystemInDarkTheme()) Palette.current.background else Color.White
                ) {
                    Column(modifier = Modifier.padding(start = 14.dp, end = 21.dp)) {
                        Row(modifier = Modifier.padding(top = 7.dp)) {
                            Text(
                                text = it.author,
                                style = TextStyles.title.copy(color = Palette.current.primary)
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
                                        viewModel.vote(it.name, if (it.likes == true) 0 else 1)
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
                                        viewModel.vote(it.name, if (it.likes == false) 0 else -1)
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
