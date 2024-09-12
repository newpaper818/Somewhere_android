package com.newpaper.somewhere.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView

@Composable
fun GoogleFullBannerAd(
    adView: AdView
){
    Box(
        modifier = Modifier
            .size(379.dp, 60.dp)
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

@Composable
fun GoogleMediumRectangleAd(
    adView: AdView
){
    Box(
        modifier = Modifier
            .size(300.dp, 250.dp)
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