package com.example.humblr.ui.screens.subreddit

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.humblr.R
import com.example.humblr.data.model.Comment
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

data class SubredditUiState(
    val loading: Boolean = true,
    val subreddit: Subreddit? = null,
    val comments: List<Comment> = emptyList(),
    val error: String? = null
)

@Composable
fun SubredditScreen(showSnackbar: (String) -> Unit, navigateUp: () -> Unit) {
    val viewModel = hiltViewModel<SubredditViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.error) {
        if (uiState.error == null) return@LaunchedEffect
        showSnackbar(uiState.error)
        viewModel.errorShown()
    }

    Column {
        if (uiState.loading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        val subreddit = uiState.subreddit ?: return@Column
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        Surface(modifier = Modifier.fillMaxWidth().height(53.dp), color = Palette.current.primary) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Text(
                    text = subreddit.title,
                    maxLines = 2,
                    style = TextStyles.displaySmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 8.dp),
            state = listState
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(
                                    id = if (subreddit.noFollow) {
                                        R.drawable.join
                                    } else {
                                        R.drawable.joined
                                    }
                                ),
                                contentDescription = null,
                                tint = (
                                    if (subreddit.noFollow) {
                                        Palette.current.primary
                                    } else {
                                        Palette.current.secondary
                                    }
                                    ).copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp).clickable { viewModel.subscribe() }
                            )
                            Text(
                                text = subreddit.author,
                                style = TextStyles.title.copy(color = Palette.current.primary),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = SimpleDateFormat.getTimeInstance()
                                    .format(Date(subreddit.created.toLong())),
                                style = TextStyles.default.copy(
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        }
                        AsyncImage(
                            model = subreddit.url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().padding(top = 21.dp)
                        )
                        MarkdownText(
                            markdown = subreddit.selftext,
                            style = TextStyles.default
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(
                                text = "${subreddit.numComments} комментария",
                                style = TextStyles.default.copy(
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            val context = LocalContext.current
                            Text(
                                text = "Поделиться",
                                style = TextStyles.default.copy(
                                    color = Color.Black.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.clickable {
                                    val intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, subreddit.url)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(intent, null))
                                }
                            )
                            Text(
                                text = subreddit.ups.toString(),
                                style = TextStyles.default.copy(
                                    color = Color.Black.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            if (subreddit.saved) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(15.dp).padding(start = 2.dp)
                                        .clickable { viewModel.unsave(subreddit.name) }
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.favorite),
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(15.dp).padding(start = 2.dp)
                                        .clickable { viewModel.save(subreddit.name) }
                                )
                            }
                        }
                    }
                }
            }

            items(uiState.comments) {
                Surface(
                    modifier = Modifier.padding(top = 11.dp),
                    shape = RoundedCornerShape(5.dp),
                    color = Color.White
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
                                    color = Color.Black.copy(alpha = 0.5f)
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
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp).clickable {
                                    viewModel.vote(it.name, if (it.likes == true) 0 else 1)
                                }
                            )
                            Text(
                                text = it.score.toString(),
                                style = TextStyles.default.copy(
                                    color = Color.Black.copy(alpha = 0.5f)
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
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp).clickable {
                                    viewModel.vote(it.name, if (it.likes == false) 0 else -1)
                                }
                            )
                            Row(
                                modifier = Modifier.padding(start = 16.dp).clickable { },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.download),
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = stringResource(R.string.download),
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = TextStyles.default.copy(
                                        color = Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (it.saved) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { viewModel.unsave(it.name) }
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.favorite),
                                    contentDescription = null,
                                    tint = Color.Black.copy(alpha = 0.5f),
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
    }
}
