package com.example.somewhere.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.DeleteItemButton
import com.example.somewhere.ui.screenUtils.DeleteOrNotDialog
import com.example.somewhere.ui.screenUtils.cards.ImageCard
import com.example.somewhere.ui.screenUtils.cards.InformationCard
import com.example.somewhere.ui.screenUtils.cards.MapCard
import com.example.somewhere.ui.screenUtils.MapChooseLocation
import com.example.somewhere.ui.screenUtils.cards.MemoCard
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.SetSpotTypeDialog
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.SetBudgetOrDistanceDialog
import com.example.somewhere.ui.screenUtils.SpotListProgressBar
import com.example.somewhere.ui.screenUtils.cards.DateTimeCard
import com.example.somewhere.ui.screenUtils.cards.TitleWithColorCard
import com.example.somewhere.ui.screenUtils.cards.TitleCardMove
import com.example.somewhere.ui.screenUtils.cards.ZoomCard
import com.example.somewhere.ui.screenUtils.focusOnToSpot
import com.example.somewhere.ui.screenUtils.initialZoomLevel
import com.example.somewhere.ui.screenUtils.seoulLocation
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
fun SpotDetailScreen(
    isEditMode: Boolean,
    setUseImePadding: (useImePadding: Boolean) -> Unit,

    originalTrip: Trip,
    tempTrip: Trip,
    dateId: Int,
    spotId: Int,    //fixed value do not use / use [currentSpotId]

    changeEditMode: (editMode: Boolean?) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateId: Int) -> Unit,
    deleteSpot: (dateId: Int, spotId: Int) -> Unit,
    saveTrip: () -> Unit,

    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    onImageClicked: () -> Unit,

    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
){
    val showingTrip = if (isEditMode) tempTrip
                      else            originalTrip

    val dateList = showingTrip.dateList
    val spotList = showingTrip.dateList[dateId].spotList
    var currentSpotId by rememberSaveable { mutableStateOf(spotId) }
    //var prevSpotId by rememberSaveable { mutableStateOf(currentSpotId) }

    val currentDate = dateList[dateId]
    val currentSpot = spotList[currentSpotId]

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    var isEditLocationMode by rememberSaveable { mutableStateOf(false) }

    var isMapExpand by rememberSaveable { mutableStateOf(false) }

    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }

    val scrollState = rememberLazyListState()

    val pageCount = showingTrip.dateList[dateId].spotList.size

    val spotPagerState = rememberPagerState(
        initialPage = currentSpotId
    )

    val progressBarState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentSpotId
    )

    //dialog: delete trip? unsaved data will be deleted
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    //when back button click
    val onBackButtonClick = {
        if(isEditLocationMode)
            isEditLocationMode = false

        else{
            if (originalTrip != tempTrip)
                showExitDialog = true
            else {
                changeEditMode(false)
            }
        }
    }

    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }

    //===============================================================
    var spotFrom: Spot? = null
    var spotTo: Spot? = null

    if(currentSpot.spotType.isMove()) {
        spotFrom = currentSpot.getPrevSpot(dateList, spotList, dateId)
        spotTo = currentSpot.getNextSpot(dateList, spotList, dateId)
    }

    val location =
        if (currentSpot.spotType.isNotMove())
            currentSpot.location ?: seoulLocation
        else
            spotFrom?.location ?: seoulLocation


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, currentSpot.zoomLevel ?: initialZoomLevel)
    }

    val density = LocalDensity.current.density

    //animate map to spot
    val animateToSpot = {
        //if map visible
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

    if (spotPagerState.isScrollInProgress){
        animateToSpot()
    }

    //set top bar title
    DateDestination.title =
        if (isEditLocationMode)
            stringResource(id = R.string.top_bar_title_set_location)

        else if (isEditMode)
            stringResource(id = R.string.top_bar_title_edit_spot)

        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText
        }

    val dateTitle = dateList[dateId].titleText
    val subTitle =
        if (isEditLocationMode)
            null
        else {
            if (dateTitle == null)
                dateList[dateId].getDateText(includeYear = true)
            else
                dateList[dateId].getDateText(includeYear = true) + " - " + dateTitle
        }

    Scaffold(
        //top bar
        topBar = {
            SomewhereTopAppBar(
                isEditMode = isEditMode,
                title = DateDestination.title,
                subTitle = subTitle,

                //back button
                navigationIcon = MyIcons.back,
                navigationIconOnClick = {
                    if (!isEditMode) navigateUp()
                    else             onBackButtonClick()

                },

                //edit button
                actionIcon1 =
                    if (!isEditMode) MyIcons.edit
                    else             null,
                actionIcon1Onclick = {
                    changeEditMode(null)
                    isMapExpand = false
                },

                actionIcon2 = null,
                actionIcon2Onclick = {}
            )
        }
    ) { i ->
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
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {

                MySpacerColumn(height = 8.dp)

                SpotListProgressBar(
                    initialIdx = spotId,
                    progressBarState = progressBarState,
                    isEditMode = isEditMode,

                    dateList = dateList,
                    dateId = dateId,
                    spotList = spotList,
                    currentSpotIdx = spotPagerState.currentPage,
                    addNewSpot = {
                        addNewSpot(dateId)

                        coroutineScope.launch {
                            delay(100)
                            val lastIdx = spotList.size
                            currentSpotId = lastIdx
                            spotPagerState.animateScrollToPage(lastIdx)

                            val toIdx = if (
                                    currentSpotId >= 1 &&
                                    spotList[currentSpotId - 1].spotType.isMove()
                                    && spotList[currentSpotId].spotType.isNotMove()
                                )
                                    currentSpotId -1
                                else    currentSpotId

                            progressBarState.animateScrollToItem(toIdx)
                        }
                    },
                    onClickSpot = {spotId ->
                        coroutineScope.launch {
                            spotPagerState.animateScrollToPage(spotId)
                        }
                    }
                )

                MySpacerColumn(height = 8.dp)

                var lazyColumnHeight by rememberSaveable { mutableStateOf(0) }

                //
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



//                    if (currentSpotId != prevSpotId) {
//                        Log.d("test", "--- $currentSpotId $prevSpotId")
//                        animateToSpot()
//                        prevSpotId = currentSpotId
//                    }

                    //map card
                    item {
                        MapCard(
                            isEditMode = isEditMode,
                            isMapExpand = isMapExpand,
                            expandHeight = (lazyColumnHeight / LocalDensity.current.density).toInt(),
                            mapSize = mapSize,

                            fusedLocationClient = fusedLocationClient,
                            userLocationEnabled = userLocationEnabled,
                            setUserLocationEnabled = setUserLocationEnabled,
                            cameraPositionState = cameraPositionState,

                            currentDate = currentDate,
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
                                    dateId,
                                    updateTripState,
                                    null,
                                    null
                                )
                            },
                            setMapSize = {
                                mapSize = it
                            },
                            spotFrom = spotFrom,
                            spotTo = spotTo
                        )
                    }

                    item {
                        HorizontalPager(
                            pageCount = pageCount,
                            state = spotPagerState,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.weight(1f)
                        ) { pageIndex ->     //pageIndex == spotId

                            currentSpotId = spotPagerState.currentPage

                            if (!isEditLocationMode) {
                                //each page
                                SpotDetailPage(
                                    isEditMode = isEditMode,
                                    setUseImePadding = setUseImePadding,

                                    showingTrip = showingTrip,
                                    dateId = dateId,
                                    currentDate = dateList[dateId],
                                    spotId = pageIndex,
                                    currentSpot = dateList[dateId].spotList[pageIndex],

                                    focusManager = focusManager,

                                    updateTripState = updateTripState,

                                    toggleIsEditLocation = {
                                        isEditLocationMode = !isEditLocationMode
                                    },

                                    onImageClicked = onImageClicked,
                                    deleteSpot = {
                                        if (spotList.size <= 1) {
                                            navigateUp()
                                            deleteSpot(dateId, currentSpotId)
                                        } else {
                                            var toIdx = currentSpotId - 1

                                            if (toIdx < 0) {
                                                toIdx = currentSpotId
                                            }

                                            deleteSpot(dateId, currentSpotId)

                                            coroutineScope.launch {
                                                delay(100)
                                                spotPagerState.animateScrollToPage(toIdx)
                                                progressBarState.animateScrollToItem(toIdx)
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

            //set location page
            AnimatedVisibility(
                visible = isEditLocationMode,
                enter = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }),
                exit = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it })
            ) {
                SetLocationPage(
                    showingTrip = showingTrip,
                    spotList = spotList,
                    currentSpotId = currentSpotId,
                    dateList = dateList,
                    dateId = dateId,
                    updateTripState = updateTripState,
                    toggleIsEditLocationMode = {
                        isEditLocationMode = false
                    },
                    animateToSpot = {
                        animateToSpot()
                    },
                    fusedLocationClient = fusedLocationClient,
                    setUserLocationEnabled = setUserLocationEnabled
                )
            }
        }
    }
}

