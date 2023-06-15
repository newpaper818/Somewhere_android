package com.example.somewhere.ui.screens.trip

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.GraphListItem
import com.example.somewhere.ui.screenUtils.InformationCard
import com.example.somewhere.ui.screenUtils.MemoCard
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screenUtils.TitleCard
import com.example.somewhere.ui.screenUtils.TripDurationCard
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import kotlinx.coroutines.launch

object TripDestination : NavigationDestination {
    override val route = "trip"
    override var title = "Trip screen"
    const val tripIdArg = "tripId"
    val routeWithArgs = "$route/{$tripIdArg}"
}

@Composable
fun TripScreen(
    isEditMode: Boolean,
    onDateClicked: (Date) -> Unit,
    changeEditMode: () -> Unit,
    updateTopAppBarTitle: (String) -> Unit,

    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
){
    val tripUiState = tripViewModel.tripUiState

    val showingTrip = if (isEditMode) tripUiState.tempTrip
                        else tripUiState.trip


    val onCancelButtonClicked = {
        TripDestination.title = showingTrip.titleText ?: "No title"
        changeEditMode()
    }

    if(isEditMode)
        BackHandler {
            onCancelButtonClicked()
        }


    updateTopAppBarTitle(
        if (isEditMode) "Edit trip"
        else showingTrip.titleText ?: "No title"
    )

    val coroutineScope = rememberCoroutineScope()

    TripDestination.title = if (isEditMode) "Edit Trip"
                            else showingTrip.titleText ?: "No title"



    Column {
        LazyColumn(
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 200.dp),
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colors.background)
        ) {
            item {
                TitleCard(
                    isEditMode = isEditMode,
                    titleText = showingTrip.titleText,
                    onTitleChange = { titleText ->
                        tripViewModel.updateTripUiState(true, showingTrip.copy(titleText = titleText))
                    }
                )
            }

            item {
                TripDurationCard(
                    currentTrip = showingTrip,
                    isDateListEmpty = showingTrip.dateList.isEmpty(),
                    startDateText = showingTrip.getStartDateText(),
                    endDateText = showingTrip.getEndDateText(),
                    durationText = showingTrip.getDurationText(),

                    isEditMode = isEditMode,
                    setTripDuration = { startDate, endDate ->
                        tripViewModel.updateTripDurationAndTripUiState(true, startDate, endDate)
                    }
                )
            }

            //information card
            item {
                InformationCard(
                    list = listOf(
                        Pair(MyIcons.budget, showingTrip.getTotalBudgetText()),
                        Pair(MyIcons.travelDistance, showingTrip.getTotalTravelDistanceText())
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            //memo card
            item {
                MemoCard(
                    isEditMode = isEditMode,
                    memoText = showingTrip.memoText,
                    onMemoChanged = { memoText ->
                        tripViewModel.updateTripUiState(true, showingTrip.copy(memoText = memoText))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            //FIXME this
            //currentTrip.allDateCollapse()

            //dates
            item {
                StartEndDummySpaceWithRoundedCorner(isFirst = true, isLast = false)
            }

            item {
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = expandVertically(tween(400)),
                    exit = shrinkVertically(tween(400))
                ) {
                    Card(
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp, 0.dp)
                        ) {
                            Text(
                                text = "Dates",
                                style = MaterialTheme.typography.h6,
                                modifier = if (showingTrip.dateList.isNotEmpty()) Modifier.padding(
                                    0.dp, 0.dp, 0.dp, 0.dp
                                )
                                else Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                            )
                        }
                    }
                }
            }

            if (showingTrip.dateList.isNotEmpty()) {
                items(showingTrip.dateList) {

                    var isExpanded by rememberSaveable { mutableStateOf(false) }

                    GraphListItem(
                        isEditMode = isEditMode,
                        isExpanded = isExpanded,
                        itemId = it.id,

                        sideText = it.getDateText(false),
                        mainText = it.titleText,
                        expandedText = it.getExpandedText(showingTrip),

                        isFirstItem = it == showingTrip.dateList.first(),
                        isLastItem = it == showingTrip.dateList.last(),

                        onItemClick = { dateId ->
                            for (date in showingTrip.dateList) {
                                if (date.id == dateId) {
                                    onDateClicked(date)
                                    break
                                }
                            }
                        },
                        onExpandedButtonClicked = { dateId -> //TODO remove dateId?
                            isExpanded = !isExpanded
                        }
                    )
                }
            } else {
                //empty dates
                val text = if (isEditMode) "Set Trip Duration!"
                else "No Plan..."

                item {
                    Card(
                        backgroundColor = MaterialTheme.colors.surface,
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 0.dp,
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp, 0.dp)
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                }
            }

            item {
                StartEndDummySpaceWithRoundedCorner(isFirst = false, isLast = true)
            }
        }

        //TODO animation
        if(isEditMode)
            BottomSaveCancelBar(
                onCancelClick = onCancelButtonClicked,
                onSaveClick = {
                    coroutineScope.launch {
                        tripViewModel.saveTrip()
                    }
                    TripDestination.title = "Edit Trip"
                    changeEditMode()

                }
            )
    }
}