package com.example.multisportdraftbuilder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

@Composable
fun MultiSportDraftBuilderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DraftBuilderDarkScheme,
        typography = Typography,
        content = content
    )
}
