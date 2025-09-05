package com.newpaper.somewhere.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

//Shape.kt

val Shapes = Shapes(
    //none: banners, bottom app bars, tabs...
    extraSmall =    RoundedCornerShape( 4.dp),  //textField, snackBar
    small =         RoundedCornerShape( 8.dp),  //inner card, chips, rich tooltip
    medium =        RoundedCornerShape(16.dp),  //card, small FAB
    large =         RoundedCornerShape(23.dp),  //FAB, extended FAB, dialog button
    extraLarge =    RoundedCornerShape(35.dp)   //dialog
    //full: button
)