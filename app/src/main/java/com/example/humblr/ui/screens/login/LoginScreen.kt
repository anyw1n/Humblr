package com.example.humblr.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.humblr.R
import com.example.humblr.ui.theme.HumblrTheme
import com.example.humblr.ui.theme.Palette
import com.example.humblr.ui.theme.TextStyles
import com.example.humblr.util.ThemedPreview

@Composable
fun LoginScreen(login: () -> Unit) {
    Scaffold(containerColor = Palette.current.background) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Palette.current.primary,
                        Color.White
                    ),
                    endY = with(LocalDensity.current) { 444.dp.toPx() }
                )
            ).padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 68.dp).padding(top = 38.dp)
            )
            Text(
                stringResource(id = R.string.app_name),
                fontSize = 40.sp,
                style = TextStyles.appName,
                modifier = Modifier.padding(top = 88.dp)
            )
            Text(
                stringResource(R.string.app_description),
                style = TextStyles.default,
                modifier = Modifier.padding(top = 5.dp)
            )
            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 55.dp).padding(top = 60.dp)
                    .height(57.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Palette.current.primary,
                    contentColor = Color.White
                ),
                onClick = login
            ) {
                Text(
                    "Войти",
                    style = TextStyles.default,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@ThemedPreview
@Composable
fun LoginScreenPreview() {
    HumblrTheme {
        LoginScreen {}
    }
}
