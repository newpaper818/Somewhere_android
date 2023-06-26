package com.example.somewhere.ui.screens.trip

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.DeleteOrNotDialog
import com.example.somewhere.ui.screenUtils.GraphListItem
import com.example.somewhere.ui.screenUtils.ImageCard
import com.example.somewhere.ui.screenUtils.InformationCard
import com.example.somewhere.ui.screenUtils.MemoCard
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screenUtils.TitleCard
import com.example.somewhere.ui.screenUtils.TripDurationCard
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import com.example.somewhere.ui.screens.somewhere.SomewhereFloatingButton
import com.example.somewhere.ui.screens.somewhere.SomewhereTopAppBar
import com.maxkeppeker.sheets.core.CoreDialog
import com.maxkeppeker.sheets.core.models.CoreSelection
import com.maxkeppeker.sheets.core.models.base.ButtonStyle
import com.maxkeppeker.sheets.core.models.base.SelectionButton
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import kotlinx.coroutines.launch

object TripDestination : NavigationDestination {
    override val route = "trip"
    override var title = "Trip screen"
    const val tripIdArg = "tripId"
    val routeWithArgs = "$route/{$tripIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    isNewTrip: Boolean,
    isEditMode: Boolean,
    onDateClicked: (Date) -> Unit,
    changeEditMode: (Boolean?) -> Unit,
    changeIsNewTrip: (Boolean?) -> Unit,

    navigateUp: () -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteTrip: (Trip) -> Unit,

    modifier: Modifier = Modifier,
    tripViewModel: TripViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
){
    val tripUiState = tripViewModel.tripUiState

    val showingTrip =
        if (isEditMode) tripUiState.tempTrip
        else tripUiState.trip

    val context = LocalContext.current
    val dialogState = rememberUseCaseState(visible = false)

    CoreDialog(
        state = dialogState,
        selection = CoreSelection(
            withButtonView = true,
            negativeButton = SelectionButton(
                text = stringResource(id = R.string.dialog_cancel),
                type = ButtonStyle.OUTLINED
            ),
            onNegativeClick = {},
            positiveButton = SelectionButton(
                text = stringResource(id = R.string.dialog_exit),
                type = ButtonStyle.FILLED
            ),
            onPositiveClick = {
                changeEditMode(false)

                tripViewModel.updateTripUiState(true, tripUiState.trip)

                if (isNewTrip){
                    navigateUpAndDeleteTrip(tripUiState.trip)
                }
            }
        ),
        onPositiveValid = true,
        body = {
            Text(text = stringResource(id = R.string.dialog_body))
        },
    )

    if (isEditMode)
        BackHandler {
            if (tripViewModel.tripUiState.trip != tripViewModel.tripUiState.tempTrip)
                dialogState.show()
            else {
                changeEditMode(false)

                if (isNewTrip){
                    navigateUpAndDeleteTrip(tripUiState.trip)
                }
            }
        }


    TripDestination.title =
        if (isEditMode) stringResource(id = R.string.top_bar_title_edit_trip)
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.top_bar_title_no_title)
            else
                showingTrip.titleText
        }

    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                isEditMode = isEditMode,
                title = TripDestination.title,

                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    if (!isEditMode) navigateUp()
                    else {
                        if (tripViewModel.tripUiState.trip != tripViewModel.tripUiState.tempTrip)
                            dialogState.show()
                        else{
                            changeEditMode(false)

                            if (isNewTrip){
                                navigateUpAndDeleteTrip(tripUiState.trip)
                            }
                        }
                    }
                },

                actionIcon1 =
                    if (!isEditMode) MyIcons.edit
                    else null,
                actionIcon1Onclick = {
                    changeEditMode(true)
                },

                actionIcon2 = null,
                actionIcon2Onclick = {}
            )
        },

        //bottom floating button
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            //trip map floating button
            AnimatedVisibility(
                visible =
                        //uiState.currentTrip != null &&
                        //uiState.currentTrip?.getFirstLocation() != null &&
                        !isEditMode,
                enter = slideInVertically(animationSpec = tween(500), initialOffsetY = {(it*1.5).toInt()}),
                exit = slideOutVertically(animationSpec = tween(500), targetOffsetY = {(it*1.5).toInt()})
            ) {
                SomewhereFloatingButton(
                    text = stringResource(id = R.string.see_on_map),
                    icon = MyIcons.map,
                    onButtonClicked = navigateToTripMap
                )
            }
        }
    ) { i ->
        val a = i


        Column {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 200.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colors.background)
            ) {

                item {
                    val toastText = stringResource(id = R.string.image_card_image_limit_toast)

                    ImageCard(
                        isEditMode = isEditMode,
                        imgList = showingTrip.imagePathList,
                        onAddImages = {

                            var addImageList: List<String> = it

                            if (showingTrip.imagePathList.size + it.size > 10){
                                addImageList = it.subList(0, 10 - showingTrip.imagePathList.size)
                                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                            }

                            tripViewModel.updateTripUiState(
                                true,
                                showingTrip.copy(imagePathList = showingTrip.imagePathList + addImageList)
                            )
                        },
                        deleteImage = {
                            val newList: MutableList<String> = showingTrip.imagePathList.toMutableList()
                            newList.remove(it)

                            tripViewModel.updateTripUiState(
                                true,
                                showingTrip.copy(imagePathList = newList.toList())
                            )
                        }
                    )
                }

                item {
                    TitleCard(
                        isEditMode = isEditMode,
                        titleText = showingTrip.titleText,
                        onTitleChange = { titleText ->
                            val newTitleText: String? =
                                if (titleText == "") null
                                else titleText

                            tripViewModel.updateTripUiState(
                                true,
                                showingTrip.copy(titleText = newTitleText)
                            )
                        }
                    )
                }

                //trip duration card
                item {
                    TripDurationCard(
                        dateList = showingTrip.dateList,
                        isDateListEmpty = showingTrip.dateList.isEmpty(),
                        startDateText = showingTrip.getStartDateText(true),
                        endDateText = showingTrip.getEndDateText(true),
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
                            val newMemoText: String? =
                                if (memoText == "") null
                                else memoText

                            tripViewModel.updateTripUiState(
                                true,
                                showingTrip.copy(memoText = newMemoText)
                            )
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
                            expandedText = it.getExpandedText(showingTrip, isEditMode),

                            onTitleTextChange = {id, titleText ->
                                tripViewModel.updateDateTitle(true, id, titleText)
                            },

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
                    item {
                        //empty dates
                        val text = if (isEditMode) stringResource(id = R.string.dates_set_trip_duration)
                                    else stringResource(id = R.string.dates_no_plan)

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

            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                BottomSaveCancelBar(
                    onCancelClick = {
                        if (tripViewModel.tripUiState.trip != tripViewModel.tripUiState.tempTrip)
                            dialogState.show()
                        else{
                            changeEditMode(false)

                            if (isNewTrip){
                                navigateUpAndDeleteTrip(tripUiState.trip)
                            }
                        }
                    },
                    onSaveClick = {
                        coroutineScope.launch {
                            tripViewModel.saveTrip()
                        }
                        changeEditMode(false)
                        changeIsNewTrip(false)
                    }
                )
            }
        }
    }
}