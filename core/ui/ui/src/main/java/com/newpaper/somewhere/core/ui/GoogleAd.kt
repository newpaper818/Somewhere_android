package com.newpaper.somewhere.core.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.lightspark.composeqr.BuildConfig
import com.newpaper.somewhere.core.utils.REWARDED_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.REWARDED_AD_UNIT_ID_TEST

private const val GOOGLE_AD_TAG = "Google-Ad"

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

//rewarded interstitial ad
fun loadRewardedAd(
    context: Context,
    onAdLoaded: (RewardedInterstitialAd?) -> Unit
){
    val adRequest = AdRequest.Builder().build()

    RewardedInterstitialAd.load(
        context,
        if (BuildConfig.DEBUG) REWARDED_AD_UNIT_ID_TEST else REWARDED_AD_UNIT_ID,
        adRequest,
        object: RewardedInterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(GOOGLE_AD_TAG, "Ad error - $adError")
                onAdLoaded(null)
            }

            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                Log.d(GOOGLE_AD_TAG, "Ad was loaded")
                onAdLoaded(ad)
            }
        }
    )
}

fun loadAndShowRewardedAd(
    ad: RewardedInterstitialAd?,
    context: Context,
    activity: Activity,
    onUserEarnedReward: () -> Unit
) {
    if (ad == null) {
        loadRewardedAd(
            context = context,
            onAdLoaded = { loadedAd ->
                if (loadedAd != null){
                    showRewardedAd(loadedAd, activity, onUserEarnedReward)
                }
                else {
                    onUserEarnedReward()
                }
            }
        )
    } else {
        showRewardedAd(ad, activity, onUserEarnedReward)
    }
}

private fun showRewardedAd(
    ad: RewardedInterstitialAd,
    activity: Activity,
    onUserEarnedReward: () -> Unit
) {
    ad.show(
        activity,
        OnUserEarnedRewardListener { rewardItem ->
            // Handle the reward.
            Log.d(GOOGLE_AD_TAG, "User earned the reward.")
            onUserEarnedReward()
        }
    )
}