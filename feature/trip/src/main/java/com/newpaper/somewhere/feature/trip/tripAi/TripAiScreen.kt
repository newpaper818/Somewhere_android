package com.newpaper.somewhere.feature.trip.tripAi

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.loadAndShowRewardedAd
import com.newpaper.somewhere.core.ui.loadRewardedAd
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID_TEST
import com.newpaper.somewhere.feature.dialog.deleteOrNot.TwoButtonsDialog
import com.newpaper.somewhere.feature.dialog.tripAiDialog.CautionDialog
import com.newpaper.somewhere.feature.dialog.tripAiDialog.CautionFreePlanDialog
import com.newpaper.somewhere.feature.trip.BuildConfig
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripAi.component.PrevNextButtons
import com.newpaper.somewhere.feature.trip.tripAi.model.TripType
import com.newpaper.somewhere.feature.trip.tripAi.model.TripWith
import com.newpaper.somewhere.feature.trip.tripAi.page.CreateTripPage
import com.newpaper.somewhere.feature.trip.tripAi.page.EnterTripCityPage
import com.newpaper.somewhere.feature.trip.tripAi.page.EnterTripDurationPage
import com.newpaper.somewhere.feature.trip.tripAi.page.SelectTripTypePage
import com.newpaper.somewhere.feature.trip.tripAi.page.SelectTripWithPage
import kotlinx.coroutines.launch
import java.time.LocalDate

private var rewardedInterstitialAd: RewardedInterstitialAd? = null

@Composable
fun TripAiRoute(
    appUserData: UserData,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    commonTripViewModel: CommonTripViewModel,
    navigateUp: () -> Unit,
    navigateToAiCreatedTrip: (Trip) -> Unit,
    tripAiViewModel: TripAiViewModel = hiltViewModel()
){
    val context = LocalContext.current

    val tripAiUiState by tripAiViewModel.tripAiUiState.collectAsState()

    val onClickBack = {
        //if user enter nothing, navigate back
        if (tripAiUiState.tripAiPhase == TripAiPhase.TRIP_TO
            && tripAiUiState.tripTo == null
            || tripAiUiState.tripTo == "")
            navigateUp()
        //else, ask to exit
        else
            tripAiViewModel.setShowExitDialog(true)
    }

    BackHandler {
        onClickBack()
    }

    rewardedInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
            Log.d("Google-Ad", "Ad was clicked.")
        }

        override fun onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Log.d("Google-Ad", "Ad dismissed fullscreen content.")
            rewardedInterstitialAd = null
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            //Called when ad fails to show.
            Log.e("Google-Ad", "Ad failed to show fullscreen content.")
            rewardedInterstitialAd = null
        }

        override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d("Google-Ad", "Ad recorded an impression.")
        }

        override fun onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d("Google-Ad", "Ad showed fullscreen content.")
        }
    }

    val adView =
        if (!appUserData.isUsingSomewherePro) {
            AdView(context).apply {
                setAdSize(AdSize.MEDIUM_RECTANGLE)
                adUnitId = if (BuildConfig.DEBUG) BANNER_AD_UNIT_ID_TEST
                            else BANNER_AD_UNIT_ID

                loadAd(AdRequest.Builder().build())
            }
        }
        else null

    if (tripAiUiState.showExitDialog) {
        TwoButtonsDialog(
            bodyText = stringResource(id = R.string.are_you_sure_to_exit),
            positiveButtonText = stringResource(id = R.string.exit),
            onDismissRequest = {
                tripAiViewModel.setShowExitDialog(false)
            },
            onClickPositive = {
                tripAiViewModel.setShowExitDialog(false)
                navigateUp()
            }
        )
    }

    //load ad
    LaunchedEffect(tripAiUiState.tripAiPhase) {
        if (
            !appUserData.isUsingSomewherePro
            && tripAiUiState.tripAiPhase == TripAiPhase.TRIP_TYPE
            && rewardedInterstitialAd == null
        ){
            loadRewardedAd(
                context = context,
                onAdLoaded = { ad ->
                    rewardedInterstitialAd = ad
                }
            )
        }
    }


    //create trip with ai when in CREATE_TRIP phase
    LaunchedEffect(
        tripAiUiState.tripAiPhase,
        tripAiUiState.createTripError,
        tripAiUiState.userGetReward
    ) {
        //when internet fail while creating trip TODO
        if (
            tripAiUiState.tripAiPhase == TripAiPhase.CREATE_TRIP
            && !tripAiUiState.createTripError
            && !tripAiUiState.creatingTrip
            && tripAiUiState.userGetReward
        ) {
            tripAiViewModel.setCreatingTrip(true)
            tripAiViewModel.viewModelScope.launch {
                //get ai created raw trip
                val aiCreatedRawTrip = tripAiViewModel.getAiCreatedRawTrip()

                if (aiCreatedRawTrip != null) {

                    //add id time in ai created raw trip
                    var newOrderId = 0
                    val lastTrip =
                        commonTripViewModel.commonTripUiState.value.tripInfo.trips?.lastOrNull()
                    if (lastTrip != null) {
                        newOrderId = lastTrip.orderId + 1
                    }

                    val aiCreatedTrip = tripAiViewModel.addAdditionalDataToAiCreatedRawTrip(
                        aiCreatedRawTrip = aiCreatedRawTrip,
                        tripManagerId = appUserData.userId,
                        newOrderId = newOrderId
                    )

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(
                        trip = aiCreatedTrip
                    )

                    //navigate to trip
                    navigateToAiCreatedTrip(aiCreatedTrip)
                }
                else {
                    tripAiViewModel.setCreateTripError(true)
                }
                tripAiViewModel.setCreatingTrip(false)
            }
        }
    }


    TripAiScreen(
        isUsingSomewherePro = appUserData.isUsingSomewherePro,
        internetEnabled = internetEnabled,
        dateTimeFormat = dateTimeFormat,
        tripAiUiState = tripAiUiState,
        adView = adView,

        onClickBack = onClickBack,

        setPhase = tripAiViewModel::setPhase,

        tripToTextChanged = tripAiViewModel::setTripTo,
        setStartDate = tripAiViewModel::setStartDate,
        setEndDate = tripAiViewModel::setEndDate,
        setTripWith = tripAiViewModel::setTripWith,
        onClickTripType = tripAiViewModel::onClickTripType,

        setCreateTripError = tripAiViewModel::setCreateTripError,
        setUserGetReward = tripAiViewModel::setUserGetReward,

        setShowCautionDialog = tripAiViewModel::setShowCautionDialog,
    )
}

