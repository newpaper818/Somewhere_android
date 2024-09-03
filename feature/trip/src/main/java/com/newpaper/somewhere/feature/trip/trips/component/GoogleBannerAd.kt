package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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

@Composable
internal fun GoogleBannerAd(
    adView: AdView
){
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
}