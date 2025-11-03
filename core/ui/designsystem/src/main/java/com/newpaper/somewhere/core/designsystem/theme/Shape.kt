package com.newpaper.somewhere.core.designsystem.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape

//Shape.kt



val Shapes = Shapes(
    //none: banners, bottom app bars, tabs...
    extraSmall =    SmoothRoundedCornerShape(6.dp),   //textField
    small =         SmoothRoundedCornerShape(14.dp),  //inner card, chips, plainToolTip, snackBar
    medium =        SmoothRoundedCornerShape(22.dp),  //card, small FAB
    large =         SmoothRoundedCornerShape(23.dp),  //FAB, extended FAB, dialog button
    extraLarge =    SmoothRoundedCornerShape(35.dp)   //dialog
    //full: button 999.dp
)