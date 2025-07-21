package com.example.humblr.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.humblr.R

val Typography = Typography(
    displayMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    displaySmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp
    )
)

object TextStyles {
    val appName = TextStyle(
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily(
            Font(
                R.font.montserrat_black,
                weight = FontWeight.Black
            )
        ),
        color = Palette.Light.primary
    )
    val display = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
    val displaySmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    val title = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
    val default = TextStyle(
        fontSize = 14.sp
    )
    val bodySmall = TextStyle(
        fontSize = 12.sp
    )
}
