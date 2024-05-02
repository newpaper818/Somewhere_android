package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn

@Composable
internal fun GoogleBannerAd(
    internetEnabled: Boolean,
    adView: AdView
){
    AnimatedVisibility(
        visible = internetEnabled,
        enter = scaleIn(animationSpec = tween(300))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300))
                + fadeOut(animationSpec = tween(300))
    ) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(320.dp)
                    .height(50.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceDim)
            ) {
                //banner ad
                AndroidView(
                    modifier = Modifier,
                    factory = { adView },
                )
            }
            MySpacerColumn(height = 8.dp)
        }
    }

}