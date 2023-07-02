package com.example.humblr.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.humblr.R
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import com.example.humblr.util.OnboardingSteps
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val complete: Boolean = false
)

private val Images = listOf(
    R.drawable.onboarding_first,
    R.drawable.onboarding_second,
    R.drawable.onboarding_third
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(complete: () -> Unit) {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val uiState = viewModel.uiState
    val pagerState = rememberPagerState(0)

    LaunchedEffect(uiState.complete) {
        if (uiState.complete) complete()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .padding(top = 28.dp),
        color = Palette.current.background
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Palette.current.primary
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Text(
                    stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily(
                        Font(
                            R.font.montserrat_black,
                            weight = FontWeight.Black
                        )
                    ),
                    color = Palette.current.primary,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    stringResource(
                        if (pagerState.canScrollForward) R.string.skip else R.string.ready
                    ),
                    color = Palette.current.primary.copy(alpha = 0.5f),
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { viewModel.complete() }
                )
            }

            HorizontalPager(
                modifier = Modifier.weight(1f),
                pageCount = OnboardingSteps,
                state = pagerState
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 35.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = Images[it]),
                        contentDescription = null
                    )
                    Text(
                        text = stringArrayResource(id = R.array.onboarding_title)[it],
                        style = TextStyles.display,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    )
                    Text(
                        text = stringArrayResource(id = R.array.onboarding_message)[it],
                        style = TextStyles.default,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 7.dp)
                    )
                }
            }

            val coroutineScope = rememberCoroutineScope()
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 56.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(OnboardingSteps) {
                    Surface(
                        modifier = Modifier
                            .size(8.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                if (pagerState.currentPage != it) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(it)
                                    }
                                }
                            },
                        shape = CircleShape,
                        color = if (pagerState.currentPage == it) {
                            Palette.current.primary
                        } else {
                            Palette.current.deselected
                        }
                    ) {}
                    if (it != OnboardingSteps - 1) Spacer(modifier = Modifier.width(7.dp))
                }
            }
        }
    }
}
