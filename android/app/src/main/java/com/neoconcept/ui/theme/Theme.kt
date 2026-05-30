package com.neoconcept.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Red100 = Color(0xFFFF3000)
val Red5Tint = Color(0xFFFFE0D0)
val Green = Color(0xFF00C853)
val Gray10 = Color(0xFF1A1A1A)
val Gray60 = Color(0xFF999999)
val Gray90 = Color(0xFFE5E5E5)
val Gray95 = Color(0xFFF2F2F2)

val LightColorScheme = lightColorScheme(
    primary = Black,
    onPrimary = White,
    primaryContainer = Black,
    onPrimaryContainer = White,
    secondary = Red100,
    onSecondary = White,
    secondaryContainer = Red5Tint,
    onSecondaryContainer = Black,
    tertiary = Green,
    onTertiary = White,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray10,
    outline = Gray90,
    outlineVariant = Gray60,
    error = Red100,
    onError = White,
)

@Composable
fun NeoConceptTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
