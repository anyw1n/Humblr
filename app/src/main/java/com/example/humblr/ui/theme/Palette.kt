package com.example.humblr.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

sealed class Palette {
    val primary = Color(0xFF5222D0)
    val secondary = Color(0xFFEC615B)
    val deselected = Color(0xFFC4C4C4)
    abstract val background: Color

    object Light : Palette() {
        override val background = Color(0xFFF8F6FC)
    }

    object Dark : Palette() {
        override val background = Color(0xFF27292D)
    }

    companion object {
        val current: Palette
            @Composable
            @ReadOnlyComposable
            get() = LocalPalette.current
    }
}

val LocalPalette: ProvidableCompositionLocal<Palette> = compositionLocalOf { Palette.Light }
