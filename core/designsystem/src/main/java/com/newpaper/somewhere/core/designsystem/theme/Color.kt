package com.newpaper.somewhere.core.designsystem.theme

import androidx.compose.ui.graphics.Color

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
val e10 = Color(0xFF3B120E)
val e20 = Color(0xFF581B14)
val e30 = Color(0xFF80271F)
val e40 = Color(0xFFA53328)
val e50 = Color(0xFFCB4538)
val e60 = Color(0xFFD57067)
val e70 = Color(0xFFDF9691)
val e80 = Color(0xFFDFBAB6)
val e90 = Color(0xFFF5DFDD)
val e95 = Color(0xFFFAEEEE)
val e99 = Color(0xFFFEFBF9)
val e100 = Color(0xFFffffff)
val errorContainerLight = Color(0xFFFFAFAF)

//neutral hsl(0, 0%, x%)
val n0 = Color(0xFF000000)
val n01 = Color(0xFF030303)
val n03 = Color(0xFF080808)
val n04 = Color(0xFF0a0a0a)
val n05 = Color(0xFF0d0d0d)
val n06 = Color(0xFF0f0f0f)
val n07 = Color(0xFF121212)
val n08 = Color(0xFF141414)
val n10 = Color(0xFF1a1a1a)
val n12 = Color(0xFF1d1d1d)
val n13 = Color(0xFF212121)
val n15 = Color(0xFF262626) // 38
val n17 = Color(0xFF2B2B2B) // 43
val n20 = Color(0xFF333333) // 51
val n22 = Color(0xFF383838) // 56
val n24 = Color(0xFF3D3D3D) // 61
val n30 = Color(0xFF4d4d4d) // 77
val n40 = Color(0xFF666666)
val n50 = Color(0xFF808080)
val n60 = Color(0xFF999999)
val n70 = Color(0xFFb3b3b3)
val n80 = Color(0xFFcccccc) // 204
val n87 = Color(0xFFDEDEDE) // 222
val n90 = Color(0xFFe6e6e6) // 230
val n92 = Color(0xFFebebeb)
val n93 = Color(0xFFededed) // 237
val n94 = Color(0xFFF0F0F0) // 240
val n95 = Color(0xFFf2f2f2) // 242
val n96 = Color(0xFFf6f6f6) // 246
val n97 = Color(0xFFf7f7f7) // 247
val n98 = Color(0xFFfafafa) // 250
val n99 = Color(0xFFfcfcfc) // 252
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






object CustomColor {
    //val gray = Color(0xFF878690)
    val black = Color(0xff000000)
    val white = Color(0xffffffff)

    val outlineError = Color(0xffE71717)

    val spotMapLineMove = Color(0xFF493cfa).copy(alpha = 0.6f)

    val imageBackground = Color(0xFF0d0d0d)
    val imageForeground = Color(0xFF0d0d0d).copy(alpha = 0.7f)
}

object GraphColor {
    val point = Color(0xFF493cfa)
    val line = Color(0xFF7168e8)

}

object ProgressBarColor {
    val pointDefault = Color(0xFF7168e8)
    val pointHighlight = Color(0xFF493cfa)
    val lineDefaultMove = Color(0xFF7168e8)
    val lineHighlight = Color(0xFF493cfa)
}




const val blackInt = 0xff000000.toInt()
const val whiteInt = 0xffffffff.toInt()

