package com.example.humblr.util

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ThemedPreview
