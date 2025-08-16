package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.n50

@Composable
fun DotsIndicator(
    isDarkAppTheme: Boolean,
    pageCount:Int,
    currentPage: Int,

    otherDotColor: Color = n50,
    barColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
){
    if (pageCount <= 1)
        return

    val currentDotColor = if (isDarkAppTheme) Color.White
                            else Color.Black

    Row(
        modifier = Modifier
            .height(20.dp)
            .clip(CircleShape)
            .background(barColor),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) {

            //current dot
            if (currentPage == it){
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(currentDotColor)
                        .size(10.dp)
                )
            }

            //other dots
            else{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                ){
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(otherDotColor)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}











@Composable
@PreviewLightDark
private fun DotsIndicatorPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(100.dp)
        ) {
            Column {
                DotsIndicator(true, 5, 1)
                DotsIndicator(false, 5, 1)
            }
        }
    }
}