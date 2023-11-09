package com.newpaper.somewhere.ui.screenUtils.commonScreenUtils

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val SPACER_SMALL = 16.dp
val SPACER_BIG = 24.dp

@Composable
fun MySpacerRow(
    width: Dp
) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun MySpacerColumn(
    height: Dp
) {
    Spacer(modifier = Modifier.height(height))
}