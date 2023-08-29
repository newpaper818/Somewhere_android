package com.example.somewhere.ui.screens.trip

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.DeleteOrNotDialog
import com.example.somewhere.ui.screenUtils.GraphListItem
import com.example.somewhere.ui.screenUtils.cards.ImageCard
import com.example.somewhere.ui.screenUtils.cards.InformationCard
import com.example.somewhere.ui.screenUtils.cards.MemoCard
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.SomewhereFloatingButton
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screenUtils.cards.TitleCard
import com.example.somewhere.ui.screenUtils.cards.TripDurationCard
import com.example.somewhere.ui.screens.somewhere.SomewhereTopAppBar
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

object TripDestination : NavigationDestination {
    override val route = "trip"
    override var title = "Trip screen"
    const val tripIdArg = "tripId"
    val routeWithArgs = "$route/{$tripIdArg}"
}

@Composable
fun TripScreen(
    isEditMode: Boolean,

    originalTrip: Trip,
    tempTrip: Trip,
    isNewTrip: Boolean,

    changeEditMode: (editMode: Boolean?) -> Unit,

    navigateUp: () -> Unit,
    navigateToDate: (dateId: Int) -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteTrip: (deleteTrip: Trip) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,
    updateDateTitle: (toTempTrip: Boolean, dateId: Int, dateTitleText: String) -> Unit,
    saveTrip: () -> Unit,

    modifier: Modifier = Modifier,
){
    val showingTrip =
        if (isEditMode) tempTrip
        else originalTrip

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    val scrollState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBackButtonClick = {
        if (originalTrip != tempTrip)
            showExitDialog = true
        else {
            changeEditMode(false)

            if (isNewTrip){
                navigateUpAndDeleteTrip(originalTrip)
            }
        }
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }

    //set top bar title
    TripDestination.title =
        if (isEditMode) stringResource(id = R.string.top_bar_title_edit_trip)
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText
        }


    Scaffold (
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                isEditMode = isEditMode,
                title = TripDestination.title,

                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    if (!isEditMode) navigateUp()
                    else             onBackButtonClick()

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
            if(showingTrip.getFirstLocation() != null) {

                //trip map floating button
                AnimatedVisibility(
                    visible =
                    //FIXME
                    //uiState.currentTrip != null &&
                    //uiState.currentTrip?.getFirstLocation() != null &&
                    !isEditMode,
                    enter = slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { (it * 1.5).toInt() }),
                    exit = slideOutVertically(
                        animationSpec = tween(500),
                        targetOffsetY = { (it * 1.5).toInt() })
                ) {
                    SomewhereFloatingButton(
                        text = stringResource(id = R.string.see_on_map),
                        icon = MyIcons.map,
                        onButtonClicked = navigateToTripMap
                    )
                }
            }
        }
    ) {i ->
        val a = i

        if(showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { showExitDialog = false },
                onDeleteClick = {
                    showExitDialog = false
                    changeEditMode(false)
                    updateTripState(true, originalTrip)

                    if (isNewTrip){
                        navigateUpAndDeleteTrip(originalTrip)
                    }
                }
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 300.dp),
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {

                //title card
                item {
                    TitleCard(
                        isEditMode = isEditMode,
                        titleText = showingTrip.titleText,
                        focusManager = focusManager,
                        onTitleChange = { titleText ->
                            val newTitleText: String? =
                                if (titleText == "") null
                                else titleText

                            updateTripState(
                                true,
                                showingTrip.copy(titleText = newTitleText)
                            )
                        }
                    )
                }

                //image card
                item {
                    val toastText = stringResource(id = R.string.toast_image_limit)

                    ImageCard(
                        isEditMode = isEditMode,
                        imgList = showingTrip.imagePathList,
                        onAddImages = {
                            var addImageList: List<String> = it

                            //up to 10 images
                            if (showingTrip.imagePathList.size + it.size > 10){
                                addImageList = it.subList(0, 10 - showingTrip.imagePathList.size)
                                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                            }

                            updateTripState(
                                true,
                                showingTrip.copy(imagePathList = showingTrip.imagePathList + addImageList)
                            )
                        },
                        deleteImage = {
                            val newList: MutableList<String> = showingTrip.imagePathList.toMutableList()
                            newList.remove(it)

                            updateTripState(
                                true,
                                showingTrip.copy(imagePathList = newList.toList())
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
                            updateTripDurationAndTripState(true, startDate, endDate)
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

                            updateTripState(
                                true,
                                showingTrip.copy(memoText = newMemoText)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                //dates card
                item {
                    StartEndDummySpaceWithRoundedCorner(isFirst = true, isLast = false)
                }

                //dates text
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
                                    style = getTextStyle(TextType.CARD__TITLE),
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

                            onTitleTextChange = { dateId, dateTitleText ->
                                updateDateTitle(true, dateId, dateTitleText)
                            },

                            isFirstItem = it == showingTrip.dateList.first(),
                            isLastItem = it == showingTrip.dateList.last(),
                            availableDelete = false,

                            onItemClick = { dateId ->
                                navigateToDate(dateId)
                            },
                            onExpandedButtonClicked = { dateId -> //TODO remove dateId?
                                isExpanded = !isExpanded
                            }
                        )
                    }
                }
                //no dates
                else {
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
                                    style = getTextStyle(TextType.CARD__BODY_NULL)
                                )
                            }
                        }
                    }
                }

                item {
                    StartEndDummySpaceWithRoundedCorner(isFirst = false, isLast = true)
                }
            }

            //bottom save cancel bar
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                BottomSaveCancelBar(
                    onCancelClick = {
                        focusManager.clearFocus()
                        onBackButtonClick()
                    },
                    onSaveClick = {
                        coroutineScope.launch {
                            saveTrip()
                        }
                    }
                )
            }
        }
    }
}