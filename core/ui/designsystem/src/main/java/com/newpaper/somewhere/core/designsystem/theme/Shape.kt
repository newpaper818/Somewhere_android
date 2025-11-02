package com.newpaper.somewhere.core.designsystem.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape

//Shape.kt

val Shapes = Shapes(
    //none: banners, bottom app bars, tabs...
    extraSmall =    SmoothRoundedCornerShape(4.dp),  //textField, snackBar
    small =         SmoothRoundedCornerShape( 8.dp),  //inner card, chips, rich tooltip
    medium =        SmoothRoundedCornerShape(16.dp),  //card, small FAB
    large =         SmoothRoundedCornerShape(23.dp),  //FAB, extended FAB, dialog button
    extraLarge =    SmoothRoundedCornerShape(35.dp)   //dialog
    //full: button
)