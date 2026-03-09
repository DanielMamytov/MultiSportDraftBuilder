package com.example.multisportdraftbuilder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DraftBuilderDarkScheme = darkColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    tertiary = Info,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorColor
)

private val DraftBuilderLightScheme = lightColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    tertiary = Info,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorColor
)

@Composable
fun MultiSportDraftBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DraftBuilderDarkScheme else DraftBuilderLightScheme,
        typography = Typography,
        content = content
    )
}
