package com.example.humblr.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.humblr.R
import com.example.humblr.data.model.Subreddit
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun SubredditItem(subreddit: Subreddit, onSubscribe: () -> Unit, onClick: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(5.dp),
        color = if (isSystemInDarkTheme()) Palette.current.background else Color.White,
        modifier = Modifier.clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(vertical = 19.dp)
                    .padding(start = 12.dp, end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.text_behind),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        colorFilter = ColorFilter.tint(
                            color = (
                                if (subreddit.noFollow) {
                                    Palette.current.primary
                                } else {
                                    Palette.current.secondary
                                }
                                ).copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )
                    Text(
                        text = subreddit.title,
                        style = TextStyles.title,
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    painter = painterResource(
                        id = if (subreddit.noFollow) R.drawable.join else R.drawable.joined
                    ),
                    contentDescription = null,
                    tint = (
                        if (subreddit.noFollow) {
                            Palette.current.primary
                        } else {
                            Palette.current.secondary
                        }
                        ).copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 8.dp)
                        .clickable { onSubscribe() }
                )
            }
            if (expanded) {
                if (subreddit.selftext.isBlank()) {
                    AsyncImage(
                        model = subreddit.url,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 22.dp)
                            .fillMaxWidth()
                    )
                } else if (!subreddit.url.endsWith("jpg")) {
                    MarkdownText(
                        markdown = subreddit.selftext,
                        style = TextStyles.default,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = Modifier.padding(start = 12.dp, end = 22.dp)
                    )
                } else {
                    Row {
                        AsyncImage(
                            model = subreddit.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(75.dp)
                        )
                        MarkdownText(
                            markdown = subreddit.selftext,
                            style = TextStyles.default,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = Modifier.padding(start = 8.dp, end = 22.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 22.dp)
                        .padding(top = 19.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subreddit.author,
                        style = TextStyles.default.copy(color = Palette.current.primary)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.clickable { onClick(subreddit.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subreddit.numComments.toString(),
                            style = TextStyles.default.copy(color = Palette.current.primary)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.comments),
                            contentDescription = null,
                            tint = Palette.current.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(19.dp))
        }
    }
}
