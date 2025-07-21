package com.example.humblr.ui.screens.friends

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.humblr.R
import com.example.humblr.ui.common.ErrorLoadState
import com.example.humblr.ui.common.LoadingLoadState
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendsScreen(navigateUp: () -> Unit, onUserClick: (String) -> Unit) {
    val viewModel = hiltViewModel<FriendsViewModel>()
    val friends = viewModel.flow.collectAsLazyPagingItems()

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp),
            color = Palette.current.primary
        ) {
            Box(contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.friends_list),
                    style = TextStyles.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalItemSpacing = 11.dp
        ) {
            items(
                count = friends.itemCount,
                key = friends.itemKey(key = { it.name }),
                contentType = friends.itemContentType()
            ) { index ->
                friends[index]?.let {
                    Surface(
                        modifier = Modifier.clickable { onUserClick(it.name) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSystemInDarkTheme()) {
                            Palette.current.background
                        } else {
                            Color.White
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 35.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = it.snoovatarImg,
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(65.dp)
                            )
                            Surface(
                                modifier = Modifier.padding(start = 13.dp),
                                color = Palette.current.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(30)
                            ) {
                                Text(
                                    text = it.name,
                                    style = TextStyles.display,
                                    modifier = Modifier.padding(horizontal = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(35.dp))
                        }
                    }
                }
            }

            when (val state = friends.loadState.refresh) {
                is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                    ErrorLoadState(state.error, friends::retry)
                }

                is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
                    LoadingLoadState()
                }

                is LoadState.NotLoading -> if (friends.itemCount == 0) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(
                            text = stringResource(R.string.no_items_found),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(all = 16.dp)
                        )
                    }
                }
            }

            when (val state = friends.loadState.append) {
                is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                    ErrorLoadState(state.error, friends::retry)
                }

                is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) {
                    LoadingLoadState()
                }

                else -> Unit
            }
        }
    }
}
