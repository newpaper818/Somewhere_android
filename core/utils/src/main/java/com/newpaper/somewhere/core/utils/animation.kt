package com.newpaper.somewhere.core.utils

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically


val enterVertically = expandVertically(tween(350)) + fadeIn(tween(300, delayMillis = 200))
val exitVertically = shrinkVertically(tween(350, delayMillis = 150)) + fadeOut(tween(300))

val enterHorizontally = expandHorizontally(tween(350)) + fadeIn(tween(300, delayMillis = 200))
val exitHorizontally = shrinkHorizontally(tween(350, delayMillis = 150)) + fadeOut(tween(300))

val enterVerticallyScaleIn = scaleIn(animationSpec = tween(300, delayMillis = 150)) + expandVertically(tween(250)) + fadeIn(tween(350, delayMillis = 150))
val enterVerticallyScaleInDelay = scaleIn(animationSpec = tween(300, delayMillis = 350)) + expandVertically(tween(250, delayMillis = 250)) + fadeIn(tween(350, delayMillis = 400))
val exitVerticallyScaleOut = scaleOut(animationSpec = tween(350, delayMillis = 100)) + shrinkVertically(tween(250, delayMillis = 250)) + fadeOut(tween(300))
