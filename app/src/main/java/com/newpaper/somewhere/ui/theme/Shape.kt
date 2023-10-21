package com.newpaper.somewhere.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    //none: banners, bottom app bars, tabs...
    extraSmall =    RoundedCornerShape( 4.dp),  //textField, snackBar
    small =         RoundedCornerShape( 8.dp),  //inner card, chips, rich tooltip
    medium =        RoundedCornerShape(16.dp),  //card, small FAB
    large =         RoundedCornerShape(20.dp),  //FAB, extended FAB
    extraLarge =    RoundedCornerShape(28.dp)   //dialog
    //full: button
)