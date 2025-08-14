package com.newpaper.somewhere.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newpaper.somewhere.core.model.data.MyColor

//Color.kt

//primary Hue Chroma Tone 266 100 xx
val p0 = Color(0xFFFFFFFF)  //
val p10 = Color(0xFF001A42) //
val p20 = Color(0xFF002E6A) //
val p25 = Color(0xFF00387F) //
val p30 = Color(0xFF004395) //
val p35 = Color(0xFF004FAC) //
val p40 = Color(0xFF005AC3) //
val p50 = Color(0xFF0072F3) //
val p60 = Color(0xFF4E8EFF) //
val p70 = Color(0xFF81AAFF) //
val p80 = Color(0xFFAEC6FF) //
val p90 = Color(0xFFD8E2FF) //
val p95 = Color(0xFFEDF0FF) //
val p98 = Color(0xFFF9F9FF) //
val p99 = Color(0xFFFEFBFF) //
val p100 = Color(0xFFffffff) //

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


val dateColorList = listOf(

    MyColor(0xFF493cfa.toInt()),
    MyColor(0xFF22b14c.toInt(), blackInt),
    MyColor(0xFFff7f27.toInt(), blackInt),
    MyColor(0xFF732bf5.toInt()),

    MyColor(0xFFffaec9.toInt(), blackInt),
    MyColor(0xFFed1c24.toInt()),
    MyColor(0xFF00a2e8.toInt(), blackInt),
    MyColor(0xFFa349a4.toInt()),

    MyColor(0xFF0023f5.toInt()),
    MyColor(0xFF73fbfd.toInt(), blackInt),
    MyColor(0xFF7168e8.toInt()),
    MyColor(0xFFb97a57.toInt()),

    MyColor(0xFF880015.toInt()),
    MyColor(0xFF99d9ea.toInt(), blackInt),
    MyColor(0xFFfff200.toInt(), blackInt),
    MyColor(0xFF3f48cc.toInt()),

    MyColor(0xFF75fa8d.toInt(), blackInt),
    MyColor(0xFF7f82bb.toInt(), blackInt),
    MyColor(0xFF3a083e.toInt()),
    MyColor(0xFF7092be.toInt(), blackInt),

    MyColor(0xFF3a0603.toInt()),
    MyColor(0xFFffc90e.toInt(), blackInt),
    MyColor(0xFF183e0c.toInt()),
    MyColor(0xFFefe4b0.toInt(), blackInt),

    MyColor(0xFFb5e61d.toInt(), blackInt),
    MyColor(0xFFc8bfe7.toInt(), blackInt),
    MyColor(0xFF817f26.toInt()),
    MyColor(0xFF75163f.toInt()),

    MyColor(0xFF000000.toInt()),
    MyColor(0xFF7f7f7f.toInt(), blackInt),
    MyColor(0xFFc3c3c3.toInt(), blackInt),
    MyColor(0xFFffffff.toInt(), blackInt),
)

@Preview
@Composable
private fun DateColorPreview(

){
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.width(140.dp)
    ) {
        itemsIndexed(dateColorList){ index, myColor ->
            Box(
                modifier = Modifier.size(35.dp),
                contentAlignment = Alignment.Center
            ){
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(myColor.color))
                        .size(30.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = (index+1).toString(),
                        color = Color(myColor.onColor),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
