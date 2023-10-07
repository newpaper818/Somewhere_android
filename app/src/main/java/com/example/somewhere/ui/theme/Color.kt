package com.example.somewhere.ui.theme

import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.somewhere.ui.theme.ColorType.*

//hi
val gray = Color(0xFF878690)
val black = Color(0xff000000)
val white = Color(0xffffffff)

//
val md_theme_light_primary = Color(0xFF534FC0)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE2DFFF)
val md_theme_light_onPrimaryContainer = Color(0xFF0F0069)
val md_theme_light_inversePrimary = Color(0xFFC3C0FF)

val md_theme_light_secondary = Color(0xFF006687)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFC1E8FF)
val md_theme_light_onSecondaryContainer = Color(0xFF001E2B)

val md_theme_light_tertiary = Color(0xFF006C45)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFF81F9BB)
val md_theme_light_onTertiaryContainer = Color(0xFF002112)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_onBackground = black //Color(0xFF1C1B1F)

val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_onSurface = black //Color(0xFF1C1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE4E1EC)
val md_theme_light_onSurfaceVariant = black //Color(0xFF47464F)
val md_theme_light_inverseOnSurface = Color(0xFFF3EFF4)
val md_theme_light_inverseSurface = Color(0xFF313034)
val md_theme_light_surfaceTint = Color(0xFF534FC0)

val md_theme_light_outline = Color(0xFF787680)
val md_theme_light_outlineVariant = Color(0xFFC8C5D0)

val md_theme_light_shadow = Color(0xFF000000)

val md_theme_light_scrim = Color(0xFF000000)



val md_theme_dark_primary = Color(0xFFC3C0FF)
val md_theme_dark_onPrimary = Color(0xFF221790)
val md_theme_dark_primaryContainer = Color(0xFF3B35A7)
val md_theme_dark_onPrimaryContainer = Color(0xFFE2DFFF)
val md_theme_dark_inversePrimary = Color(0xFF534FC0)

val md_theme_dark_secondary = Color(0xFF72D2FF)
val md_theme_dark_onSecondary = Color(0xFF003548)
val md_theme_dark_secondaryContainer = Color(0xFF004D66)
val md_theme_dark_onSecondaryContainer = Color(0xFFC1E8FF)

val md_theme_dark_tertiary = Color(0xFF64DCA0)
val md_theme_dark_onTertiary = Color(0xFF003822)
val md_theme_dark_tertiaryContainer = Color(0xFF005233)
val md_theme_dark_onTertiaryContainer = Color(0xFF81F9BB)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_onBackground = white //Color(0xFFE5E1E6)

val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = white //Color(0xFFE5E1E6)
val md_theme_dark_surfaceVariant = Color(0xFF47464F)
val md_theme_dark_onSurfaceVariant = white //Color(0xFFC8C5D0)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE5E1E6)
val md_theme_dark_surfaceTint = Color(0xFFC3C0FF)

val md_theme_dark_outline = Color(0xFF928F9A)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_outlineVariant = Color(0xFF37363F)
val md_theme_dark_scrim = Color(0xFF000000)


val seed = Color(0xFF8E8BFF)



//primary Hue Chroma Tone 280 47 40
val p0 = Color(0xFF000000)
val p10 = Color(0xFF000e5f)
val p20 = Color(0xFF192778)
val p25 = Color(0xFF263384)
val p30 = Color(0xFF323f90)
val p35 = Color(0xFF3f4b9c)
val p40 = Color(0xFF4b57a9)
val p50 = Color(0xFF6471c4)
val p60 = Color(0xFF7e8ae0)  //primary?
val p70 = Color(0xFF99a5fd)
val p80 = Color(0xFFbbc3ff)
val p90 = Color(0xFFdfe0ff)
val p95 = Color(0xFFf0efff)
val p98 = Color(0xFFfbf8ff)
val p99 = Color(0xFFfffbff)
val p100 = Color(0xFFffffff)

