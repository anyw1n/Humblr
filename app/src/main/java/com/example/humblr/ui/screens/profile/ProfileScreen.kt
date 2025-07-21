package com.example.humblr.ui.screens.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.humblr.R
import com.example.humblr.data.Me
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles

data class ProfileUiState(
    val loading: Boolean = true,
    val me: Me? = null,
    val error: String? = null,
    val logout: Boolean = false
)

@Composable
fun ProfileScreen(showSnackbar: (String) -> Unit, onFriendsClick: () -> Unit, logout: () -> Unit) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.error) {
        if (uiState.error == null) return@LaunchedEffect
        showSnackbar(uiState.error)
        viewModel.errorShown()
    }

    LaunchedEffect(uiState.logout) {
        if (uiState.logout) logout()
    }

    Column(
        modifier = Modifier.padding(start = 31.dp, top = 70.dp, end = 29.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        val me = uiState.me ?: return
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = if (isSystemInDarkTheme()) Palette.current.background else Color.White
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(19.dp))
                AsyncImage(
                    model = me.iconImg,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(95.dp)
                )
                Surface(
                    modifier = Modifier.padding(top = 11.dp),
                    color = Palette.current.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(30)
                ) {
                    Text(
                        text = me.name,
                        style = TextStyles.display,
                        modifier = Modifier.padding(horizontal = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 11.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Palette.current.primary,
                contentColor = Color.White
            ),
            onClick = { onFriendsClick() }
        ) {
            Text(
                text = stringResource(R.string.friends_list),
                style = TextStyles.default,
                fontWeight = FontWeight.Medium
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Palette.current.primary,
                contentColor = Color.White
            ),
            onClick = { viewModel.clearSaved() }
        ) {
            Text(
                text = stringResource(R.string.clear_saved),
                style = TextStyles.default,
                fontWeight = FontWeight.Medium
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 51.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Palette.current.secondary,
                contentColor = Color.White
            ),
            onClick = { viewModel.logout() }
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = TextStyles.default,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
