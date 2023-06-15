package com.example.somewhere.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorPalette = lightColors(
    primary = primary,
    primaryVariant = point,
    secondary = secondary,
    secondaryVariant = line,
    background = background,
    surface = surface,
    error = error,

    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
)

private val DarkColorPalette = darkColors(
    primary = primary_,
    primaryVariant = point,
    secondary = secondary_,
    secondaryVariant = line,
    background = background_,
    surface = surface_,
    error = error_,

    onPrimary = onPrimary_,
    onSecondary = onSecondary_,
    onBackground = onBackground_,
    onSurface = onSurface_,
    onError = onError_
)

@Composable
fun SomewhereTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )

    //status bar color = transparent
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )
    }
}