//secondary 290 16 60
val s0 = Color(0xFF000000)
val s10 = Color(0xFF140463)
val s20 = Color(0xFF2b2277)
val s25 = Color(0xFF362f82)
val s30 = Color(0xFF423b8e)
val s35 = Color(0xFF4d479b)
val s40 = Color(0xFF5a54a8)
val s50 = Color(0xFF736dc3)
val s60 = Color(0xFF8c87df)
val s70 = Color(0xFFa7a1fc)
val s80 = Color(0xFFc5c0ff)
val s90 = Color(0xFFe3dfff)
val s95 = Color(0xFFf3eeff)
val s98 = Color(0xFFfcf8ff)
val s99 = Color(0xFFfffbff)
val s100 = Color(0xFFffffff)


//tertiary 230 24 60
val t0 = Color(0xFF000000)
val t10 = Color(0xFF001f2a)
val t20 = Color(0xFF003547)
val t25 = Color(0xFF004156)
val t30 = Color(0xFF004d65)
val t35 = Color(0xFF005975)
val t40 = Color(0xFF006685)
val t50 = Color(0xFF0081a7)
val t60 = Color(0xFF129cc8)
val t70 = Color(0xFF43b7e5)
val t80 = Color(0xFF6cd2ff)
val t90 = Color(0xFFbfe9ff)
val t95 = Color(0xFFe1f4ff)
val t98 = Color(0xFFf4faff)
val t99 = Color(0xFFfafcff)
val t100 = Color(0xFFffffff)

//error
val e0 = Color(0xFF000000)
val e10 = Color(0xFF410002)
val e20 = Color(0xFF690005)
val e25 = Color(0xFF7e0007)
val e30 = Color(0xFF93000a)
val e35 = Color(0xFFa80710)
val e40 = Color(0xFFba1a1a)
val e50 = Color(0xFFde3730)
val e60 = Color(0xFFff5449)
val e70 = Color(0xFFff897d)
val e80 = Color(0xFFffb4ab)
val e90 = Color(0xFFffdad6)
val e95 = Color(0xFFffedea)
val e98 = Color(0xFFfff8f7)
val e99 = Color(0xFFfffbff)
val e100 = Color(0xFFffffff)

//neutral hsl(0, 0%, x%)
val n0 = Color(0xFF000000)
val n01 = Color(0xFF030303)
val n03 = Color(0xFF080808)
val n05 = Color(0xFF0d0d0d)
val n07 = Color(0xFF121212)
val n08 = Color(0xFF141414)
val n10 = Color(0xFF1a1a1a)
val n13 = Color(0xFF212121)
val n15 = Color(0xFF262626)
val n20 = Color(0xFF333333)
val n30 = Color(0xFF4d4d4d)
val n40 = Color(0xFF666666)
val n50 = Color(0xFF808080)
val n60 = Color(0xFF999999)
val n70 = Color(0xFFb3b3b3)
val n80 = Color(0xFFcccccc)
val n90 = Color(0xFFe6e6e6)
val n92 = Color(0xFFebebeb)
val n93 = Color(0xFFededed)
val n95 = Color(0xFFf2f2f2)
val n97 = Color(0xFFf7f7f7)
val n98 = Color(0xFFfafafa)
val n99 = Color(0xFFfcfcfc)
val n100 = Color(0xFFffffff)

//neutral variant hsl(83, 10%, x%)
val nv0 = Color(0xFF000000)
val nv01 = Color(0xFF030302)
val nv05 = Color(0xFF0d0e0b)
val nv07 = Color(0xFF121410)
val nv10 = Color(0xFF1a1c17)
val nv20 = Color(0xFF34382e)
val nv30 = Color(0xFF4e5445)
val nv40 = Color(0xFF68705c)
val nv50 = Color(0xFF828c73)
val nv60 = Color(0xFF9ba38f)
val nv70 = Color(0xFFb4baab)
val nv80 = Color(0xFFcdd1c7)
val nv90 = Color(0xFFe6e8e3)
val nv93 = Color(0xFFeeefeb)
val nv95 = Color(0xFFf3f4f1)
val nv98 = Color(0xFFfafaf9)
val nv99 = Color(0xFFfdfdfc)
val nv100 = Color(0xFFffffff)


val point = Color(0xFF493cfa)
val line = Color(0xFF7168e8)

