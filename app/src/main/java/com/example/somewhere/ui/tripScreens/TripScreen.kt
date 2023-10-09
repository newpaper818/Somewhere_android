package com.example.somewhere.ui.tripScreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.example.somewhere.ui.tripScreenUtils.GraphListItem
import com.example.somewhere.ui.tripScreenUtils.cards.ImageCard
import com.example.somewhere.ui.tripScreenUtils.cards.InformationCard
import com.example.somewhere.ui.tripScreenUtils.cards.MemoCard
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.tripScreenUtils.dialogs.SetCurrencyTypeDialog
import com.example.somewhere.ui.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.tripScreenUtils.cards.TripDurationCard
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.AnimatedBottomSaveCancelBar
import com.example.somewhere.ui.tripScreenUtils.EditAndMapFAB
import com.example.somewhere.ui.tripScreenUtils.cards.TitleCard
import com.example.somewhere.viewModel.DateTimeFormat
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

    dateTimeFormat: DateTimeFormat,

    changeEditMode: (editMode: Boolean?) -> Unit,

    navigateUp: () -> Unit,
    navigateToDate: (dateId: Int) -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteTrip: (deleteTrip: Trip) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,
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

    val snackBarHostState = remember { SnackbarHostState() }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSetCurrencyDialog by rememberSaveable { mutableStateOf(false) }
    var showBottomSaveCancelBar by rememberSaveable { mutableStateOf(true) }

    val locale = LocalConfiguration.current.locales[0]

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

    val snackBarPadding by animateFloatAsState(
        targetValue = if (isEditMode) 10f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    //for expanded fab animation
    val isFABExpanded by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }


    Scaffold (
        snackbarHost = { SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .width(500.dp)
                .padding(0.dp, 0.dp, 0.dp, snackBarPadding.dp)
                .imePadding()
        ) },

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                title = TripDestination.title,

                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    if (!isEditMode) navigateUp()
                    else             onBackButtonClick()
                }
            )
        },

        //bottom floating button
        floatingActionButton = {
            EditAndMapFAB(
                visible = !isEditMode,
                onEditClick = { changeEditMode(null) },
                showMapFAB = showingTrip.getFirstLocation() != null,
                onMapClick = navigateToTripMap,
                isMapFABExpanded = isFABExpanded
            )
        }
    ) { paddingValues ->

        //dialogs
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

        if (showSetCurrencyDialog){
            SetCurrencyTypeDialog(
                initialCurrencyType = showingTrip.unitOfCurrencyType,
                onOkClick = { newCurrencyType ->
                    showingTrip.setCurrencyType(updateTripState, newCurrencyType)
                    showSetCurrencyDialog = false
                    showBottomSaveCancelBar = true
                },
                onDismissRequest = {
                    showSetCurrencyDialog = false
                    showBottomSaveCancelBar = true
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 300.dp),
                modifier = modifier.fillMaxSize()
            ) {

                //title card
                item {
                    TitleCard(
                        isEditMode = isEditMode,
                        titleText = showingTrip.titleText,
                        focusManager = focusManager,
                        onTitleChange = { newTitleText ->
                            showingTrip.setTitleText(updateTripState, newTitleText)
                        },
                        onTextSizeLimit = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "over 100",
                                    actionLabel = null,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }

                //image card
                item {
                    val snackBarText = stringResource(id = R.string.snack_bar_image_limit)

                    ImageCard(
                        isEditMode = isEditMode,
                        imgList = showingTrip.imagePathList,
                        onAddImages = {
                            var addImageList: List<String> = it

                            //up to 10 images
                            if (showingTrip.imagePathList.size + it.size > 10){
                                addImageList = it.subList(0, 10 - showingTrip.imagePathList.size)

                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = snackBarText,
                                        actionLabel = null,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }

                            showingTrip.setImage(updateTripState, showingTrip.imagePathList + addImageList)
                        },
                        deleteImage = {
                            val newList: MutableList<String> = showingTrip.imagePathList.toMutableList()
                            newList.remove(it)

                            showingTrip.setImage(updateTripState, newList.toList())
                        },
                        showSnackBar = { text, actionLabel, duration ->
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = text,
                                    actionLabel = actionLabel,
                                    duration = duration
                                )
                            }
                        }
                    )
                }

                //trip duration card
                item {
                    TripDurationCard(
                        dateList = showingTrip.dateList,
                        isDateListEmpty = showingTrip.dateList.isEmpty(),
                        startDateText = showingTrip.getStartDateText(locale, dateTimeFormat, true),
                        endDateText = showingTrip.getEndDateText(locale,dateTimeFormat, true),
                        durationText = showingTrip.getDurationText(),

                        dateTimeFormat = dateTimeFormat,

                        isEditMode = isEditMode,
                        setShowBottomSaveCancelBar = {
                            showBottomSaveCancelBar = it
                        },
                        setTripDuration = { startDate, endDate ->
                            updateTripDurationAndTripState(true, startDate, endDate)
                        }
                    )
                }

                //information card
                item {
                    InformationCard(
                        isEditMode = isEditMode,
                        list = listOf(
                            Triple(MyIcons.budget, showingTrip.getTotalBudgetText()) {
                                showSetCurrencyDialog = true
                                showBottomSaveCancelBar = false
                            },
                            Triple(MyIcons.travelDistance, showingTrip.getTotalTravelDistanceText(), null)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                //memo card
                item {
                    MemoCard(
                        isEditMode = isEditMode,
                        memoText = showingTrip.memoText,
                        onMemoChanged = { newMemoText ->
                            showingTrip.setMemoText(updateTripState, newMemoText)
                        },
                        onTextSizeLimit = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "Memo is too long",
                                    actionLabel = null,
                                    duration = SnackbarDuration.Short
                                )
                            }
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ){
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
                            pointColor = Color(it.color.color),
                            isEditMode = isEditMode,
                            isExpanded = isExpanded,
                            itemId = it.id,

                            sideText = it.getDateText(locale, dateTimeFormat.copy(includeDayOfWeek = false), false),
                            mainText = it.titleText,
                            expandedText = it.getExpandedText(showingTrip, isEditMode),

                            onTitleTextChange = { dateId, dateTitleText ->
                                showingTrip.dateList[dateId].setTitleText(showingTrip, updateTripState, dateTitleText)
                            },

                            isFirstItem = it == showingTrip.dateList.first(),
                            isLastItem = it == showingTrip.dateList.last(),
                            availableDelete = false,

                            onItemClick = { dateId ->
                                navigateToDate(dateId)
                            },
                            onExpandedButtonClicked = { dateId -> //TODO remove dateId?
                                isExpanded = !isExpanded
                            },
                            showLongTextSnackBar = { text, actionLabel, duration ->
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = text,
                                        actionLabel = actionLabel,
                                        duration = duration
                                    )
                                }
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
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxWidth(),
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
            AnimatedBottomSaveCancelBar(
                visible = isEditMode && showBottomSaveCancelBar,
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