package com.example.somewhere.ui.tripScreens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.tripScreenUtils.SaveCancelButtons
import com.example.somewhere.ui.tripScreenUtils.DeleteItemButton
import com.example.somewhere.ui.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.example.somewhere.ui.tripScreenUtils.cards.ImageCard
import com.example.somewhere.ui.tripScreenUtils.cards.InformationCard
import com.example.somewhere.ui.tripScreenUtils.cards.MapCard
import com.example.somewhere.ui.tripScreenUtils.MapChooseLocation
import com.example.somewhere.ui.tripScreenUtils.cards.MemoCard
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.tripScreenUtils.dialogs.SetSpotTypeDialog
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.example.somewhere.ui.tripScreenUtils.dialogs.SetBudgetOrDistanceDialog
import com.example.somewhere.ui.tripScreenUtils.SpotListProgressBar
import com.example.somewhere.ui.tripScreenUtils.cards.DateTimeCard
import com.example.somewhere.ui.tripScreenUtils.cards.TitleCardMove
import com.example.somewhere.ui.tripScreenUtils.cards.ZoomCard
import com.example.somewhere.ui.tripScreenUtils.focusOnToSpot
import com.example.somewhere.ui.tripScreenUtils.DEFAULT_ZOOM_LEVEL
import com.example.somewhere.ui.tripScreenUtils.SEOUL_LOCATION
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.AnimatedBottomSaveCancelBar
import com.example.somewhere.ui.tripScreenUtils.cards.TitleCard
import com.example.somewhere.viewModel.DateTimeFormat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SpotDetailDestination : NavigationDestination {
    override val route = "spot detail"
    override var title = ""
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotScreen(
    isEditMode: Boolean,
    setUseImePadding: (useImePadding: Boolean) -> Unit,

    originalTrip: Trip,
    tempTrip: Trip,
    dateIndex: Int,
    spotIndex: Int,    //fixed value do not use / use [currentSpotId]

    dateTimeFormat: DateTimeFormat,

    changeEditMode: (editMode: Boolean?) -> Unit,
    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,
    reorderSpotImageList: (dateIndex: Int, spotIndex: Int, currentIndex: Int, destinationIndex: Int) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateId: Int) -> Unit,
    deleteSpot: (dateId: Int, spotId: Int) -> Unit,
    saveTrip: () -> Unit,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    onImageClicked: () -> Unit,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
){
    val showingTrip = if (isEditMode) tempTrip
                      else            originalTrip

//    Log.d("trip", "SpotScreen.kt ${showingTrip.dateList.first().spotList.first()}")

    val context = LocalContext.current

    var currentDateIndex by rememberSaveable { mutableStateOf(dateIndex) }
    var currentSpotIndex by rememberSaveable { mutableStateOf(spotIndex) }

    val dateList = showingTrip.dateList
    val spotList = dateList[currentDateIndex].spotList

    //var prevSpotId by rememberSaveable { mutableStateOf(currentSpotId) }

    val currentSpot = spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var isEditLocationMode by rememberSaveable { mutableStateOf(false) }
    var isMapExpand by rememberSaveable { mutableStateOf(false) }
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }

    var errorCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isEditMode){
        errorCount = 0

        if (isEditMode)
            isMapExpand = false
    }

    val scrollState = rememberLazyListState()

    val spotPagerState = rememberPagerState(
        initialPage = currentSpotIndex
    )

    val progressBarState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentSpotIndex
    )

    //dialog: delete trip? unsaved data will be deleted
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showBottomSaveCancelBar by rememberSaveable { mutableStateOf(true) }

    val snackBarHostState = remember { SnackbarHostState() }



    //when back button click
    val onBackButtonClick = {
        if (!isEditMode) navigateUp()
        else {
            if (isEditLocationMode)
                isEditLocationMode = false
            else {
                if (originalTrip != tempTrip)
                    showExitDialog = true
                else {
                    changeEditMode(false)
                }
            }
        }
    }

    BackHandler {
        onBackButtonClick()
    }

    //===============================================================
    var spotFrom: Spot? = null
    var spotTo: Spot? = null

    if(currentSpot?.spotType?.isMove() == true) {
        spotFrom = currentSpot.getPrevSpot(dateList, spotList, currentDateIndex)
        spotTo = currentSpot.getNextSpot(dateList, spotList, currentDateIndex)
    }

    val location =
        if (currentSpot?.spotType?.isNotMove() == true)
            currentSpot.location ?: SEOUL_LOCATION
        else
            spotFrom?.location ?: SEOUL_LOCATION


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, currentSpot?.zoomLevel ?: DEFAULT_ZOOM_LEVEL)
    }

    val density = LocalDensity.current.density

    //[when current page changed] -> animate progress bar / map spot
    LaunchedEffect(spotPagerState, spotList){
        snapshotFlow { spotPagerState.currentPage }.collect { currentPageIndex ->
            currentSpotIndex = currentPageIndex

            //progress bar animate
            coroutineScope.launch {
                val toIdx = if (currentPageIndex == 0) 0
                            else currentPageIndex + 1
                progressBarState.animateScrollToItem(toIdx)
            }

            //map spot animate
            if (currentSpot != null) {
                coroutineScope.launch {
                    coroutineScope.launch {
                        mapAnimateToSpot(
                            scrollState,
                            spotList[currentPageIndex],
                            dateList,
                            spotList,
                            currentDateIndex,
                            cameraPositionState,
                            mapSize,
                            density,
                            coroutineScope
                        )
                    }
                }
            }
        }
    }

    //set top bar title
    DateDestination.title =
        if (isEditLocationMode)
            stringResource(id = R.string.set_location)

        else if (isEditMode)
            stringResource(id = R.string.edit_spot)

        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText
        }

    val dateTitle = dateList[currentDateIndex].titleText
    val subTitle =
        if (isEditLocationMode)
            null
        else {
            if (dateTitle == null)
                dateList[currentDateIndex].getDateText(dateTimeFormat, includeYear = true)
            else
                stringResource(id = R.string.sub_title, dateList[currentDateIndex].getDateText(dateTimeFormat, includeYear = true), dateTitle)
        }

    val snackBarPadding by animateFloatAsState(
        targetValue = if (isMapExpand) 75f
                        else if (isEditMode && !isEditLocationMode) 56f
                        else if (isEditLocationMode) {
                            if (LocalConfiguration.current.screenWidthDp > 670)
                                90f
                            else 150f
                        }
                        else 0f,
        animationSpec = tween(300),
        label = "snackbar padding"
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .padding(0.dp, 0.dp, 0.dp, snackBarPadding.dp)
                    .imePadding()
            )
        },
        //top bar
        topBar = {
            SomewhereTopAppBar(
                title = DateDestination.title,
                subTitle = subTitle,

                //back button
                navigationIcon =
                    if (isEditLocationMode) MyIcons.close
                    else                    MyIcons.back,
                navigationIconOnClick = {
                    onBackButtonClick()
                },

                actionIcon1 = if (!isEditMode) MyIcons.edit else null,
                actionIcon1Onclick = {
                    changeEditMode(null)
                }
            )
        }
    ) { paddingValue ->

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
                .padding(top = paddingValue.calculateTopPadding()),
            contentAlignment = Alignment.BottomCenter
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                MySpacerColumn(height = 8.dp)

                //progress bar
                SpotListProgressBar(
                    initialIdx = currentSpotIndex,
                    progressBarState = progressBarState,
                    isEditMode = isEditMode,

                    dateTimeFormat = dateTimeFormat,
                    dateList = dateList,
                    dateId = currentDateIndex,
                    spotList = spotList,
                    currentSpotIdx = currentSpotIndex,
                    addNewSpot = {
                        coroutineScope.launch {
                            addNewSpot(currentDateIndex)
                            delay(70)
                            val lastIdx = spotList.size
                            currentSpotIndex = lastIdx
                            spotPagerState.animateScrollToPage(lastIdx)

                        }
                    },
                    onClickSpot = {toSpotId ->
                        currentSpotIndex = toSpotId

                        //[when click progress bar item] -> pager animate
                        coroutineScope.launch {
                            spotPagerState.animateScrollToPage(toSpotId)
                        }
                    },
                    onPrevDateClick = {toDateId ->
                        currentDateIndex = toDateId
                        val lastSpotIndex = dateList[toDateId].spotList.lastIndex
                        currentSpotIndex = if (lastSpotIndex == -1) 0 else lastSpotIndex
                    },
                    onNextDateClick = {toDateId ->
                        currentDateIndex = toDateId
                        currentSpotIndex = 0
                    }
                )

                MySpacerColumn(height = 8.dp)

                var lazyColumnHeight by rememberSaveable { mutableStateOf(0) }

                //map + spot page
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            lazyColumnHeight = it.size.height
                        },
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = !isMapExpand
                ) {

                    if (spotList.isEmpty()){
                        item{
                            Text(stringResource(id = R.string.no_spot))
                        }
                    }
                    else {
                        if (currentSpot != null) {
                            //map card
                            item {

                                MapCard(
                                    isEditMode = isEditMode,
                                    isMapExpand = isMapExpand,
                                    expandHeight = (lazyColumnHeight / LocalDensity.current.density).toInt() - 1,
                                    mapSize = mapSize,

                                    isDarkMapTheme = isDarkMapTheme,
                                    fusedLocationClient = fusedLocationClient,
                                    userLocationEnabled = userLocationEnabled,
                                    setUserLocationEnabled = setUserLocationEnabled,
                                    cameraPositionState = cameraPositionState,

                                    spotList = spotList,
                                    currentSpot = currentSpot,
                                    onFullScreenClicked = {
                                        isMapExpand = !isMapExpand

                                        //scroll to top(map)
                                        if (isMapExpand)
                                            coroutineScope.launch {
                                                scrollState.animateScrollToItem(0)
                                            }
                                    },

                                    toggleIsEditLocation = {
                                        isEditLocationMode = !isEditLocationMode
                                    },
                                    deleteLocation = {
                                        currentSpot.setLocation(
                                            showingTrip,
                                            currentDateIndex,
                                            updateTripState,
                                            null,
                                            null
                                        )
                                    },
                                    setMapSize = {
                                        mapSize = it
                                    },
                                    showSnackBar = { text_, actionLabel_, duration_ ->
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar(
                                                text_,
                                                actionLabel_,
                                                duration = duration_
                                            )
                                        }
                                    },
                                    spotFrom = spotFrom,
                                    spotTo = spotTo
                                )
                            }


                            //each spot page
                            item {
                                HorizontalPager(
                                    pageCount = spotList.size,
                                    state = spotPagerState,
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.weight(1f)
                                ) { pageIndex ->     //pageIndex == spotIndex

                                    if (!isEditLocationMode) {
                                        //each page
                                        SpotDetailPage(
                                            isEditMode = isEditMode,
                                            setUseImePadding = setUseImePadding,
                                            snackBarHostState = snackBarHostState,

                                            showingTrip = showingTrip,
                                            dateId = currentDateIndex,
                                            currentDate = dateList[currentDateIndex],
                                            spotIndex = pageIndex,
                                            currentSpot = dateList[currentDateIndex].spotList.getOrNull(
                                                pageIndex
                                            ),

                                            dateTimeFormat = dateTimeFormat,
                                            focusManager = focusManager,
                                            onErrorCountChange = {
                                                if (it) errorCount++
                                                else    errorCount--
                                            },

                                            addAddedImageList = addAddedImages,
                                            addDeletedImageList = addDeletedImages,
                                            reorderSpotImageList = { currentIndex, destinationIndex ->
                                                reorderSpotImageList(currentDateIndex, pageIndex, currentIndex, destinationIndex)
                                            },

                                            setShowBottomSaveCancelBar = {
                                                showBottomSaveCancelBar = it
                                            },
                                            updateTripState = updateTripState,

                                            toggleIsEditLocation = {
                                                isEditLocationMode = !isEditLocationMode
                                            },

                                            onImageClicked = onImageClicked,
                                            deleteSpot = {
                                                addDeletedImages(currentSpot.imagePathList)

                                                if (spotList.size <= 1) {
                                                    deleteSpot(currentDateIndex, currentSpotIndex)
                                                } else {
                                                    var toIdx = currentSpotIndex - 1

                                                    if (toIdx < 0) {
                                                        toIdx = currentSpotIndex
                                                    }

                                                    coroutineScope.launch {
                                                        spotPagerState.animateScrollToPage(toIdx)
                                                        progressBarState.animateScrollToItem(toIdx + 1)
                                                        deleteSpot(currentDateIndex, currentSpotIndex)
                                                    }
                                                }
                                            },
                                            navigateUp = navigateUp,
                                            modifier = modifier.padding(16.dp, 0.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
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

            //set location page
            AnimatedVisibility(
                visible = isEditLocationMode,
                enter = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }),
                exit = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })
            ) {
                SetLocationPage(
                    showingTrip = showingTrip,
                    spotList = spotList,
                    currentSpotId = currentSpotIndex,
                    dateList = dateList,
                    dateId = currentDateIndex,
                    isDarkMapTheme = isDarkMapTheme,
                    updateTripState = updateTripState,
                    toggleIsEditLocationMode = {
                        isEditLocationMode = false
                    },
                    fusedLocationClient = fusedLocationClient,
                    setUserLocationEnabled = setUserLocationEnabled,
                    showSnackBar = { text, actionLabel, duration ->
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(text, actionLabel, duration = duration)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SpotDetailPage(
    isEditMode: Boolean,
    setUseImePadding: (useImePadding: Boolean) -> Unit,
    snackBarHostState: SnackbarHostState,

    showingTrip: Trip,
    dateId: Int,
    currentDate: Date,
    spotIndex: Int,
    currentSpot: Spot?,

    dateTimeFormat: DateTimeFormat,

    focusManager: FocusManager,
    onErrorCountChange: (plusError: Boolean) -> Unit,

    addAddedImageList: (List<String>) -> Unit,
    addDeletedImageList: (List<String>) -> Unit,
    reorderSpotImageList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    toggleIsEditLocation: (spotId: Int) -> Unit,

    onImageClicked: () -> Unit,
    deleteSpot: () -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
){
    val dateList = showingTrip.dateList
    val spotList = showingTrip.dateList[dateId].spotList

    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(top = 0.dp, bottom = 300.dp),
        modifier = Modifier
            .heightIn(300.dp, 5000.dp)
            .padding(16.dp, 0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        //userScrollEnabled = false
    ) {
        if (currentSpot != null){

            //title card
            item {
                MySpacerColumn(height = 16.dp)

                if (currentSpot.spotType.isNotMove())
                    TitleCard(
                        isEditMode = isEditMode,
                        titleText = currentSpot.titleText,
                        onTitleChange = { newTitleText ->
                            currentSpot.setTitleText(
                                showingTrip,
                                dateId,
                                updateTripState,
                                newTitleText
                            )
                        },
                        focusManager = focusManager,
                        isLongText = { onErrorCountChange(it) }
                    )
                else {
                    TitleCardMove(
                        isEditMode = isEditMode,
                        titleText = currentSpot.titleText,
                        onTitleChange = { newTitleText ->
                            currentSpot.setTitleText(
                                showingTrip,
                                dateId,
                                updateTripState,
                                newTitleText
                            )
                        },
                        focusManager = focusManager,
                        isLongText = { onErrorCountChange(it) }
                    )

                    MySpacerColumn(height = 16.dp)
                }
            }

            //date time card
            item {
                DateTimeCard(
                    setUseImePadding = setUseImePadding,
                    dateList = dateList,
                    date = currentDate,
                    spot = currentSpot,
                    isEditMode = isEditMode,
                    dateTimeFormat = dateTimeFormat,
                    setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                    changeDate = { newDateId ->
                        if (dateId != newDateId) {
                            navigateUp()
                            showingTrip.moveSpotToDate(
                                showingTrip,
                                dateId,
                                spotIndex,
                                newDateId,
                                updateTripState
                            )
                        }
                    },
                    setStartTime = { startTime ->
                        currentSpot.setStartTime(showingTrip, dateId, updateTripState, startTime)
                    },
                    setEndTime = { endTime ->
                        currentSpot.setEndTime(showingTrip, dateId, updateTripState, endTime)
                    }
                )

                MySpacerColumn(height = 16.dp)
            }

            //spot type, budget, travel distance card
            item {
                var showSpotTypeDialog by rememberSaveable { mutableStateOf(false) }
                var showBudgetDialog by rememberSaveable { mutableStateOf(false) }
                var showDistanceDialog by rememberSaveable { mutableStateOf(false) }

                val setImePadding = {
                    coroutineScope.launch {
                        delay(80)
                        setUseImePadding(true)
                    }
                }

                if (showSpotTypeDialog) {
                    SetSpotTypeDialog(
                        initialSpotType = currentSpot.spotType,
                        onDismissRequest = {
                            showSpotTypeDialog = false
                            setShowBottomSaveCancelBar(true)
                        },
                        onOkClick = { newSpotType ->
                            showSpotTypeDialog = false
                            setShowBottomSaveCancelBar(true)
                            currentSpot.setSpotType(
                                showingTrip,
                                dateId,
                                updateTripState,
                                newSpotType
                            )
                        }
                    )
                }

                if (showBudgetDialog) {
                    setUseImePadding(false)
                    SetBudgetOrDistanceDialog(
                        currencySymbol = showingTrip.unitOfCurrencyType.symbol,
                        initialValue = currentSpot.budget,
                        onDismissRequest = {
                            showBudgetDialog = false
                            setShowBottomSaveCancelBar(true)
                            setImePadding()
                        },
                        onSaveClick = { newBudget ->
                            showBudgetDialog = false
                            setShowBottomSaveCancelBar(true)
                            currentSpot.setBudget(showingTrip, dateId, updateTripState, newBudget)
                            setImePadding()
                        }
                    )
                }

                if (showDistanceDialog) {
                    setUseImePadding(false)
                    SetBudgetOrDistanceDialog(
                        initialValue = currentSpot.travelDistance,
                        onDismissRequest = {
                            showDistanceDialog = false
                            setShowBottomSaveCancelBar(true)
                            setImePadding()
                        },
                        onSaveClick = {
                            showDistanceDialog = false
                            setShowBottomSaveCancelBar(true)
                            setImePadding()
                        }
                    )
                }

                if (currentSpot.spotType.isMove()) {
                    currentSpot.updateDistance(
                        showingTrip = showingTrip,
                        currentDateIndex = dateId,
                        updateTripState = updateTripState,
                        spotFrom = currentSpot.getPrevSpot(dateList, spotList, dateId)?.location,
                        spotTo = currentSpot.getNextSpot(dateList, spotList, dateId)?.location
                    )
                }

                InformationCard(
                    isEditMode = isEditMode,
                    list = listOf(
                        Triple(MyIcons.category, currentSpot.getSpotTypeText()) {
                            showSpotTypeDialog = true
                            setShowBottomSaveCancelBar(false)
                        },

                        Triple(MyIcons.budget, currentSpot.getBudgetText(showingTrip)) {
                            showBudgetDialog = true
                            setShowBottomSaveCancelBar(false)
                        },

                        Triple(MyIcons.travelDistance, currentSpot.getDistanceText()) {
                            if (currentSpot.spotType.isNotMove()) {
                                showDistanceDialog = true
                                setShowBottomSaveCancelBar(false)
                            }
                        }
                    )
                )

                MySpacerColumn(height = 16.dp)
            }

            //memo card
            item {
                MemoCard(
                    isEditMode = isEditMode,
                    memoText = currentSpot.memo,
                    onMemoChanged = { newMemoText ->
                        currentSpot.setMemoText(showingTrip, dateId, updateTripState, newMemoText)
                    },
                    isLongText = { onErrorCountChange(it) }
                )

                MySpacerColumn(height = 16.dp)
            }

            //image card
            item {
                ImageCard(
                    tripId = showingTrip.id,
                    isEditMode = isEditMode,
                    imagePathList = currentSpot.imagePathList,
                    onAddImages = { imageFiles ->
                        addAddedImageList(imageFiles)

                        val newImgList = currentSpot.imagePathList + imageFiles

                        currentSpot.setImage(showingTrip, dateId, updateTripState, newImgList)
                    },
                    deleteImage = { imageFile ->
                        addDeletedImageList(listOf(imageFile))

                        val newImgList: MutableList<String> =
                            currentSpot.imagePathList.toMutableList()
                        newImgList.remove(imageFile)

                        currentSpot.setImage(showingTrip, dateId, updateTripState, newImgList)
                    },
                    isOverImage = { onErrorCountChange(it) },
                    reorderImageList = reorderSpotImageList
                )
            }

            //delete spot button
            item {
                //TODO animation
                if (isEditMode) {
                    var showDialog by rememberSaveable { mutableStateOf(false) }

                    if (showDialog) {
                        DeleteOrNotDialog(
                            bodyText = stringResource(id = R.string.dialog_body_delete_spot),
                            deleteText = stringResource(id = R.string.dialog_button_delete),
                            onDismissRequest = { showDialog = false },
                            onDeleteClick = {
                                showDialog = false
                                deleteSpot()
                            }
                        )
                    }

                    //delete spot
                    MySpacerColumn(height = 64.dp)

                    DeleteItemButton(
                        text = stringResource(id = R.string.delete_spot),
                        onClick = {
                        //show dialog
                        showDialog = true
                    })
                }
            }
        }
    }
}

@Composable
private fun SetLocationPage(
    showingTrip: Trip,
    spotList: List<Spot>,
    currentSpotId: Int,
    dateList: List<Date>,
    dateId: Int,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    toggleIsEditLocationMode: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val firstLocation = spotList[currentSpotId].getPrevLocation(dateList, dateId)
    var newLocation: LatLng by rememberSaveable { mutableStateOf(firstLocation) }

    var newZoomLevel: Float by rememberSaveable { mutableStateOf(spotList[currentSpotId].zoomLevel ?: DEFAULT_ZOOM_LEVEL) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, newZoomLevel)
    }

    var screenWidthDp by rememberSaveable { mutableStateOf(0) }
    val density = LocalDensity.current.density

    //edit location
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onSizeChanged {
                screenWidthDp = (it.width.toFloat() / density).toInt()
            }
    ) {
        //google map
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            MapChooseLocation(
                context = LocalContext.current,
                isDarkMapTheme = isDarkMapTheme,
                cameraPositionState = cameraPositionState,
                date = dateList[dateId],
                spot = spotList[currentSpotId],
                onLocationChange = { newLocation_ ->
                    newLocation = newLocation_
                },
                onZoomChange = {newZoomLevel_ ->
                    newZoomLevel = newZoomLevel_
                }
            )

            //center marker
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(spotList[currentSpotId].spotType.group.color.color))
            ){
                Text(
                    text = spotList[currentSpotId].iconText.toString(),
                    style = getTextStyle(TextType.PROGRESS_BAR__ICON_TEXT_HIGHLIGHT)
                        .copy(color = Color(spotList[currentSpotId].spotType.group.color.onColor))
                )
            }
        }

//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(320.dp),
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            item {
//                //zoom card
////                Box(modifier = Modifier.width(335.dp)) {
//                    ZoomCard(
//                        zoomLevel = newZoomLevel,
//                        mapZoomTo = { newZoomLevel_ ->
//                            newZoomLevel = newZoomLevel_
//                            coroutineScope.launch {
//                                cameraPositionState.animate(
//                                    CameraUpdateFactory.zoomTo(newZoomLevel), 300
//                                )
//                            }
//                        },
//                        fusedLocationClient = fusedLocationClient,
//                        cameraPositionState = cameraPositionState,
//                        setUserLocationEnabled = setUserLocationEnabled,
//                        showSnackBar = showSnackBar
//                    )
////                }
//            }
//            item {
//                //cancel save buttons
//                SaveCancelButtons(
//                    onCancelClick = toggleIsEditLocationMode,
//                    onSaveClick = {
//                        //set location and zoom level
//                        //spotList[currentSpotId].zoomLevel = zoomLevel
//                        spotList[currentSpotId].setLocation(
//                            showingTrip,
//                            dateId,
//                            updateTripState,
//                            newLocation,
//                            newZoomLevel
//                        )
//                        toggleIsEditLocationMode()
//                    },
//                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
//                    positiveText = stringResource(id = R.string.dialog_button_ok)
//                )
//            }
//        }


        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(1f)
            ) {
                ZoomCard(
                    zoomLevel = newZoomLevel,
                    mapZoomTo = { newZoomLevel1 ->
                        newZoomLevel = newZoomLevel1
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.zoomTo(newZoomLevel), 300
                            )
                        }
                    },
                    fusedLocationClient = fusedLocationClient,
                    cameraPositionState = cameraPositionState,
                    setUserLocationEnabled = setUserLocationEnabled,
                    showSnackBar = showSnackBar
                )
            }

            if (screenWidthDp >= 670){
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.weight(1f)
                ) {
                    //cancel save buttons
                    SaveCancelButtons(
                        onCancelClick = toggleIsEditLocationMode,
                        onSaveClick = {
                            //set location and zoom level
                            //spotList[currentSpotId].zoomLevel = zoomLevel
                            spotList[currentSpotId].setLocation(
                                showingTrip,
                                dateId,
                                updateTripState,
                                newLocation,
                                newZoomLevel
                            )
                            toggleIsEditLocationMode()
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        positiveText = stringResource(id = R.string.dialog_button_ok)
                    )
                }
            }
        }
        if (screenWidthDp < 670) {

            MySpacerColumn(height = 8.dp)

            //cancel save buttons
            SaveCancelButtons(
                onCancelClick = toggleIsEditLocationMode,
                onSaveClick = {
                    //set location and zoom level
                    //spotList[currentSpotId].zoomLevel = zoomLevel
                    spotList[currentSpotId].setLocation(
                        showingTrip,
                        dateId,
                        updateTripState,
                        newLocation,
                        newZoomLevel
                    )
                    toggleIsEditLocationMode()
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                positiveText = stringResource(id = R.string.dialog_button_ok)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun scrollToPage(
    coroutineScope: CoroutineScope,
    spotPagerState: PagerState,
    progressBarState: LazyListState,
    spotId: Int
){
    coroutineScope.launch {
        spotPagerState.animateScrollToPage(spotId)
        progressBarState.animateScrollToItem(spotId)
    }
}

private fun mapAnimateToSpot(
    scrollState: LazyListState,
    currentSpot: Spot,
    dateList: List<Date>,
    spotList: List<Spot>,
    dateId: Int,
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    density: Float,
    coroutineScope: CoroutineScope
){
    val spotFrom = currentSpot.getPrevSpot(dateList, spotList, dateId)
    val spotTo = currentSpot.getNextSpot(dateList, spotList, dateId)

    if (scrollState.firstVisibleItemIndex == 0) {
        //if spot is move
        if (currentSpot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null) {
            coroutineScope.launch {
                focusOnToSpot(cameraPositionState, mapSize, listOf(spotFrom, spotTo), density)
            }
        }
        //if spot is not move
        else if (currentSpot.spotType.isNotMove() && currentSpot.location != null) {
            coroutineScope.launch {
                focusOnToSpot(cameraPositionState, mapSize, listOf(currentSpot))
            }
        }
    }
}