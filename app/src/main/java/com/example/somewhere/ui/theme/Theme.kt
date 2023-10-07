package com.example.somewhere.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController



private val AppLightColorScheme = lightColorScheme(
    primary = p40,
    onPrimary = p100,
    primaryContainer = p90,
    onPrimaryContainer = p10,
    inversePrimary = p80,

    secondary = s40,
    onSecondary = s100,
    secondaryContainer = s90,
    onSecondaryContainer = s10,

    tertiary = t40,
    onTertiary = t100,
    tertiaryContainer = t90,
    onTertiaryContainer = t10,

    error = e40,
    onError = e100,
    errorContainer = e90,
    onErrorContainer = e10,

    background = n95,           //background, dialog background
    onBackground = n0,

    surface = n93,              //dialog body background
    onSurface = n10,
    surfaceVariant = n97,       //card
    onSurfaceVariant = n10,
    inverseSurface = n08,
    inverseOnSurface = n90,
    surfaceTint = n99,

    outline = n50,
    outlineVariant = n92,

//    scrim =
)
private val AppDarkColorScheme = darkColorScheme(
    primary = p80,
    onPrimary = p20,
    primaryContainer = p30,
    onPrimaryContainer = p90,
    inversePrimary = p40,

    secondary = s80,
    onSecondary = s20,
    secondaryContainer = s30,
    onSecondaryContainer = s90,

    tertiary = t80,
    onTertiary = t20,
    tertiaryContainer = t30,
    onTertiaryContainer = t90,

    error = e80,
    onError = e20,
    errorContainer = e30,
    onErrorContainer = e90,

    background = n10,
    onBackground = n100,

    surface = n08,
    onSurface = n90,
    surfaceVariant = n13,
    onSurfaceVariant = n90,
    inverseSurface = n93,
    inverseOnSurface = n10,
    surfaceTint = n01,

    outline = n60,
    outlineVariant = n20,

    scrim = p20
)

@Composable
fun SomewhereTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    //dynamic colors
    //val useDynamicColors = Build.VERSION.SDK_INT >+ Build.VERSION_CODES.S

    val appColorScheme = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = appColorScheme,
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