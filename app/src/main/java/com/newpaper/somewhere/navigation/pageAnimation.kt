package com.newpaper.somewhere.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally




//page animation
internal val enterTransition = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it })
internal val exitTransition =
    fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
internal val popEnterTransition =
    fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
internal val popExitTransition = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })

//
internal val TopEnterTransition = fadeIn(animationSpec = tween(300))
internal val TopExitTransition = fadeOut(animationSpec = tween(300))
internal val TopPopEnterTransition = fadeIn(animationSpec = tween(300))
internal val TopPopExitTransition = fadeOut(animationSpec = tween(300))




//page animation for image
internal val imageEnterTransition = fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
internal val imageExitTransition =
    fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)
internal val imagePopEnterTransition =
    fadeIn(tween(300)) + scaleIn(animationSpec = tween(300), initialScale = 0.7f)
internal val imagePopExitTransition = fadeOut(tween(300)) + scaleOut(animationSpec = tween(300), targetScale = 0.7f)