@Composable
fun TripAiScreen(
    isUsingSomewherePro: Boolean,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    tripAiUiState: TripAiUiState,
    adView: AdView?,

    onClickBack: () -> Unit,

    setPhase: (TripAiPhase) -> Unit,

    tripToTextChanged: (String) -> Unit,
    setStartDate: (LocalDate?) -> Unit,
    setEndDate: (LocalDate?) -> Unit,
    setTripWith: (TripWith) -> Unit,
    onClickTripType: (TripType) -> Unit,

    setCreateTripError: (Boolean) -> Unit,
    setUserGetReward: (Boolean) -> Unit,

    setShowCautionDialog: (Boolean) -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = context as Activity

    val tripAiPhaseList = enumValues<TripAiPhase>()
    val pagerState = rememberPagerState(
        initialPage = tripAiUiState.tripAiPhase.ordinal,
        pageCount = { tripAiPhaseList.size }
    )

    val focusManager = LocalFocusManager.current

    var showDateErrorText by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(pagerState.currentPage) {
        setPhase(tripAiPhaseList[pagerState.currentPage])
    }

    if (tripAiUiState.showCautionDialog){
        if (isUsingSomewherePro){
            CautionDialog(
                onDismissRequest = {
                    setShowCautionDialog(false)
                },
                onClickPositive = {
                    setShowCautionDialog(false)
                    setUserGetReward(true)

                    //to next page
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                }
            )
        }
        else {
            CautionFreePlanDialog(
                onDismissRequest = {
                    setShowCautionDialog(false)
                },
                onClickPositive = {
                    loadAndShowRewardedAd(
                        context = context,
                        ad = rewardedInterstitialAd,
                        activity = activity,
                        onAdLoaded = {
                            setShowCautionDialog(false)
                        },
                        onUserEarnedReward = {
                            setShowCautionDialog(false)
                            setUserGetReward(true)

                            //to next page
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            }
                        }
                    )
                }
            )
        }
    }

    Scaffold (
        modifier = Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding()
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val phase = tripAiPhaseList[pageIndex]
                    when (phase) {
                        TripAiPhase.TRIP_TO -> {
                            EnterTripCityPage(
                                searchText = tripAiUiState.tripTo ?: "",
                                onTextChanged = tripToTextChanged,
                                onClearClicked = { tripToTextChanged("") },
                                onKeyboardActionClicked = {
                                    focusManager.clearFocus()
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            pagerState.currentPage + 1
                                        )
                                    }
                                }
                            )
                        }

                        TripAiPhase.TRIP_DATE -> {
                            EnterTripDurationPage(
                                dateTimeFormat = dateTimeFormat,
                                initialStartDate = tripAiUiState.startDate,
                                initialEndDate = tripAiUiState.endDate,
                                setStartDate = setStartDate,
                                setEndDate = setEndDate,
                                onErrorTextVisible = { showDateErrorText = it }
                            )
                        }

                        TripAiPhase.TRIP_WITH -> {
                            SelectTripWithPage(
                                selectedTripWith = tripAiUiState.tripWith,
                                setTripWith = setTripWith
                            )
                        }

                        TripAiPhase.TRIP_TYPE -> {
                            SelectTripTypePage(
                                selectedTripTypes = tripAiUiState.tripTypes,
                                onClick = onClickTripType
                            )
                        }

                        TripAiPhase.CREATE_TRIP -> {
                            CreateTripPage(
                                internetEnabled = internetEnabled,
                                adView = adView,
                                createTripError = tripAiUiState.createTripError,
                                onClickTryAgain = { setCreateTripError(false) }
                            )
                        }
                    }
                }
            }

            if (tripAiUiState.tripAiPhase != TripAiPhase.CREATE_TRIP) {
                    PrevNextButtons(
                        errorVisible =
                            when(tripAiUiState.tripAiPhase) {
                                TripAiPhase.TRIP_DATE -> showDateErrorText
                                TripAiPhase.TRIP_TYPE -> !internetEnabled
                                else -> false
                            },
                        errorText =
                            when(tripAiUiState.tripAiPhase) {
                                TripAiPhase.TRIP_DATE -> stringResource(id = R.string.date_range_error)
                                TripAiPhase.TRIP_TYPE -> stringResource(id = R.string.internet_unavailable)
                                else -> ""
                            },

                        prevButtonVisible = pagerState.currentPage != 0,
                        onClickPrev = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1
                                )
                            }
                        },
                        nextIsCreateTrip = tripAiUiState.tripAiPhase == TripAiPhase.TRIP_TYPE,
                        nextButtonEnabled =
                        when (tripAiUiState.tripAiPhase) {
                            TripAiPhase.TRIP_TO ->
                                tripAiUiState.tripTo != null && tripAiUiState.tripTo != ""

                            TripAiPhase.TRIP_DATE ->
                                tripAiUiState.startDate != null
                                        && tripAiUiState.endDate != null
                                        && tripAiUiState.startDate.plusDays(15) > tripAiUiState.endDate

                            TripAiPhase.TRIP_WITH ->
                                tripAiUiState.tripWith != null

                            TripAiPhase.TRIP_TYPE ->
                                tripAiUiState.tripTypes.isNotEmpty() && internetEnabled

                            TripAiPhase.CREATE_TRIP -> false
                        },
                        onClickNext = {
                            if (tripAiUiState.tripAiPhase == TripAiPhase.TRIP_TYPE) {
                                setShowCautionDialog(true)
                            } else {
                                focusManager.clearFocus()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1
                                    )
                                }
                            }
                        }
                    )
                }

            CloseButton(
                onClick = onClickBack,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.8f))
                .clickable { onClick() }
        ) {
            DisplayIcon(icon = TopAppBarIcon.close)
        }
    }
}