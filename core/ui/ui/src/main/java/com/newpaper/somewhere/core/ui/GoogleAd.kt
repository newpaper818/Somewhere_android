package com.newpaper.somewhere.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView

@Composable
fun GoogleBannerAd(
    adView: AdView,
    useFullBanner: Boolean
){
    val modifier =
        if (!useFullBanner)Modifier
            .size(320.dp, 50.dp) //BANNER
            .background(MaterialTheme.colorScheme.surfaceDim)
        else Modifier
            .size(468.dp, 60.dp) //FULL_BANNER
            .background(MaterialTheme.colorScheme.surfaceDim)

    Box(
        modifier = modifier
    ) {
        //banner ad
        AndroidView(
            modifier = Modifier.clearAndSetSemantics { },
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
            .background(MaterialTheme.colorScheme.surfaceDim)
    ) {
        //banner ad
        AndroidView(
            modifier = Modifier.clearAndSetSemantics { },
            factory = { adView },
        )
    }
}