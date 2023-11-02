package com.newpaper.somewhere.ui.screens.tripScreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.enumUtils.SpotTypeGroup
import com.newpaper.somewhere.enumUtils.TimeFormat
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.DateListProgressBar
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.DummySpaceWithLine
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.InformationCard
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.MemoCard
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.NewItemButton
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.FilterCards
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.TitleWithColorCard
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.AnimatedBottomSaveCancelBar
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.SeeOnMapExtendedFAB
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.ADDITIONAL_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.MAX_TITLE_LENGTH
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.DUMMY_SPACE_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.MIN_CARD_HEIGHT
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SetSpotTypeDialog
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.dialogs.SetTimeDialog
import com.newpaper.somewhere.utils.SlideState
import com.newpaper.somewhere.utils.dragAndDropVertical
import com.newpaper.somewhere.viewModel.DateTimeFormat
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.math.roundToInt

object DateDestination : NavigationDestination {
    override val route = "date"
    override var title = "Date screen"
    const val dateIdArg = "dateId"
    val routeWithArgs = "$route/{$dateIdArg}"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateScreen(
    isEditMode: Boolean,

    originalTrip: Trip,
    tempTrip: Trip,
    dateIndex: Int,

    dateTimeFormat: DateTimeFormat,

    changeEditMode: (editMode: Boolean?) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    reorderSpotList: (dateIndex: Int, currentIndex: Int, destinationIndex: Int) -> Unit,
    saveTrip: () -> Unit,

    navigateUp: () -> Unit,
    navigateToSpot: (dateId: Int, spotId: Int) -> Unit,
    navigateToDateMap: () -> Unit,

    modifier: Modifier = Modifier
){

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    val showingTrip = if (isEditMode) tempTrip
    else            originalTrip

    val dateList = showingTrip.dateList

    val progressBarState = rememberLazyListState(
        initialFirstVisibleItemIndex = dateIndex
    )

    val datePagerState = rememberPagerState(
        initialPage = dateIndex,
        pageCount = { dateList.size }
    )

    val snackBarHostState = remember { SnackbarHostState() }
    var errorCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isEditMode){
        errorCount = 0
    }

    //animate progress bar when current page changed
    LaunchedEffect(datePagerState){
        snapshotFlow { datePagerState.currentPage }.collect {currentPage_ ->
            progressBarState.animateScrollToItem(currentPage_)
        }
    }

    //dialog
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showBottomSaveCancelBar by rememberSaveable { mutableStateOf(true) }


    //when back button click
    val onBackButtonClick = {
        if (!isEditMode) navigateUp()
        else {
            if (originalTrip != tempTrip)
                showExitDialog = true
            else {
                changeEditMode(false)
            }
        }
    }

    BackHandler {
        onBackButtonClick()
    }

