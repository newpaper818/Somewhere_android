package com.newpaper.somewhere.feature.trip.tripAi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.TripType
import com.newpaper.somewhere.core.model.enums.TripWith
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripAi.component.PrevNextButtons
import com.newpaper.somewhere.feature.trip.tripAi.page.CreateTripPage
import com.newpaper.somewhere.feature.trip.tripAi.page.EnterTripCityPage
import com.newpaper.somewhere.feature.trip.tripAi.page.EnterTripDurationPage
import com.newpaper.somewhere.feature.trip.tripAi.page.SelectTripTypePage
import com.newpaper.somewhere.feature.trip.tripAi.page.SelectTripWithPage
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun TripAiRoute(
    appUserId: String,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    commonTripViewModel: CommonTripViewModel,
    navigateUp: () -> Unit,
    navigateToAiCreatedTrip: (Trip) -> Unit,
    tripAiViewModel: TripAiViewModel = hiltViewModel()
){
    val tripAiUiState by tripAiViewModel.tripAiUiState.collectAsState()

    if (tripAiUiState.showExitDialog){
        DeleteOrNotDialog(
            bodyText = stringResource(id = R.string.are_you_sure_to_exit),
            deleteButtonText = stringResource(id = R.string.exit),
            onDismissRequest = {
                tripAiViewModel.setShowExitDialog(false)
            },
            onClickDelete = {
                tripAiViewModel.setShowExitDialog(false)
                navigateUp()
            }
        )
    }

    //create trip with ai when in CREATE_TRIP phase
    LaunchedEffect(tripAiUiState.phase) {
        //when internet fail while creating trip TODO

        if (tripAiUiState.phase == Phase.CREATE_TRIP) {
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

                    val aiCreatedTrip = tripAiViewModel.addIdTimeInAiCreatedRawTrip(
                        aiCreatedRawTrip = aiCreatedRawTrip,
                        tripManagerId = appUserId,
                        newOrderId = newOrderId
                    )

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(
                        trip = aiCreatedTrip
                    )

                    //navigate to trip
                    navigateToAiCreatedTrip(aiCreatedTrip)
                }
            }
        }
    }


    TripAiScreen(
        internetEnabled = internetEnabled,
        dateTimeFormat = dateTimeFormat,
        tripAiUiState = tripAiUiState,

        setPhase = tripAiViewModel::setPhase,
        tripToTextChanged = tripAiViewModel::setTripTo,
        setStartDate = tripAiViewModel::setStartDate,
        setEndDate = tripAiViewModel::setEndDate,
        setTripWith = tripAiViewModel::setTripWith,
        onClickTripType = tripAiViewModel::onClickTripType,

        setShowExitDialog = tripAiViewModel::setShowExitDialog
    )
}

@Composable
fun TripAiScreen(
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    tripAiUiState: TripAiUiState,

    setPhase: (Phase) -> Unit,
    tripToTextChanged: (String) -> Unit,
    setStartDate: (LocalDate?) -> Unit,
    setEndDate: (LocalDate?) -> Unit,
    setTripWith: (TripWith) -> Unit,
    onClickTripType: (TripType) -> Unit,

    setShowExitDialog: (Boolean) -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val phaseList = enumValues<Phase>()
    val pagerState = rememberPagerState(
        pageCount = { phaseList.size }
    )

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                tripAiUiState.phase.ordinal
            )
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        setPhase(phaseList[pagerState.currentPage])
    }

    Scaffold (
        modifier = Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding(),
        topBar = {
            SomewhereTopAppBar(
                title = "",
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = {
                    //are you sure? dialog
                    setShowExitDialog(true)

                    //navigate up
                }
            )
        }
    ) { paddingValues ->


        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val phase = phaseList[pageIndex]
                    when (phase) {
                        Phase.TRIP_TO -> {

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

                        Phase.TRIP_DATE -> {
                            EnterTripDurationPage(
                                dateTimeFormat = dateTimeFormat,
                                initialStartDate = tripAiUiState.startDate,
                                initialEndDate = tripAiUiState.endDate,
                                setStartDate = setStartDate,
                                setEndDate = setEndDate
                            )
                        }

                        Phase.TRIP_WITH -> {
                            SelectTripWithPage(
                                selectedTripWith = tripAiUiState.tripWith,
                                setTripWith = setTripWith
                            )
                        }

                        Phase.TRIP_TYPE -> {
                            SelectTripTypePage(
                                internetEnabled = internetEnabled,
                                selectedTripTypes = tripAiUiState.tripTypes,
                                onClick = onClickTripType
                            )
                        }

                        Phase.CREATE_TRIP -> {
                            CreateTripPage()
                        }
                    }
                }
            }

            if (tripAiUiState.phase != Phase.CREATE_TRIP) {
                PrevNextButtons(
                    prevButtonVisible = pagerState.currentPage != 0,
                    onClickPrev = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage - 1
                            )
                        }
                    },
                    nextIsCreateTrip = tripAiUiState.phase == Phase.TRIP_TYPE,
                    nextButtonEnabled =
                        when (tripAiUiState.phase) {
                            Phase.TRIP_TO ->
                                tripAiUiState.tripTo != null && tripAiUiState.tripTo != ""

                            Phase.TRIP_DATE ->
                                tripAiUiState.startDate != null
                                        && tripAiUiState.endDate != null
                                        && tripAiUiState.startDate.plusDays(15) > tripAiUiState.endDate

                            Phase.TRIP_WITH ->
                                tripAiUiState.tripWith != null

                            Phase.TRIP_TYPE ->
                                tripAiUiState.tripTypes.isNotEmpty() && internetEnabled

                            Phase.CREATE_TRIP -> true
                        },
                    onClickNext = {
                        focusManager.clearFocus()
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )
                        }
                    }
                )
            }
        }







//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize(),
//            contentAlignment = Alignment.BottomCenter
//        ){
//
//
//
//
//            LazyColumn(
//                state = scrollState,
//                horizontalAlignment = Alignment.CenterHorizontally,
////                contentPadding = PaddingValues(
////                    spacerValue, 16.dp, spacerValue, 200.dp
////                ),
//                modifier = Modifier.fillMaxSize()
//            ) {
//                item {
//                    EnterTripCityPage(
//                        searchText = "",
//                        onTextChanged = {},
//                        onClearClicked = { },
//                        onKeyboardActionClicked = { }
//                    )
//
////                    EnterTripDurationPage(dateTimeFormat = dateTimeFormat)
//
////                    SelectTripWithPage()
//
////                    SelectTripTypePage()
//                }
//            }
//
//            PrevNextButtons(
//                showPrevButton = true,
//                nextIsCreateTrip = false,
//                onClickPrev = { /*TODO*/ },
//                onClickNext = { /*TODO*/ }
//            )
//        }
    }
}