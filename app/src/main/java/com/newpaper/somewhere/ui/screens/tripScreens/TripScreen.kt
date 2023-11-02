package com.newpaper.somewhere.ui.screens.tripScreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.ImageCard
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.InformationCard
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.MemoCard
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereNavigationBar
import com.newpaper.somewhere.ui.screens.mainScreens.MyTripsDestination
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SetCurrencyTypeDialog
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.TripDurationCard
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.AnimatedBottomSaveCancelBar
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.SeeOnMapExtendedFAB
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.ADDITIONAL_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.BottomSaveCancelBar
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.MAX_TITLE_LENGTH
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.TitleCard
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.DUMMY_SPACE_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.MIN_CARD_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SetColorDialog
import com.newpaper.somewhere.utils.SlideState
import com.newpaper.somewhere.utils.dragAndDropVertical
import com.newpaper.somewhere.viewModel.DateTimeFormat
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

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
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateToDate: (dateIndex: Int) -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteTrip: (deleteTrip: Trip) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,
    reorderTripImageList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    reorderDateList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    saveTrip: () -> Unit,

    modifier: Modifier = Modifier,
){
    val showingTrip =
        if (isEditMode) tempTrip
        else originalTrip

    val focusManager = LocalFocusManager.current

    val scrollState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSetCurrencyDialog by rememberSaveable { mutableStateOf(false) }
    var showBottomSaveCancelBar by rememberSaveable { mutableStateOf(true) }

    var errorCount by rememberSaveable { mutableIntStateOf(0) }
    var dateTitleErrorCount by rememberSaveable { mutableIntStateOf(0) }

    //slideStates
    val slideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingTrip.dateList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

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
    var topBarTitle by rememberSaveable { mutableStateOf("") }

    topBarTitle =
        if (isEditMode) stringResource(id = R.string.edit_trip)
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
        modifier = Modifier.displayCutoutPadding().statusBarsPadding().navigationBarsPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

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
                title = topBarTitle,

                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    if (!isEditMode) navigateUp()
                    else onBackButtonClick()
                },

                actionIcon1 = if (!isEditMode) MyIcons.edit else null,
                actionIcon1Onclick = {
                    changeEditMode(null)
                }
            )
        },

        //bottom floating button
        floatingActionButton = {
            SeeOnMapExtendedFAB(
                visible = !isEditMode && showingTrip.getFirstLocation() != null,
                onClick = navigateToTripMap,
                expanded = isFABExpanded
            )
        }
    ) { paddingValues ->

        //dialogs
        if (showExitDialog) {
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { showExitDialog = false },
                onDeleteClick = {
                    errorCount = 0
                    showExitDialog = false
                    changeEditMode(false)
                    updateTripState(true, originalTrip)

                    if (isNewTrip) {
                        navigateUpAndDeleteTrip(originalTrip)
                    }

                    organizeAddedDeletedImages(false)
                }
            )
        }

        if (showSetCurrencyDialog) {
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
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 200.dp),
                modifier = modifier.fillMaxSize()
            ) {

                //title card
                item {
                    val snackBarLongText = stringResource(id = R.string.long_text, 100)

                    TitleCard(
                        isEditMode = isEditMode,
                        titleText = showingTrip.titleText,
                        onTitleChange = { newTitleText ->
                            showingTrip.setTitleText(updateTripState, newTitleText)
                        },
                        focusManager = focusManager,
                        isLongText = {
                            if (it) errorCount++
                            else errorCount--
                        }
                    )
                }

                //image card
                item {
                    ImageCard(
                        tripId = showingTrip.id,
                        isEditMode = isEditMode,
                        imagePathList = showingTrip.imagePathList,
                        onClickImage = { initialImageIndex ->
                            navigateToImage(showingTrip.imagePathList, initialImageIndex)
                        },
                        onAddImages = { imageFiles ->
                            addAddedImages(imageFiles)
                            showingTrip.setImage(
                                updateTripState,
                                showingTrip.imagePathList + imageFiles
                            )
                        },
                        deleteImage = { imageFile ->
                            addDeletedImages(listOf(imageFile))

                            val newList: MutableList<String> =
                                showingTrip.imagePathList.toMutableList()
                            newList.remove(imageFile)

                            showingTrip.setImage(updateTripState, newList.toList())
                        },
                        isOverImage = {
                            if (it) errorCount++
                            else errorCount--
                        },
                        reorderImageList = reorderTripImageList
                    )
                }

                //trip duration card
                item {
                    val startDate = showingTrip.dateList.firstOrNull()
                    val endDate = showingTrip.getLastEnabledDate()
                    val endDateIndex = endDate?.index
                    val sameYear = startDate?.date?.year == endDate?.date?.year

                    val defaultDateRange =
                        if (showingTrip.dateList.isNotEmpty()) {
                            showingTrip.dateList.first().date..showingTrip.dateList[endDateIndex
                                ?: 0].date
                        } else
                            LocalDate.now().let { now -> now.plusDays(1)..now.plusDays(5) }

                    TripDurationCard(
                        defaultDateRange = defaultDateRange,
                        isDateListEmpty = showingTrip.dateList.isEmpty(),
                        startDateText = showingTrip.getStartDateText(dateTimeFormat, true),
                        endDateText = showingTrip.getEndDateText(dateTimeFormat, !sameYear),
                        durationText = showingTrip.getDurationText(),

                        dateTimeFormat = dateTimeFormat,

                        isEditMode = isEditMode,
                        setShowBottomSaveCancelBar = {
                            showBottomSaveCancelBar = it
                        },
                        setTripDuration = { startDate1, endDate1 ->
                            updateTripDurationAndTripState(true, startDate1, endDate1)
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
                            Triple(
                                MyIcons.travelDistance,
                                showingTrip.getTotalTravelDistanceText(),
                                null
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                //memo card
                item {
                    val snackBarLongText = stringResource(id = R.string.long_text, 100)

                    MemoCard(
                        isEditMode = isEditMode,
                        memoText = showingTrip.memoText,
                        onMemoChanged = { newMemoText ->
                            showingTrip.setMemoText(updateTripState, newMemoText)
                        },
                        isLongText = {
                            if (it) errorCount++
                            else errorCount--
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
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp, 0.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.dates),
                                    style = getTextStyle(TextType.CARD__TITLE),
                                    modifier = if (showingTrip.dateList.isNotEmpty()) Modifier.padding(
                                        0.dp, 0.dp, 0.dp, 0.dp
                                    )
                                    else Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                                )

                                //up to 100 characters
                                if (dateTitleErrorCount > 0) {
                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = stringResource(
                                            id = R.string.long_text,
                                            MAX_TITLE_LENGTH
                                        ),
                                        style = getTextStyle(TextType.CARD__TITLE_ERROR)
                                    )
                                }
                            }
                        }
                    }
                }

                val enabledDateList = showingTrip.dateList.filter { it.enabled }

                if (enabledDateList.isNotEmpty()) {
                    items(enabledDateList) { date ->

                        var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }

                        if (showColorPickerDialog) {
                            SetColorDialog(
                                initialColor = date.color,
                                onDismissRequest = {
                                    showColorPickerDialog = false
                                    showBottomSaveCancelBar = true
                                },
                                onOkClick = {
                                    showColorPickerDialog = false
                                    showBottomSaveCancelBar = true
                                    date.setColor(showingTrip, updateTripState, it)
                                }
                            )
                        }


                        key(enabledDateList.map { it.id }) {
                            val slideState = slideStates[date.id] ?: SlideState.NONE

                            DateListItem(
                                trip = showingTrip,
                                date = date,
                                isEditMode = isEditMode,
                                dateTimeFormat = dateTimeFormat,

                                slideState = slideState,
                                updateSlideState = { dateId, newSlideState ->
                                    slideStates[enabledDateList[dateId].id] = newSlideState
                                },
                                updateItemPosition = { currentIndex, destinationIndex ->
                                    //on drag end
                                    coroutineScope.launch {
                                        //reorder list
                                        reorderDateList(currentIndex, destinationIndex)

                                        //all slideState to NONE
                                        slideStates.putAll(enabledDateList.map { it.id }
                                            .associateWith { SlideState.NONE })
                                    }
                                },
                                updateTripState = updateTripState,

                                isLongText = {
                                    if (it) {
                                        errorCount++
                                        dateTitleErrorCount++
                                    } else {
                                        errorCount--
                                        dateTitleErrorCount--
                                    }
                                },
                                onItemClick = { navigateToDate(date.index) },
                                onPointClick =
                                if (isEditMode) {
                                    {
                                        showBottomSaveCancelBar = false
                                        showColorPickerDialog = true
                                    }
                                } else null
                            )
                        }
                    }
                }
                //no dates
                else {
                    item {
                        //empty dates
                        val text =
                            if (isEditMode) stringResource(id = R.string.set_trip_duration)
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

                        organizeAddedDeletedImages(true)
                    }
                },
                saveEnabled = errorCount <= 0
            )
        }
    }
}

