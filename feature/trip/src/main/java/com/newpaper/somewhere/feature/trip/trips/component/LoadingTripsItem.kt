package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.shimmerEffect
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.trips.tripCardHeightDp

@Composable
internal fun LoadingTripsItem(
    shown: Boolean,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {

        Column(modifier = modifier) {

            MySpacerColumn(height = 124.dp) //

            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(tripCardHeightDp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                MySpacerColumn(height = 16.dp)
            }

        }
    }
}