    //set top bar title
    DateDestination.title =
        if (isEditMode) stringResource(id = R.string.edit_date)
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText
        }

    //for expanded fab animation
    var isFABExpanded by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.displayCutoutPadding().statusBarsPadding().navigationBarsPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .imePadding()
            )
        },
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                title = DateDestination.title,

                //back button
                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    onBackButtonClick()
                },

                actionIcon1 = if (!isEditMode) MyIcons.edit else null,
                actionIcon1Onclick = {
                    changeEditMode(null)
                }
            )
        },

        //bottom floating button: see on map
        floatingActionButton = {
            SeeOnMapExtendedFAB(
                visible = !isEditMode && showingTrip.getFirstLocation() != null,
                onClick = navigateToDateMap,
                expanded = isFABExpanded
            )
        }
    ) { paddingValues ->

        if(showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { showExitDialog = false },
                onDeleteClick = {
                    showExitDialog = false
                    changeEditMode(false)
                    updateTripState(true, originalTrip)

                    organizeAddedDeletedImages(false)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                MySpacerColumn(height = 4.dp)

                //progress bar
                DateListProgressBar(
                    progressBarState = progressBarState,
                    dateList = dateList,
                    currentDateIdx = datePagerState.currentPage,
                    dateTimeFormat = dateTimeFormat,
                    onClickDate = { toDateIndex ->
                        //progress bar animate
                        coroutineScope.launch {
                            progressBarState.animateScrollToItem(toDateIndex)
                        }

                        //page animate
                        coroutineScope.launch {
                            datePagerState.animateScrollToPage(toDateIndex)
                        }
                    }
                )

                MySpacerColumn(height = 8.dp)

                //spot pages
                //pageIndex == dateOrderId
                HorizontalPager(
                    state = datePagerState,
                    modifier = Modifier.weight(1f),
                    pageContent =  {pageIndex ->
                        //pageIndex == dateOrderId

                        DatePage(
                            isEditMode = isEditMode,
                            showingTrip = showingTrip,
                            dateIndex = pageIndex,
                            currentDate = dateList[pageIndex],
                            timeFormat = dateTimeFormat.timeFormat,

                            focusManager = focusManager,
                            snackBarHostState = snackBarHostState,

                            setShowBottomSaveCancelBar = {
                                showBottomSaveCancelBar = it
                            },
                            onErrorCountChange = { plusError ->
                                if (plusError) errorCount++
                                else errorCount--
                            },
                            updateTripState = updateTripState,

                            addNewSpot = { dateId ->
                                addNewSpot(dateId)
                            },
                            deleteSpot = { dateIndex, spotIndex ->
                                deleteSpot(dateIndex, spotIndex)
                                addDeletedImages(dateList[dateIndex].spotList[spotIndex].imagePathList)
                            },
                            navigateToSpot = navigateToSpot,
                            setIsFABExpanded = {
                                isFABExpanded = it
                            },
                            showSnackBar = { text_, actionLabel_, duration_ ->
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = text_,
                                        actionLabel = actionLabel_,
                                        duration = duration_
                                    )
                                }
                            },
                            reorderSpotList = { currentIndex, destinationIndex ->
                                reorderSpotList(pageIndex, currentIndex, destinationIndex)
                            },
                            modifier = modifier.padding(16.dp, 0.dp)
                        )
                    }
                )
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
fun DatePage(
    isEditMode: Boolean,
    showingTrip: Trip,
    dateIndex: Int,
    currentDate: Date,
    timeFormat: TimeFormat,

    focusManager: FocusManager,
    snackBarHostState: SnackbarHostState,

    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    onErrorCountChange: (plusError: Boolean) -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateId: Int) -> Unit,
    deleteSpot: (dateId: Int, spotId: Int) -> Unit,

    navigateToSpot: (dateId: Int, spotId: Int) -> Unit,
    setIsFABExpanded: (Boolean) -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit,
    reorderSpotList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier
){
    val dateList = showingTrip.dateList
    val spotList = currentDate.spotList

    val coroutineScope = rememberCoroutineScope()

    var spotTypeWithShownList by rememberSaveable{
        mutableStateOf(getSpotTypeGroupWithShownList(currentDate))
    }

    val scrollState = rememberLazyListState()

    //slideStates
    val slideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingTrip.dateList[dateIndex].spotList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    var spotTitleErrorCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isEditMode){
        spotTitleErrorCount = 0
    }

    LaunchedEffect(key1 = scrollState.firstVisibleItemIndex == 0 ){
        setIsFABExpanded(scrollState.firstVisibleItemIndex == 0)
    }


    //FIXME spotTypeWithShownList order problem
    //왜 순서가 바뀌지?? follow order of spotList

    LazyColumn(
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(top = 8.dp, bottom = 200.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        //title card
        item {
            TitleWithColorCard(
                isEditMode = isEditMode,

                titleText = currentDate.titleText,
                onTitleChange = {newTitleText ->
                    currentDate.setTitleText(showingTrip, updateTripState, newTitleText)
                },
                focusManager = focusManager,
                isLongText = { onErrorCountChange(it) },

                color = currentDate.color,
                onColorChange = {newDateColor ->
                    currentDate.setColor(showingTrip, updateTripState, newDateColor)
                },

                setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,

                modifier = modifier
            )
        }

        //budget, travel distance info card
        item {
            InformationCard(
                modifier = modifier,
                list = listOf(
                    Pair(MyIcons.budget, currentDate.getTotalBudgetText(showingTrip)),
                    Pair(
                        MyIcons.travelDistance,
                        currentDate.getTotalTravelDistanceText(showingTrip)
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        //memo card
        item {
            MemoCard(
                isEditMode = isEditMode,
                memoText = currentDate.memo,
                modifier = modifier,
                onMemoChanged = { newMemoText ->
                    currentDate.setMemoText(showingTrip, updateTripState, newMemoText)
                },
                isLongText = { onErrorCountChange(it) },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        spotTypeWithShownList = updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)

        if (currentDate.spotList.isNotEmpty()) {

            val firstSpotShowUpperLine = spotList.first().spotType.isMove()
                || spotList[0].getPrevSpot(dateList, dateIndex)?.spotType?.isMove() ?: false

            val lastSpotShowLowerLine = spotList.last().spotType.isMove()
                    || spotList[spotList.indexOf(spotList.last())].getNextSpot(dateList, dateIndex)?.spotType?.isMove() ?: false

            //spot type card list horizontal
            // 3 Tour / 2 Food / 5 Move ...
            item{
                FilterCards(
                    date = currentDate,
                    spotTypeWithShownList = spotTypeWithShownList,
                    onCardClicked = {spotType ->

                        val newList = spotTypeWithShownList.map{
                            if (it.first == spotType) Pair (it.first, !it.second)
                            else it
                        }

                        spotTypeWithShownList = newList
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            //currentDate.allSpotCollapse()

            //spot list
            if(checkSpotListIsShown(spotTypeWithShownList)) {
                item {
                    StartEndDummySpaceWithRoundedCorner(
                        showLine = firstSpotShowUpperLine,
                        isFirst = true,
                        isLast = false,
                        modifier = modifier
                    )
                }

                //spots title when edit mode
                item {
                    val textModifier =
                        if (showingTrip.dateList.isNotEmpty())
                            Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
                        else Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)

                    var textHeight by rememberSaveable { mutableStateOf(0) }

                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = expandVertically(tween(400)),
                        exit = shrinkVertically(tween(400))
                    ) {
                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
//
//                        }
//                        Card(
//                            modifier = modifier.fillMaxWidth()
//                        ) {
                            Box {
                                Row(
                                    modifier = Modifier.padding(16.dp, 0.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.spots),
                                        style = getTextStyle(TextType.CARD__TITLE),
                                        modifier = textModifier.onGloballyPositioned {
                                            textHeight = it.size.height
                                        }
                                    )

                                    //up to 100 characters
                                    if (spotTitleErrorCount > 0){
                                        Spacer(modifier = Modifier.weight(1f))

                                        Text(
                                            text = stringResource(id = R.string.long_text, MAX_TITLE_LENGTH),
                                            style = getTextStyle(TextType.CARD__TITLE_ERROR)
                                        )
                                    }
                                }

                                if(firstSpotShowUpperLine)
                                    DummySpaceWithLine(height = (textHeight / LocalDensity.current.density).dp)
                            }
                        }
                    }
                }


                //list with line
                items(spotList) { spot ->
                    var showSpotTypeDialog by rememberSaveable { mutableStateOf(false) }
                    var showTimePicker by rememberSaveable { mutableStateOf(false) }

                    if (showSpotTypeDialog) {
                        SetSpotTypeDialog(
                            initialSpotType = spot.spotType,
                            onDismissRequest = {
                                showSpotTypeDialog = false
                                setShowBottomSaveCancelBar(true)
                            },
                            onOkClick = { newSpotType ->
                                showSpotTypeDialog = false
                                setShowBottomSaveCancelBar(true)
                                spot.setSpotType(
                                    showingTrip,
                                    dateList,
                                    dateIndex,
                                    updateTripState,
                                    newSpotType
                                )
                            }
                        )
                    }

                    if (showTimePicker){
//                        setUseImePadding(false)
                        SetTimeDialog(
                            initialTime = spot.startTime ?: LocalTime.of(12,0),
                            timeFormat = timeFormat,
                            isSetStartTime = true,
                            onDismissRequest = {
                                showTimePicker = false
                                setShowBottomSaveCancelBar(true)
                            },
                            onConfirm = {newTime ->
                                spot.setStartTime(showingTrip, dateIndex, updateTripState, newTime)
                                showTimePicker = false
                                setShowBottomSaveCancelBar(true)
                            }
                        )
                    }


                    key(spotList.map { it.id }){
                        val slideState = slideStates[spot.id] ?: SlideState.NONE

                        if (checkSpotTypeGroupIsShown(spot.spotType.group, spotTypeWithShownList)) {

                            SpotListItem(
                                modifier = modifier,
                                trip = showingTrip,
                                dateId = dateIndex,
                                spot = spot,
                                isEditMode = isEditMode,
                                timeFormat = timeFormat,
                                slideState = slideState,
                                updateSlideState = { dateId, newSlideState ->
                                    slideStates[spotList[dateId].id] = newSlideState
                                },
                                updateItemPosition = { currentIndex, destinationIndex ->
                                    //on drag end
                                    coroutineScope.launch {
                                        //reorder list
                                        reorderSpotList(currentIndex, destinationIndex)

                                        //all slideState to NONE
                                        slideStates.putAll(spotList.map { it.id }
                                            .associateWith { SlideState.NONE })
                                    }
                                },
                                updateTripState = updateTripState,
                                onTitleTextChange = { spotTitleText ->
                                    spotList[spot.index].setTitleText(showingTrip, dateIndex, updateTripState, spotTitleText)
                                },
                                isLongText = {
                                    if (it) spotTitleErrorCount++
                                    else    spotTitleErrorCount--
                                    onErrorCountChange(it)
                                },
                                onItemClick = {
                                    navigateToSpot(dateIndex, spotList.indexOf(spot))
                                },
                                onDeleteClick = {
                                    //dialog: ask delete
                                    deleteSpot(dateIndex, spot.index)
                                    spotTypeWithShownList =
                                        updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)
                                },
                                onSideTextClick =
                                    if (isEditMode){
                                        {
                                            showTimePicker = true
                                            setShowBottomSaveCancelBar(false)
                                        }
                                    }
                                    else null,
                                onPointClick =
                                    if (isEditMode){
                                        {
                                            showSpotTypeDialog = true
                                            setShowBottomSaveCancelBar(false)
                                        }
                                    }
                                    else null
                            )
                        }
                    }
                }

                item {
                    StartEndDummySpaceWithRoundedCorner(
                        showLine = lastSpotShowLowerLine,
                        isFirst = false,
                        isLast = true,
                        modifier = modifier
                    )
                }
            }
            else {
                //no chosen spot type
                item {
                    MySpacerColumn(height = 16.dp)

                    NoChosenSpotTypeGroupText()
                }
            }
        }
        else{
            //no date
            // No plan... Let's create a new plan!
            item {
                NoPlanText()
            }
        }

        //new spot button
        if(isEditMode){
            item {
                MySpacerColumn(height = 16.dp)

                NewItemButton(
                    text = stringResource(id = R.string.new_spot),
                    onClick = {
                        //add new spot
                        addNewSpot(dateIndex)
                        spotTypeWithShownList = updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)
                    }
                )
            }
        }
    }
}

@Composable
fun SpotListItem(
    trip: Trip,
    dateId: Int,
    spot: Spot,

    isEditMode: Boolean,

    timeFormat: TimeFormat,

    slideState: SlideState,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    onTitleTextChange: (title: String) -> Unit,
    isLongText: (Boolean) -> Unit,

    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSideTextClick: (() -> Unit)?,
    onPointClick: (() -> Unit)?,

    modifier: Modifier = Modifier
){
    val dateList = trip.dateList
    val spotList = trip.dateList[dateId].spotList

    //set point color
    val pointColor =
        if (spot.spotType.isMove())
            Color.Transparent
        else
            Color(spot.spotType.group.color.color)

    //is expanded
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
        if (isEditMode) modifier
            .offset {
                if (isDragged) IntOffset(0, itemOffsetY.value.roundToInt())
                else IntOffset(0, verticalTranslation)
            }
            .zIndex(zIndex)
        else modifier

    //item ui
    GraphListItem(
        modifier = dragModifier,
        dragHandleModifier = Modifier
            .dragAndDropVertical(
                spot, spotList,
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

        pointColor = pointColor,
        isEditMode = isEditMode,
        isExpanded = isExpanded,

        iconText = if (spot.spotType.isNotMove()) spot.iconText.toString()
        else null,
        iconTextColor = spot.spotType.group.color.onColor,

        sideTextPlaceHolderIcon = MyIcons.time,
        sideText = spot.getStartTimeText(timeFormat) ?: "",
        mainText = spot.titleText,
        expandedText = spot.getExpandedText(trip, isEditMode),

        onTitleTextChange = onTitleTextChange,

        isFirstItem = spot.spotType.isNotMove()
                && spot == spotList.first()
                && spot.getPrevSpot(dateList, dateId)?.spotType?.isNotMove() ?: true,

        isLastItem = spot.spotType.isNotMove()
                && spot == spotList.last()
                && spot.getNextSpot(dateList, dateId)?.spotType?.isNotMove() ?: true,

        deleteEnabled = true,
        dragEnabled = true,

        onItemClick = onItemClick,
        onExpandedButtonClicked = {
            isExpanded = !isExpanded
        },
        onDeleteClick = onDeleteClick,
        onSideTextClick = onSideTextClick,
        onPointClick = onPointClick,

        isLongText = isLongText
    )
}

private fun getSpotTypeGroupWithShownList(
    date: Date
):  List<Pair<SpotTypeGroup, Boolean>>{
    val list: MutableList<Pair<SpotTypeGroup, Boolean>> = mutableListOf()

    val spotTypeList = enumValues<SpotTypeGroup>()

    for (spotType in spotTypeList){
        val a = date.getSpotTypeGroupCount(spotTypeGroup = spotType)
        if (a != 0)
            list.add(Pair(spotType, true))
    }

    return list
}

private fun updateSpotTypeGroupWithShownList(
    date: Date,
    spotTypeGroupWithShownList: List<Pair<SpotTypeGroup, Boolean>>
):  List<Pair<SpotTypeGroup, Boolean>>{
    val list: MutableList<Pair<SpotTypeGroup, Boolean>> = mutableListOf()

    for (spot in date.spotList) {

        if (Pair(spot.spotType.group, true) in spotTypeGroupWithShownList
            && Pair(spot.spotType.group, true) !in list
        ){
            list.add(Pair(spot.spotType.group, true))
        }

        else if (Pair(spot.spotType.group, false) in spotTypeGroupWithShownList
            && Pair(spot.spotType.group, false) !in list
        ){
            list.add(Pair(spot.spotType.group, false))
        }

        else if (Pair(spot.spotType.group, true) !in list && Pair(spot.spotType.group, false) !in list){
            list.add(Pair(spot.spotType.group, true))
        }
    }

    return list
}

//check at least one SpotType is true
private fun checkSpotListIsShown(
    spotTypeGroupWithShownList: List<Pair<SpotTypeGroup, Boolean>>
): Boolean {
    for (spotWithBoolean in spotTypeGroupWithShownList){
        if (spotWithBoolean.second)
            return true
    }
    return false
}



@Composable
private fun NoPlanText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.no_plan),
            style = textStyle,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoChosenSpotTypeGroupText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.choose_spot_type),
            style = textStyle
        )
    }
}

fun checkSpotTypeGroupIsShown(
    spotTypeGroup: SpotTypeGroup, spotTypeGroupWithShownList: List<Pair<SpotTypeGroup, Boolean>>
): Boolean {
    for (spotWithShown in spotTypeGroupWithShownList){
        if (spotWithShown.first == spotTypeGroup  && spotWithShown.second)
            return true
        else if (spotWithShown.first == spotTypeGroup)
            return false
    }
    return false
}