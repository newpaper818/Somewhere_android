package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.button.TryAgainButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.createTripIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.GoogleMediumRectangleAd
import com.newpaper.somewhere.feature.trip.R
import kotlinx.coroutines.delay

@Composable
internal fun CreateTripPage(
    internetEnabled: Boolean,
    adView: AdView?,
    createTripError: Boolean,
    onClickTryAgain: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!createTripError) {
                CreatingTrip()

                if (adView != null) {
                    MySpacerColumn(height = 64.dp)

                    GoogleMediumRectangleAd(
                        adView = adView,
                        showRemoveAdsButton = false
                    )
                }
            }
            else {
                Error(
                    internetEnabled = internetEnabled,
                    onClickTryAgain = onClickTryAgain
                )
            }

            MySpacerColumn(height = 40.dp)
        }
    }
}

@Composable
private fun CreatingTrip(

){
    val iconPagerState = rememberPagerState(
        pageCount = { createTripIcons.size }
    )

    LaunchedEffect(Unit) {
        delay(500)
        while(true) {
            val to = (iconPagerState.currentPage + 1) % createTripIcons.size

            if (to == 0) {
                delay(500)
                iconPagerState.scrollToPage(
                    page = to
                )
            }
            else {
                if (to == 1)    delay(500)
                else            delay(1000)

                iconPagerState.animateScrollToPage(
                    page = to,
                    animationSpec = spring()
                )
            }
        }
    }

    HorizontalPager(
        state = iconPagerState,
        userScrollEnabled = false,
        modifier = Modifier.size(40.dp)
    ) {
        DisplayIcon(icon = createTripIcons[it])
    }

    MySpacerColumn(height = 16.dp)
    
    Text(
        text = stringResource(id = R.string.creating_trip_with_ai),
        style = MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Normal
        )
    )

    MySpacerColumn(height = 12.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) { }
    ) {
        Text(
            text = stringResource(id = R.string.it_may_took_a_while),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MySpacerColumn(height = 4.dp)

        Text(
            text = stringResource(id = R.string.it_may_contain_incorrect_information),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Error(
    internetEnabled: Boolean,
    onClickTryAgain: () -> Unit
){
    Text(
        text = stringResource(id = R.string.something_went_wrong),
        style = MaterialTheme.typography.bodyLarge
    )

    MySpacerColumn(height = 16.dp)

    TryAgainButton(
        enabled = internetEnabled,
        onClick = onClickTryAgain
    )
}










@Composable
@PreviewLightDark
private fun CreateTripPagePreview() {
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            CreateTripPage(
                internetEnabled = true,
                adView = null,
                createTripError = false,
                onClickTryAgain = {}
            )
        }
    }
}