@Composable
private fun DateListItem(
    trip: Trip,
    date: Date,

    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,

    slideState: SlideState,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    isLongText: (Boolean) -> Unit,

    onItemClick: () -> Unit,
    onSideTextClick: (() -> Unit)? = null,
    onPointClick: (() -> Unit)? = null
){
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    //get item height(px), use at drag reorder
    var itemHeight: Int
    val unExpandedItemHeight = MIN_CARD_HEIGHT + DUMMY_SPACE_HEIGHT * 2
    val expandedItemHeight = MIN_CARD_HEIGHT + DUMMY_SPACE_HEIGHT * 2 + ADDITIONAL_HEIGHT

    with(LocalDensity.current){
        itemHeight = if (isExpanded) expandedItemHeight.toPx().toInt()
                     else            unExpandedItemHeight.toPx().toInt()
    }

    //is dragged
    var isDragged by remember { mutableStateOf(false) }
    val zIndex = if (isDragged) 1.0f else 0.0f

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        },
        label = "vertical translation"
    )

    //item y offset
    val itemOffsetY = remember { Animatable(0f) }

    //item modifier
    val dragModifier =
        //set y offset while dragging or drag end
        if (isEditMode) Modifier
            .offset {
                if (isDragged) IntOffset(0, itemOffsetY.value.roundToInt())
                else IntOffset(0, verticalTranslation)
            }
            .zIndex(zIndex)
        else Modifier

    //item ui
    GraphListItem(
        modifier = dragModifier,
        dragHandleModifier = Modifier
            .dragAndDropVertical(
                item = date,
                items = trip.dateList,
                itemHeight = itemHeight,
                updateSlideState = updateSlideState,
                offsetY = itemOffsetY,
                onStartDrag = {
                    isDragged = true
                },
                onStopDrag = { currentIndex, destinationIndex ->

                    if (currentIndex != destinationIndex){
                        updateItemPosition(currentIndex, destinationIndex)
                    }

                    isDragged = false
                }
            ),

        pointColor = Color(date.color.color),
        isEditMode = isEditMode,
        isExpanded = isExpanded,

        sideText = date.getDateText(dateTimeFormat.copy(includeDayOfWeek = false), false),
        mainText = date.titleText,
        expandedText = date.getExpandedText(trip, isEditMode),

        onTitleTextChange = { dateTitleText ->
            trip.dateList[date.index].setTitleText(trip, updateTripState, dateTitleText)
        },

        isFirstItem = date == trip.dateList.first(),
        isLastItem = date == trip.dateList.last(),
        deleteEnabled = false,
        dragEnabled = true,

        onItemClick = onItemClick,
        onExpandedButtonClicked = {
            isExpanded = !isExpanded
        },
        onSideTextClick = onSideTextClick,
        onPointClick = onPointClick,
        isLongText = isLongText
    )
}