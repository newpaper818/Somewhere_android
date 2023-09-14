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
val secondaryVariant = Color(0xFFd5d5d5)    //card on drag
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
val secondaryVariant_ = Color(0xFF303030)   //card on drag
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
    CARD_ON_DRAG,

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
        CARD_ON_DRAG                    -> MaterialTheme.colors.secondaryVariant

        ERROR                           -> MaterialTheme.colors.error
    }
}


const val blackInt = 0xff000000.toInt()
const val whiteInt = 0xffffffff.toInt()

data class MyColor(
    @ColorInt val color: Int = 0xFF493cfa.toInt(),
    @ColorInt val onColor: Int = whiteInt
)

val myColorLists = listOf(
    MyColor(0xFF493cfa.toInt()),              MyColor(0xFF7168e8.toInt()),
    MyColor(0xff5090d0.toInt()),

    MyColor(0xFFff0000.toInt()),              MyColor(0xFFff6666.toInt()),
    MyColor(0xFFff8000.toInt()),              MyColor(0xFFffb266.toInt(), blackInt),

    MyColor(0xFFffff00.toInt(), blackInt),    MyColor(0xFFffff66.toInt(), blackInt),
    MyColor(0xFF80ff00.toInt(), blackInt),    MyColor(0xFFb2ff66.toInt(), blackInt),

    MyColor(0xFF00ff00.toInt(), blackInt),    MyColor(0xFF66ff66.toInt(), blackInt),
    MyColor(0xFF00ff80.toInt(), blackInt),    MyColor(0xFF66ffb2.toInt(), blackInt),

    MyColor(0xFF00ffff.toInt(), blackInt),    MyColor(0xFF66ffff.toInt(), blackInt),
    MyColor(0xFF0080ff.toInt()),              MyColor(0xFF66b2ff.toInt()),

    MyColor(0xFF0000ff.toInt()),              MyColor(0xFF6666ff.toInt()),
    MyColor(0xFF7f00ff.toInt()),              MyColor(0xFFb266ff.toInt()),

    MyColor(0xFFff00ff.toInt()),              MyColor(0xFFff66ff.toInt()),
    MyColor(0xFFff007f.toInt()),              MyColor(0xFFff66b2.toInt()),

    MyColor(0xFF808080.toInt()),              MyColor(0xFFc0c0c0.toInt(), blackInt),
)