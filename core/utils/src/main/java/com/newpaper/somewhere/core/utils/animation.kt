package com.newpaper.somewhere.core.utils

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically


val enterVertically = expandVertically(tween(350)) + fadeIn(tween(300, delayMillis = 200))
val exitVertically = shrinkVertically(tween(350, delayMillis = 150)) + fadeOut(tween(300))

val enterHorizontally = expandHorizontally(tween(350)) + fadeIn(tween(300, delayMillis = 200))
val exitHorizontally = shrinkHorizontally(tween(350, delayMillis = 150)) + fadeOut(tween(300))