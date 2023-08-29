package com.example.somewhere.ui.theme

import androidx.annotation.ColorInt
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.somewhere.ui.theme.ColorType.*


val point = Color(0xFF493cfa)
val line = Color(0xFF7168e8)

val black = Color(0xff000000)
val white = Color(0xffffffff)
val gray = Color(0xFF7f7f7f)

val errorColor = Color(0xFFff4040)


// Light Theme
val background = Color(0xFFececec)          //background (236)
val onBackground = black

val surface = Color(0xFFf2f2f2)             //card (242)
val onSurface = black

val primary = Color(0xFF4a4aff)             //selected card text
val primaryVariant = Color(0x204a4aff)      //selected card (80,80,255)
val onPrimary = white

val secondary = Color(0xFFfafafa)           //dialog card surface(250)
val secondaryVariant = Color(0xFFfafafa)    //?
val onSecondary = black

val error = errorColor
val onError = white

// Dark Theme
val background_ = Color(0xFF000000)         //background (0)
val onBackground_ = white

val surface_ = Color(0xFF181818)            //card (12)
val onSurface_ = white

val primary_ = Color(0xFF7070ff)            //selected card text
val primaryVariant_ = Color(0x307070ff)     //selected card (112,112,255)
val onPrimary_ = white

val secondary_ = Color(0xFF222222)          //dialog card surface(16)
val secondaryVariant_ = Color(0xFF222222)   //?
val onSecondary_ = white

val error_ = errorColor
val onError_ = white



enum class ColorType{
    GRAPH__POINT,
    GRAPH__LINE,
    GRAPH__HIGHLIGHT,

    PROGRESS_BAR__POINT_DEFAULT,
    PROGRESS_BAR__POINT_HIGHLIGHT,
    PROGRESS_BAR__LINE_DEFAULT,
    PROGRESS_BAR__LINE_HIGHLIGHT,



    BACKGROUND,
    CARD,

    DIALOG__BACKGROUND,
    DIALOG__CARD,

    BUTTON,
    BUTTON__NEGATIVE,
    BUTTON__POSITIVE,
    BUTTON__DELETE,
    BUTTON__MAP,

    BUTTON__ON_NEGATIVE,

    CARD_SELECTED,
    CARD_ON_SELECTED,

    ERROR
}


@Composable
fun getColor(colorType: ColorType): Color {
    return when(colorType){
        GRAPH__POINT                    -> point
        GRAPH__LINE                     -> line
        GRAPH__HIGHLIGHT                -> Color(0xFF4020b5)

        PROGRESS_BAR__POINT_DEFAULT     -> line
        PROGRESS_BAR__POINT_HIGHLIGHT   -> point
        PROGRESS_BAR__LINE_DEFAULT      -> line
        PROGRESS_BAR__LINE_HIGHLIGHT    -> point



        BACKGROUND                      -> MaterialTheme.colors.background
        CARD                            -> MaterialTheme.colors.surface

        DIALOG__BACKGROUND              -> MaterialTheme.colors.surface
        DIALOG__CARD                    -> MaterialTheme.colors.secondary

        BUTTON                          -> MaterialTheme.colors.primaryVariant.copy(1f)
        BUTTON__NEGATIVE                -> gray
        BUTTON__POSITIVE                -> MaterialTheme.colors.primaryVariant.copy(1f)
        BUTTON__DELETE                  -> MaterialTheme.colors.error
        BUTTON__MAP                     -> MaterialTheme.colors.surface.copy(0.7f)

        BUTTON__ON_NEGATIVE             -> white

        CARD_SELECTED                   -> MaterialTheme.colors.primaryVariant
        CARD_ON_SELECTED                -> MaterialTheme.colors.primary

        ERROR                           -> MaterialTheme.colors.error
    }
}

val dateColorList = listOf(
    0xFF493cfa.toInt(),
    0xFF153015.toInt(),
    0xFFff0103.toInt(),
    0xFF4068f0.toInt(),
    0xFF247923.toInt(),
    0xFF031560.toInt(),
    0xFF4dcfda.toInt(),
    0xFFf53015.toInt(),
    0xFF0f0103.toInt(),
    0xFF40f8f0.toInt(),
    0xFFf47923.toInt(),
    0xFF031560.toInt(),

    0xFF493cfa.toInt(),
    0xFF153015.toInt(),
    0xFFff0103.toInt(),
    0xFF4068f0.toInt(),
    0xFF247923.toInt(),
    0xFF031560.toInt(),
    0xFF4dcfda.toInt(),
    0xFFf53015.toInt(),
    0xFF0f0103.toInt(),
    0xFF40f8f0.toInt(),
    0xFFf47923.toInt(),
    0xFF031560.toInt(),

    0xFF493cfa.toInt(),
    0xFF153015.toInt(),
    0xFFff0103.toInt(),
    0xFF4068f0.toInt(),
    0xFF247923.toInt(),
    0xFF031560.toInt(),
    0xFF4dcfda.toInt(),
    0xFFf53015.toInt(),
    0xFF0f0103.toInt(),
    0xFF40f8f0.toInt(),
    0xFFf47923.toInt(),
    0xFF031560.toInt(),
)