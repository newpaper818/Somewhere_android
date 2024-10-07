package com.newpaper.somewhere.feature.trip.tripAi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import kotlinx.coroutines.launch

@Composable
fun TripAiRoute(
    appUserId: String,
    commonTripViewModel: CommonTripViewModel,
    tripAiViewModel: TripAiViewModel = hiltViewModel()
){
    val tripAiUiState by tripAiViewModel.tripAiUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    TripAiScreen(
        aiText = tripAiUiState.trip.toString(),
        onClickButton = {
            tripAiViewModel.viewModelScope.launch {
                //get ai created raw trip
                val aiCreatedRawTrip = tripAiViewModel.getAiCreatedRawTrip()

                if (aiCreatedRawTrip != null) {

                    //add id time in ai created raw trip
                    var newOrderId = 0
                    val lastTrip = commonTripViewModel.commonTripUiState.value.tripInfo.trips?.lastOrNull()
                    if (lastTrip != null) { newOrderId = lastTrip.orderId + 1 }

                    val aiCreatedTrip = tripAiViewModel.addIdTimeInAiCreatedRawTrip(
                        aiCreatedRawTrip = aiCreatedRawTrip,
                        tripManagerId = appUserId,
                        newOrderId = newOrderId
                    )

                    //save trip
//                    val beforeTempTripDateListLastIndex =
//                        commonTripViewModel.saveTrip(
//                            appUserId = appUserId,
//                            deleteNotEnabledDate = true
//                        )

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(
                        trip = aiCreatedTrip
                    )
                }
            }
        }
    )
}

@Composable
fun TripAiScreen(
    aiText: String,
    onClickButton: () -> Unit,
){
    val scrollState = rememberLazyListState()

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding(),
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
//                contentPadding = PaddingValues(
//                    spacerValue, 16.dp, spacerValue, 200.dp
//                ),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Button(
                        onClick = onClickButton
                    ) {
                        Text(text = "create trip")
                    }
                }

                item {
                    Text(text = aiText)
                }
            }
        }
    }
}