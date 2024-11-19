package com.newpaper.somewhere.core.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.newpaper.somewhere.core.designsystem.component.button.RemoveAdsButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.ui.ui.BuildConfig
import com.newpaper.somewhere.core.utils.REWARDED_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.REWARDED_AD_UNIT_ID_TEST

private const val GOOGLE_AD_TAG = "Google-Ad"

@Composable
fun GoogleBannerAd(
    adView: AdView,
    useFullBanner: Boolean
){
    val modifier =
        if (!useFullBanner) Modifier
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
    adView: AdView,
    showRemoveAdsButton: Boolean = true,
    onClickRemoveAds: () -> Unit = { }
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        if (showRemoveAdsButton) {
            MySpacerColumn(height = 4.dp)

            RemoveAdsButton(
                onClick = onClickRemoveAds
            )
        }
    }
}



//rewarded interstitial ad
fun loadRewardedAd(
    context: Context,
    onAdLoaded: (RewardedInterstitialAd?) -> Unit
){
    val adRequest = AdRequest.Builder().build()
    val adUnitId = if (BuildConfig.DEBUG) REWARDED_AD_UNIT_ID_TEST else REWARDED_AD_UNIT_ID

    RewardedInterstitialAd.load(
        context,
        adUnitId,
        adRequest,
        object: RewardedInterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(GOOGLE_AD_TAG, "load rewarded ad error - $adError")
                onAdLoaded(null)
            }

            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                Log.d(GOOGLE_AD_TAG, "rewarded ad was loaded")
                onAdLoaded(ad)
            }
        }
    )
}

fun showRewardedAd(
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

fun loadAndShowRewardedAd(
    context: Context,
    ad: RewardedInterstitialAd?,
    activity: Activity,
    onAdLoaded: () -> Unit,
    onUserEarnedReward: () -> Unit
){
    if (ad != null) {
        //show ad
        onAdLoaded()
        showRewardedAd(
            ad = ad,
            activity = activity,
            onUserEarnedReward = onUserEarnedReward
        )
    }
    else {
        //load ad
        loadRewardedAd(
            context = context,
            onAdLoaded = { loadedAd ->
                onAdLoaded()
                if (loadedAd != null){
                    //show ad
                    showRewardedAd(
                        ad = loadedAd,
                        activity = activity,
                        onUserEarnedReward = onUserEarnedReward
                    )
                }
                else {
                    onUserEarnedReward()
                }
            }
        )
    }
}