//val errorColor = Color(0xFFff4040)
//
//// Light Theme
//val background = Color(0xFFececec)          //background (236)
//val onBackground = black
//
//val surface = Color(0xFFf2f2f2)             //card (242)
//val onSurface = black
//
//val primary = Color(0xFF4a4aff)             //selected card text
//val primaryVariant = Color(0x204a4aff)      //selected card (80,80,255)
//val onPrimary = white
//
//val secondary = Color(0xFFfafafa)           //dialog card surface(250)
//val secondaryVariant = Color(0xFFd5d5d5)    //card on drag
//val onSecondary = black
//
//val error = errorColor
//val onError = white
//
//// Dark Theme
//val background_ = Color(0xFF000000)         //background (0)
//val onBackground_ = white
//
//val surface_ = Color(0xFF181818)            //card (12)
//val onSurface_ = white
//
//val primary_ = Color(0xFF7070ff)            //selected card text  7070ff
//val primaryVariant_ = Color(0x307070ff)     //selected card (112,112,255)
//val onPrimary_ = white
//
//val secondary_ = Color(0xFF222222)          //dialog card surface(16)
//val secondaryVariant_ = Color(0xFF303030)   //card on drag
//val onSecondary_ = white
//
//val error_ = errorColor
//val onError_ = white



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

    ERROR_BORDER
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



        BACKGROUND                      -> MaterialTheme.colorScheme.background
        CARD                            -> MaterialTheme.colorScheme.surfaceVariant

        DIALOG__BACKGROUND              -> MaterialTheme.colorScheme.background
        DIALOG__CARD                    -> MaterialTheme.colorScheme.surface

        BUTTON                          -> MaterialTheme.colorScheme.primary.copy(1f)
        BUTTON__NEGATIVE                -> gray
        BUTTON__POSITIVE                -> MaterialTheme.colorScheme.primaryContainer.copy(1f)
        BUTTON__DELETE                  -> MaterialTheme.colorScheme.error
        BUTTON__MAP                     -> MaterialTheme.colorScheme.surface.copy(0.7f)

        BUTTON__ON_NEGATIVE             -> white

        CARD_SELECTED                   -> MaterialTheme.colorScheme.primaryContainer
        CARD_ON_SELECTED                -> MaterialTheme.colorScheme.primary
        CARD_ON_DRAG                    -> MaterialTheme.colorScheme.secondaryContainer

        ERROR_BORDER                    -> e60
    }
}




const val tourColor =       0xff493cfa.toInt()
const val moveColor =       0xff53ed56.toInt()
const val movePointColor =  0xff26b502.toInt()
const val foodColor =       0xffe6a10e.toInt()
const val lodgingColor =    0xff901aeb.toInt()
const val etcColor =        0xff808080.toInt()


const val blackInt = 0xff000000.toInt()
const val whiteInt = 0xffffffff.toInt()

data class MyColor(
    @ColorInt val color: Int = 0xFF493cfa.toInt(),
    @ColorInt val onColor: Int = whiteInt
)

val myColorLists = listOf(
    //
    MyColor(0xFF493cfa.toInt()),
    MyColor(0xFF7168e8.toInt()),
    MyColor(0xFF808080.toInt()),
    MyColor(0xFFf5f0ea.toInt(), blackInt),

    //
    MyColor(0xFFff0000.toInt()),
    MyColor(0xFFd50bd9.toInt()),
    MyColor(0xFF29a632.toInt()),
    MyColor(0xFFf28b0c.toInt()),

    MyColor(0xFFd9a491.toInt()),
    MyColor(0xFFc37edb.toInt()),
    MyColor(0xFFb7a6f6.toInt()),
    MyColor(0xFF88a3e2.toInt()),

    MyColor(0xFFaaecfc.toInt(), blackInt),
    MyColor(0xFFe4d6a7.toInt(), blackInt),
    MyColor(0xFF9b2915.toInt()),
    MyColor(0xFFff6666.toInt()),

    MyColor(0xFFff8000.toInt()),
    MyColor(0xFFffb266.toInt(), blackInt),
    MyColor(0xFFffff00.toInt(), blackInt),
    MyColor(0xFF80ff00.toInt(), blackInt),

    MyColor(0xFF00ffff.toInt(), blackInt),
    MyColor(0xFF66b2ff.toInt()),
    MyColor(0xFF7f00ff.toInt()),
    MyColor(0xFFff66b2.toInt()),

    MyColor(0xFF71734c.toInt()),
    MyColor(0xFF40342a.toInt()),
    MyColor(0xFF146152.toInt()),
    MyColor(0xFFff5a33.toInt()),
)