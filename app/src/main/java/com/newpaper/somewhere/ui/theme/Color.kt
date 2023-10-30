package com.newpaper.somewhere.ui.theme

import androidx.annotation.ColorInt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.newpaper.somewhere.ui.theme.ColorType.*

//git testttttt
val gray = Color(0xFF878690)
val black = Color(0xff000000)
val white = Color(0xffffffff)


//primary Hue Chroma Tone 280 47 40
val p0 = Color(0xFF000000)
val p10 = Color(0xFF000e5f)
val p20 = Color(0xFF192778)
val p25 = Color(0xFF263384)
val p30 = Color(0xFF323f90)
val p35 = Color(0xFF3f4b9c)
val p40 = Color(0xFF4b57a9)
val p50 = Color(0xFF6471c4)
val p60 = Color(0xFF7e8ae0)
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
val lineMove = Color(0xFF7168e8)


enum class ColorType{
    GRAPH__POINT,
    GRAPH__LINE,
    GRAPH__HIGHLIGHT,

    PROGRESS_BAR__POINT_DEFAULT,
    PROGRESS_BAR__POINT_HIGHLIGHT,
    PROGRESS_BAR__LINE_DEFAULT,
    PROGRESS_BAR__LINE_DEFAULT_MOVE,
    PROGRESS_BAR__LINE_HIGHLIGHT,

    MAP_LINE_DEFAULT,
    SPOT_MAP_LINE_MOVE,



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
        GRAPH__LINE                     -> lineMove
        GRAPH__HIGHLIGHT                -> Color(0xFF4020b5)

        PROGRESS_BAR__POINT_DEFAULT     -> lineMove
        PROGRESS_BAR__POINT_HIGHLIGHT   -> point
        PROGRESS_BAR__LINE_DEFAULT      -> MaterialTheme.colorScheme.outline
        PROGRESS_BAR__LINE_DEFAULT_MOVE -> lineMove
        PROGRESS_BAR__LINE_HIGHLIGHT    -> point

        MAP_LINE_DEFAULT                -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        SPOT_MAP_LINE_MOVE              -> point


        BACKGROUND                      -> MaterialTheme.colorScheme.background
        CARD                            -> MaterialTheme.colorScheme.surfaceVariant

        DIALOG__BACKGROUND              -> MaterialTheme.colorScheme.background
        DIALOG__CARD                    -> MaterialTheme.colorScheme.surface

        BUTTON                          -> MaterialTheme.colorScheme.primary.copy(1f)
        BUTTON__NEGATIVE                -> gray
        BUTTON__POSITIVE                -> MaterialTheme.colorScheme.primaryContainer.copy(1f)
        BUTTON__DELETE                  -> MaterialTheme.colorScheme.error
        BUTTON__MAP                     -> MaterialTheme.colorScheme.surface.copy(0.85f)

        BUTTON__ON_NEGATIVE             -> white

        CARD_SELECTED                   -> MaterialTheme.colorScheme.primaryContainer
        CARD_ON_SELECTED                -> MaterialTheme.colorScheme.primary
        CARD_ON_DRAG                    -> MaterialTheme.colorScheme.secondaryContainer

        ERROR_BORDER                    -> e60
    }
}

const val blackInt = 0xff000000.toInt()
const val whiteInt = 0xffffffff.toInt()

//spot type group color
const val tourColor =       0xff493cfa.toInt()
const val moveColor =       0xff47de9f.toInt()
const val movePointColor =  0xff26b502.toInt()
const val foodColor =       0xFFedb25f.toInt()
const val lodgingColor =    0xff901aeb.toInt()
const val etcColor =        0xff8c8c8c.toInt()

const val onTourColor =     whiteInt
const val onMoveColor =     blackInt
const val onMovePointColor = blackInt
const val onFoodColor =     blackInt
const val onLodgingColor =  whiteInt
const val onEtcColor =      blackInt


data class MyColor(
    @ColorInt val color: Int = 0xFF493cfa.toInt(),
    @ColorInt val onColor: Int = whiteInt
)

val dateColorList = listOf(
    MyColor(0xFF493cfa.toInt()),
    MyColor(0xFF7168e8.toInt()),
    MyColor(0xFF000000.toInt()),
    MyColor(0xFFffffff.toInt(), blackInt),

    MyColor(0xFF7f7f7f.toInt(), blackInt),
    MyColor(0xFFc3c3c3.toInt(), blackInt),
    MyColor(0xFF880015.toInt()),
    MyColor(0xFFb97a57.toInt()),

    MyColor(0xFFed1c24.toInt()),
    MyColor(0xFFffaec9.toInt(), blackInt),
    MyColor(0xFFff7f27.toInt(), blackInt),
    MyColor(0xFFffc90e.toInt(), blackInt),

    MyColor(0xFFfff200.toInt(), blackInt),
    MyColor(0xFFefe4b0.toInt(), blackInt),
    MyColor(0xFF22b14c.toInt(), blackInt),
    MyColor(0xFFb5e61d.toInt(), blackInt),

    MyColor(0xFF00a2e8.toInt(), blackInt),
    MyColor(0xFF99d9ea.toInt(), blackInt),
    MyColor(0xFF3f48cc.toInt()),
    MyColor(0xFF7092be.toInt(), blackInt),

    MyColor(0xFFa349a4.toInt()),
    MyColor(0xFFc8bfe7.toInt(), blackInt),
    MyColor(0xFF732bf5.toInt()),
    MyColor(0xFF3a083e.toInt()),

    MyColor(0xFF75fa8d.toInt(), blackInt),
    MyColor(0xFF73fbfd.toInt(), blackInt),
    MyColor(0xFF3a0603.toInt()),
    MyColor(0xFF183e0c.toInt()),

    MyColor(0xFF817f26.toInt()),
    MyColor(0xFF0023f5.toInt()),
    MyColor(0xFF75163f.toInt()),
    MyColor(0xFF7f82bb.toInt(), blackInt),
)