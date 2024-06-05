package com.newpaper.somewhere.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppLightColorScheme = lightColorScheme(
    //primary
    primary = p40,
    onPrimary = p100,
    primaryContainer = p90,
    onPrimaryContainer = p10,
    inversePrimary = p80,

    //secondary
    secondary = s40,
    onSecondary = s100,
    secondaryContainer = s90,
    onSecondaryContainer = s10,

    //tertiary
    tertiary = t40,
    onTertiary = t100,
    tertiaryContainer = t90,
    onTertiaryContainer = t10,

    //error
    error = Color(0xFFB3261E),
    onError = Color(0xFFffffff),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

//    //background
    background = Color(0xFFF5F5F5),
//    onBackground = n0,


    //surface
    surfaceDim = Color(0xFFEEEEEE),
    surface = Color(0xFFF5F5F5),
    surfaceBright = Color(0xFFFCFCFC),

//    surfaceContainerLowest = n90,
//    surfaceContainerLow = n80,
//    surfaceContainer = n70,
//    surfaceContainerHigh = n60,
//    surfaceContainerHighest = n50,

    onSurface = Color(0xFF000000),

    surfaceVariant = Color(0xFFC8C8C8),
    onSurfaceVariant = Color(0xFF868686),

    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFFFFFFF),
    surfaceTint = Color(0xFFE1E1E1),

    //outline
    outline = n50,
    outlineVariant = n92,

    //scrim
//    scrim = n0,
)
private val AppDarkColorScheme = darkColorScheme(
    //primary
    primary = p80,
    onPrimary = p20,
    primaryContainer = p30,
    onPrimaryContainer = p90,
    inversePrimary = p40,

    //secondary
    secondary = s80,
    onSecondary = s20,
    secondaryContainer = s30,
    onSecondaryContainer = s90,

    //tertiary
    tertiary = t80,
    onTertiary = t20,
    tertiaryContainer = t30,
    onTertiaryContainer = t90,

    //error
    error = Color(0xFFAC3731),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF601410),
    onErrorContainer = Color(0xFFF9DEDC),

//    //background
    background = Color(0xFF1A1A1A),
//    onBackground = n100,

    //surface
    surfaceDim = Color(0xFF131313),
    surface = Color(0xFF1A1A1A),
    surfaceBright = Color(0xFF212121),

//    surfaceContainerLowest = n10,
//    surfaceContainerLow = n20,
//    surfaceContainer = n30,
//    surfaceContainerHigh = n40,
//    surfaceContainerHighest = n50,

    onSurface = Color(0xFFFFFFFF),

    surfaceVariant = Color(0xFF898989),
    onSurfaceVariant = Color(0xFFA0A0A0),

    inverseSurface = Color(0xFFF6F6F6),
    inverseOnSurface = Color(0xFF000000),
    surfaceTint = Color(0xFF282828),

    //outline
    outline = n60,
    outlineVariant = n20,

    //scrim
//    scrim = n0,
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
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }
}