@Composable
fun SpotDetailPage(
    isEditMode: Boolean,
    setUseImePadding: (useImePadding: Boolean) -> Unit,

    showingTrip: Trip,
    dateId: Int,
    currentDate: Date,
    spotId: Int,
    currentSpot: Spot,

    focusManager: FocusManager,

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
            .heightIn(300.dp, 50000.dp)
            .padding(16.dp, 0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        //userScrollEnabled = false
    ){


        //title card
        item {
            MySpacerColumn(height = 16.dp)

            if (currentSpot.spotType.isNotMove())
                TitleWithColorCard(
                    isEditMode = isEditMode,
                    titleText = currentSpot.titleText,
                    focusManager = focusManager,
                    onTitleChange = { newTitleText ->
                        currentSpot.setTitleText(showingTrip, dateId, updateTripState, newTitleText)
                    }
                )
            else {
                TitleCardMove(
                    isEditMode = isEditMode,
                    titleText = currentSpot.titleText,
                    focusManager = focusManager,
                    onTitleChange = { newTitleText ->
                        currentSpot.setTitleText(showingTrip, dateId, updateTripState, newTitleText)
                    }
                )

                MySpacerColumn(height = 16.dp)
            }
        }

        //image card
        item {
            val toastText = stringResource(id = R.string.toast_image_limit)

            ImageCard(
                isEditMode = isEditMode,
                imgList = currentSpot.imgPathList,
                onAddImages = {
                    var addImageList: List<String> = it

                    //up to 10 images
                    if (currentSpot.imgPathList.size + it.size > 10) {
                        addImageList = it.subList(0, 10 - currentSpot.imgPathList.size)
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    }

                    val newImgList = currentSpot.imgPathList + addImageList

                    currentSpot.setImage(showingTrip, dateId, updateTripState, newImgList)
                },
                deleteImage = {
                    val newImgList: MutableList<String> =
                        currentSpot.imgPathList.toMutableList()
                    newImgList.remove(it)

                    currentSpot.setImage(showingTrip, dateId, updateTripState, newImgList)
                }
            )
        }

        //date time card
        item {
            DateTimeCard(
                dateList = dateList,
                date = currentDate,
                spot = currentSpot,
                isEditMode = isEditMode,
                changeDate = { newDateId ->
                    if (dateId != newDateId) {
                        navigateUp()
                        showingTrip.moveSpotToDate(showingTrip, dateId, spotId, newDateId, updateTripState)
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
                    onDismissRequest = { showSpotTypeDialog = false },
                    onOkClick = { newSpotType ->
                        showSpotTypeDialog = false
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
                        setImePadding()
                    },
                    onSaveClick = { newBudget ->
                        showBudgetDialog = false
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
                        setImePadding()
                    },
                    onSaveClick = { newTravelDistance ->
                        showDistanceDialog = false
                        currentSpot.travelDistance = newTravelDistance
                        setImePadding()
                    }
                )
            }

            if (currentSpot.spotType.isMove()) {
                currentSpot.updateDistance(
                    currentSpot.getPrevSpot(dateList, spotList, dateId)?.location,
                    currentSpot.getNextSpot(dateList, spotList, dateId)?.location
                )
            }

            InformationCard(
                isEditMode = isEditMode,
                spotTypeColor = currentSpot.spotType.group.color.color,
                list = listOf(
                    Triple(MyIcons.category, currentSpot.getSpotTypeText()) {
                        showSpotTypeDialog = true
                    },

                    Triple(MyIcons.budget, currentSpot.getBudgetText(showingTrip)) {
                        showBudgetDialog = true
                    },

                    Triple(MyIcons.travelDistance, currentSpot.getDistanceText()) {
                        if (currentSpot.spotType.isNotMove())
                            showDistanceDialog = true
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
                }
            )

            MySpacerColumn(height = 16.dp)
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

                DeleteItemButton(text = "Delete spot", onClick = {
                    //show dialog
                    showDialog = true
                })
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

    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    toggleIsEditLocationMode: () -> Unit,
    animateToSpot: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val firstLocation = spotList[currentSpotId].location?: seoulLocation
    var newLocation: LatLng by rememberSaveable { mutableStateOf(firstLocation) }

    var newZoomLevel: Float by rememberSaveable { mutableStateOf(spotList[currentSpotId].zoomLevel ?: initialZoomLevel) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, newZoomLevel)
    }

    //edit location
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
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
                    text = spotList[currentSpotId].orderId.toString(),
                    style = getTextStyle(TextType.PROGRESS_BAR__TEXT_HIGHLIGHT)
                        .copy(color = Color(spotList[currentSpotId].spotType.group.color.onColor))
                )
            }
        }


        //zoom card
        ZoomCard(
            zoomLevel = newZoomLevel,
            mapZoomTo = {newZoomLevel_ ->
                newZoomLevel = newZoomLevel_
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.zoomTo(newZoomLevel), 300
                    )
                }
            },
            fusedLocationClient = fusedLocationClient,
            cameraPositionState = cameraPositionState,
            setUserLocationEnabled = setUserLocationEnabled
        )

        MySpacerColumn(height = 10.dp)

        //cancel save buttons
        BottomSaveCancelBar(
            onCancelClick = toggleIsEditLocationMode ,
            onSaveClick = {
                //set location and zoom level
                //spotList[currentSpotId].zoomLevel = zoomLevel
                spotList[currentSpotId].setLocation(showingTrip, dateId, updateTripState, newLocation, newZoomLevel)
                toggleIsEditLocationMode()

                animateToSpot()
            },
            modifier = Modifier.background(MaterialTheme.colors.background)
        )
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
