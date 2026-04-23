package com.example.taldea5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = darkColorScheme(
    primary = BrandGold,
    secondary = BrandIvory,
    tertiary = BrandGold,
    background = BrandBlack,
    surface = BrandBlack,
    onPrimary = BrandBlack,
    onSecondary = BrandBlack,
    onTertiary = BrandBlack,
    onBackground = BrandIvory,
    onSurface = BrandIvory
)

@Composable
fun Taldea5Theme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}
