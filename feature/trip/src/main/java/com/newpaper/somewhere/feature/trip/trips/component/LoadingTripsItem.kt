package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.shimmerEffect
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.trip.trips.tripCardHeightDp

@Composable
internal fun LoadingTripsItem(
    shown: Boolean,
    showAds: Boolean,
    use2Panes: Boolean,
    modifier: Modifier = Modifier
){
    var spacerValue = 16.dp + 34.dp + 16.dp
    if (showAds) spacerValue += (16.dp + 50.dp + 8.dp)
    if (showAds && use2Panes) spacerValue += 10.dp

    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(400))
    ) {

        Column(modifier = modifier.widthIn(max = itemMaxWidth)) {

            MySpacerColumn(height = spacerValue)

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