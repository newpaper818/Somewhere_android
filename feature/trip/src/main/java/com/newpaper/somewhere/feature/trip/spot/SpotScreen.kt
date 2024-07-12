package com.newpaper.somewhere.feature.trip.spot

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.tripScreenUtils.SpotListProgressBar
import com.newpaper.somewhere.core.utils.DEFAULT_ZOOM_LEVEL
import com.newpaper.somewhere.core.utils.convert.SEOUL_LOCATION
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getNextSpot
import com.newpaper.somewhere.core.utils.convert.getPrevSpot
import com.newpaper.somewhere.core.utils.convert.moveSpotToDate
import com.newpaper.somewhere.core.utils.convert.setBudget
import com.newpaper.somewhere.core.utils.convert.setEndTime
import com.newpaper.somewhere.core.utils.convert.setLocationAndUpdateTravelDistance
import com.newpaper.somewhere.core.utils.convert.setSpotType
import com.newpaper.somewhere.core.utils.convert.setStartTime
import com.newpaper.somewhere.core.utils.convert.setTravelDistance
import com.newpaper.somewhere.core.utils.focusOnToSpots
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.dialog.memo.MemoDialog
import com.newpaper.somewhere.feature.dialog.selectDate.SelectDateDialog
import com.newpaper.somewhere.feature.dialog.setBudgetOrDiatance.SetBudgetOrDistanceDialog
import com.newpaper.somewhere.feature.dialog.setLocation.SetLocationDialog
import com.newpaper.somewhere.feature.dialog.setSpotType.SetSpotTypeDialog
import com.newpaper.somewhere.feature.dialog.setTime.SetTimeDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.spot.component.NoSpotCard
import com.newpaper.somewhere.feature.trip.spot.component.SpotDetailPage
import com.newpaper.somewhere.feature.trip.spot.component.SpotMapCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    appUserId: String,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    commonTripViewModel: CommonTripViewModel,
    isCompactWidth: Boolean,
    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,

    //navigate
    navigateUp: () -> Unit,
    navigateTo: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,

    //
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    //
    modifier: Modifier = Modifier,
    spotViewModel: SpotViewModel = hiltViewModel()
){

    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()
    val spotUiState by spotViewModel.spotUiState.collectAsState()

    val originalTrip = commonTripUiState.tripInfo.trip!!
    val tempTrip = commonTripUiState.tripInfo.tempTrip!!
    val isEditMode = commonTripUiState.isEditMode

    val showingTrip = if (commonTripUiState.isEditMode) tempTrip
                        else            originalTrip

    val currentDateIndex = commonTripUiState.tripInfo.dateIndex ?: 0
    val currentSpotIndex = commonTripUiState.tripInfo.spotIndex ?: 0

    var userSwiping by rememberSaveable { mutableStateOf(true) }

    val dateList = showingTrip.dateList
    val spotList = dateList[currentDateIndex].spotList
    val currentSpot = spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()


    val scrollState = rememberLazyListState()

    val spotPagerState = rememberPagerState(
        initialPage = currentSpotIndex,
        pageCount = { spotList.size }
    )

    val progressBarState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentSpotIndex
    )


    //===============================================================
    var spotFrom: Spot? = null
    var spotTo: Spot? = null

    if(currentSpot?.spotType?.isMove() == true) {
        spotFrom = currentSpot.getPrevSpot(dateList, currentDateIndex)
        spotTo = currentSpot.getNextSpot(dateList, currentDateIndex)
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

    //if user swipe page                -> animate progress bar. map
    //if user click progress bar's spot -> animate progress bar, map, pager
    //if user click next, prev button   -> animate progress bar, map, pager

    val userDragTouching by spotPagerState.interactionSource.collectIsDraggedAsState()









    //when back button click
    val onClickBackButton = {
        if (!isEditMode) navigateUp()
        else {
            if (spotUiState.showSetLocationDialog)
                spotViewModel.setShowSetLocationDialog(false)
            else {
                if (originalTrip != tempTrip)
                    spotViewModel.setShowExitDialog(true)
                else
                    commonTripViewModel.setIsEditMode(false)
            }
        }
    }

    BackHandler {
        onClickBackButton()
    }






    LaunchedEffect(isEditMode){
        if (isEditMode)
            spotViewModel.setIsMapExpanded(false)
        else
            spotViewModel.initAllErrorCount()

    }

    LaunchedEffect(userDragTouching){
        if (userDragTouching) userSwiping = true
    }

    //when user swipe page
    LaunchedEffect(spotPagerState.currentPage){
        if (userSwiping) {
            val newSpotIndex = spotPagerState.currentPage

            //update spot index
            commonTripViewModel.setCurrentSpotIndex(newSpotIndex)

            //animate progress bar
            coroutineScope.launch {
                val toIdx = if (newSpotIndex == 0) 0
                else newSpotIndex + 1
                progressBarState.animateScrollToItem(toIdx)
            }

            //animate map spot
            if (newSpotIndex < spotList.size)
                coroutineScope.launch {
                    mapAnimateToSpot(
                        scrollState,
                        spotList[newSpotIndex],
                        dateList,
                        spotList,
                        currentDateIndex,
                        cameraPositionState,
                        spotUiState.mapSize,
                        density,
                        coroutineScope
                    )
                }
        }
    }

    //when user click spot on progress bar or click prev/next spot button
    LaunchedEffect(currentDateIndex, currentSpotIndex){
        if (!userSwiping) {

            //animate progress bar
            coroutineScope.launch {
                val toIdx = if (currentSpotIndex == 0) 0
                else currentSpotIndex + 1
                progressBarState.animateScrollToItem(toIdx)
            }

            //animate pager
            coroutineScope.launch {
                spotPagerState.animateScrollToPage(currentSpotIndex)
            }

            //animate map spot
            if (currentSpotIndex < spotList.size && currentSpotIndex >= 0)
                coroutineScope.launch {
                    mapAnimateToSpot(
                        scrollState,
                        spotList[currentSpotIndex],
                        dateList,
                        spotList,
                        currentDateIndex,
                        cameraPositionState,
                        spotUiState.mapSize,
                        density,
                        coroutineScope
                    )
                }
        }
    }


    SpotScreen(
        spotUiInfo = SpotUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            isDarkMapTheme = isDarkMapTheme,
            userSwiping = userSwiping,
            _setUserSwiping = { userSwiping = it },
            isEditMode = isEditMode,
            _setIsEditMode = commonTripViewModel::setIsEditMode
        ),
        spotData = SpotData(
            originalTrip = originalTrip,
            tempTrip = tempTrip,
            currentDateIndex = currentDateIndex,
            currentSpotIndex = currentSpotIndex,
            _setCurrentDateIndex = commonTripViewModel::setCurrentDateIndex,
            _setCurrentSpotIndex = commonTripViewModel::setCurrentSpotIndex
        ),
        spotState = SpotState(
            scrollState = scrollState,
            progressBarState = progressBarState,
            spotPagerState = spotPagerState
        ),
        spotMap = SpotMap(
            fusedLocationClient = fusedLocationClient,
            cameraPositionState = cameraPositionState,

            mapSize = spotUiState.mapSize,
            userLocationEnabled = userLocationEnabled,
            isMapExpand = spotUiState.isMapExpanded,
            _setMapSize = spotViewModel::setMapSize,
            _setUserLocationEnabled = setUserLocationEnabled,
            _setIsMapExpanded = spotViewModel::setIsMapExpanded,

            spotFrom = spotFrom,
            spotTo = spotTo
        ),
        spotErrorCount = SpotErrorCount(
            totalErrorCount = spotUiState.totalErrorCount,
            _increaseTotalErrorCount = spotViewModel::increaseTotalErrorCount,
            _decreaseTotalErrorCount = spotViewModel::decreaseSpotTitleErrorCount
        ),
        spotDialog = SpotDialog(
            isShowingDialog = spotUiState.isShowingDialog,
            showMemoDialog = spotUiState.showMemoDialog,
            showExitDialog = spotUiState.showExitDialog,
            showMoveDateDialog = spotUiState.showMoveDateDialog,
            showSetTimeDialog = spotUiState.showSetTimeDialog,
            showSetSpotTypeDialog = spotUiState.showSetSpotTypeDialog,
            showSetBudgetDialog = spotUiState.showSetBudgetDialog,
            showSetDistanceDialog = spotUiState.showSetDistanceDialog,
            showSetLocationDialog = spotUiState.showSetLocationDialog,
            showDeleteSpotDialog = spotUiState.showDeleteSpotDialog,

            _setShowMemoDialog = spotViewModel::setShowMemoDialog,
            _setShowExitDialog = spotViewModel::setShowExitDialog,
            _setShowMoveDateDialog = spotViewModel::setShowMoveDateDialog,
            _setShowSetTimeDialog = spotViewModel::setShowSetTimeDialog,
            _setShowSetSpotTypeDialog = spotViewModel::setShowSetSpotTypeDialog,
            _setShowSetBudgetDialog = spotViewModel::setShowSetBudgetDialog,
            _setShowSetDistanceDialog = spotViewModel::setShowSetDistanceDialog,
            _setShowSetLocationDialog = spotViewModel::setShowSetLocationDialog,
            _setDeleteSpotDialog = spotViewModel::setShowDeleteSpotDialog,

            isStartTime = spotUiState.isStartTime,
            _setIsStartTime = spotViewModel::setIsStartTime
        ),
        spotNavigate = SpotNavigate(
            _onClickBackButton = onClickBackButton,
            _navigateToImage = navigateToImage
        ),
        spotImage = SpotImage(
            _saveImageToInternalStorage = { index, uri ->
                commonTripViewModel.saveImageToInternalStorage(originalTrip.id, index, uri)
            },
            _downloadImage = commonTripViewModel::getImage,
            _addAddedImages = commonTripViewModel::addAddedImages,
            _addDeletedImages = commonTripViewModel::addDeletedImages,
            _organizeAddedDeletedImages = { isClickSave ->
                commonTripViewModel.organizeAddedDeletedImages(
                    tripManagerId = tempTrip.managerId,
                    isClickSave = isClickSave,
                    isInTripsScreen = true
                )
            },
            _reorderSpotImageList = { currentIndex, destinationIndex ->
                spotViewModel.reorderSpotImageList(
                    currentIndex = currentIndex,
                    destinationIndex = destinationIndex,
                    dateIndex = currentDateIndex,
                    spotIndex = currentSpotIndex
                )
            }
        ),
        appUserId = appUserId,
        isCompactWidth = isCompactWidth,
        updateTripState = commonTripViewModel::updateTripState,
        addNewSpot = commonTripViewModel::addNewSpot,
        deleteSpot = commonTripViewModel::deleteSpot,
        onClickSave = {
            coroutineScope.launch {
                if (originalTrip != tempTrip) {
                    //save tripUiState trip
                    commonTripViewModel.saveTrip(appUserId = appUserId)

                    commonTripViewModel.setIsEditMode(false)

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(trip = tempTrip)
                }
                else
                    commonTripViewModel.setIsEditMode(false)

                commonTripViewModel.organizeAddedDeletedImages(
                    tripManagerId = tempTrip.managerId,
                    isClickSave = true
                )
            }
        },
        modifier = modifier
    )
}






@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotScreen(
    spotUiInfo: SpotUiInfo,
    spotData: SpotData,
    spotState: SpotState,
    spotMap: SpotMap,
    spotErrorCount: SpotErrorCount,
    spotDialog: SpotDialog,
    spotNavigate: SpotNavigate,
    spotImage: SpotImage,

    appUserId: String,
    isCompactWidth: Boolean,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    onClickSave: () -> Unit,

    modifier: Modifier = Modifier
){
    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val dateList = showingTrip.dateList
    val spotList = dateList[currentDateIndex].spotList

    val currentDate = dateList.getOrNull(currentDateIndex)
    val currentSpot = spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current


    val snackBarHostState = remember { SnackbarHostState() }







    //set top bar title
    val topBarTitle =
        if (spotDialog.showSetLocationDialog)
            stringResource(id = R.string.set_location)

        else if (spotUiInfo.isEditMode)
            stringResource(id = R.string.edit_spot)

        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText!!
        }

    val dateTitle = dateList[currentDateIndex].titleText
    val subTitle =
        if (spotDialog.showSetLocationDialog)
            null
        else {
            if (dateTitle == null)
                dateList[currentDateIndex].getDateText(spotUiInfo.dateTimeFormat, includeYear = true)
            else
                stringResource(id = R.string.sub_title, dateList[currentDateIndex].getDateText(spotUiInfo.dateTimeFormat, includeYear = true), dateTitle)
        }

    val snackBarPadding by animateFloatAsState(
        targetValue = if (spotMap.isMapExpand) 75f
                        else if (spotUiInfo.isEditMode && !spotDialog.showSetLocationDialog) 56f
                        else if (spotDialog.showSetLocationDialog) {
                            if (LocalConfiguration.current.screenWidthDp > 670)
                                90f
                            else 150f
                        }
                        else 0f,
        animationSpec = tween(300),
        label = "snackbar padding"
    )









    MyScaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .displayCutoutPadding(),
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
                useHorizontalLayoutTitles = spotUiInfo.use2Panes,
                title = topBarTitle,
                subtitle = subTitle,
                internetEnabled = spotUiInfo.internetEnabled,

                //back button
                navigationIcon =
                    if (spotDialog.showSetLocationDialog) TopAppBarIcon.close
                    else                    TopAppBarIcon.back,
                onClickNavigationIcon = spotNavigate::onClickBackButton,

                actionIcon1 = if (!spotUiInfo.isEditMode && showingTrip.editable) TopAppBarIcon.edit
                                else null,
                actionIcon1Onclick = {
                    if (spotMap.isMapExpand)
                        spotMap.setIsMapExpanded(false)
                    spotUiInfo.setIsEditMode(true)
                }
            )
        },

        //bottom save cancel bar
        bottomSaveCancelBarVisible = spotUiInfo.isEditMode && !spotDialog.isShowingDialog,
        use2PanesAndSpotScreen = spotUiInfo.use2Panes,
        onClickCancel = {
            focusManager.clearFocus()
            spotNavigate.onClickBackButton()
        },
        onClickSave = onClickSave,
        saveEnabled = spotErrorCount.totalErrorCount <= 0

    ) { paddingValues ->

        //dialogs
        if(spotDialog.showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { spotDialog.setShowExitDialog(false) },
                onClickDelete = {
                    spotDialog.setShowExitDialog(false)
                    spotUiInfo.setIsEditMode(false)
                    updateTripState(true, spotData.originalTrip)

                    spotImage.organizeAddedDeletedImages(false)
                }
            )
        }

        if (spotDialog.showMemoDialog){
            MemoDialog(
                memoText = currentSpot?.memo ?: "",
                onDismissRequest = { spotDialog.setShowMemoDialog(false) }
            )
        }

        if (spotDialog.showMoveDateDialog && dateList.isNotEmpty() && currentDate != null){
            SelectDateDialog(
                dateTimeFormat = spotUiInfo.dateTimeFormat,
                initialDate = currentDate,
                dateList = dateList,
                onOkClick = { newDateIndex ->
                    if (newDateIndex != currentDateIndex){
                        spotDialog.setShowMoveDateDialog(false)
                        showingTrip.moveSpotToDate(showingTrip, currentDateIndex, currentSpotIndex, newDateIndex, updateTripState)
                        spotData.setCurrentDateIndex(newDateIndex)
                        val spotIndex = dateList[newDateIndex].spotList.lastIndex + 1
                        spotData.setCurrentSpotIndex(spotIndex)
                        coroutineScope.launch {
                            delay(100)
                            spotState.spotPagerState.animateScrollToPage(spotIndex)
                            spotState.progressBarState.animateScrollToItem(spotIndex + 1)
                        }
                    }
                    else {
                        spotDialog.setShowMoveDateDialog(false)
                    }
                },
                onDismissRequest = { spotDialog.setShowMoveDateDialog(false) }
            )
        }

        if (spotDialog.showSetTimeDialog && currentSpot != null){
            val initialTime = if  (spotDialog.isStartTime) currentSpot.startTime ?: LocalTime.of(12,0)
                                else        currentSpot.endTime ?: LocalTime.of(12,0)

            SetTimeDialog(
                initialTime = initialTime,
                timeFormat = spotUiInfo.dateTimeFormat.timeFormat,
                isSetStartTime = spotDialog.isStartTime,
                onDismissRequest = { spotDialog.setShowSetTimeDialog(false) },
                onConfirm = { newTime ->
                    spotDialog.setShowSetTimeDialog(false)
                    if (spotDialog.isStartTime) currentSpot.setStartTime(showingTrip, currentDateIndex, updateTripState, newTime)
                    else                        currentSpot.setEndTime(showingTrip, currentDateIndex, updateTripState, newTime)
                }
            )
        }

        if (spotDialog.showSetSpotTypeDialog && currentSpot != null){
            SetSpotTypeDialog(
                initialSpotType = currentSpot.spotType,
                onDismissRequest = { spotDialog.setShowSetSpotTypeDialog(false) },
                onClickOk = { newSpotType ->
                    spotDialog.setShowSetSpotTypeDialog(false)
                    currentSpot.setSpotType(showingTrip, dateList, currentDateIndex, updateTripState, newSpotType)
                }
            )
        }

        if (spotDialog.showSetBudgetDialog && currentSpot != null){
            SetBudgetOrDistanceDialog(
                currencyType = showingTrip.unitOfCurrencyType,
                initialValue = currentSpot.budget,
                onDismissRequest = {
                    focusManager.clearFocus()
                    spotDialog.setShowSetBudgetDialog(false)
                },
                onSaveClick = { newBudget ->
                    focusManager.clearFocus()
                    spotDialog.setShowSetBudgetDialog(false)
                    currentSpot.setBudget(showingTrip, currentDateIndex, updateTripState, newBudget)
                }
            )
        }

        if (spotDialog.showSetDistanceDialog && currentSpot != null){
            SetBudgetOrDistanceDialog(
                initialValue = currentSpot.travelDistance,
                onDismissRequest = {
                    focusManager.clearFocus()
                    spotDialog.setShowSetDistanceDialog(false)
                },
                onSaveClick = { newTravelDistance ->
                    focusManager.clearFocus()
                    spotDialog.setShowSetDistanceDialog(false)
                    currentSpot.setTravelDistance(showingTrip, currentDateIndex, updateTripState, newTravelDistance)
                }
            )
        }

        if (spotDialog.showDeleteSpotDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_delete_spot),
                deleteButtonText = stringResource(id = R.string.dialog_button_delete),
                onDismissRequest = { spotDialog.setShowDeleteSpotDialog(false) },
                onClickDelete = {
                    deleteSpot(currentDateIndex, currentSpotIndex)
                    spotDialog.setShowDeleteSpotDialog(false)
                }
            )
        }





        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {

            //set location dialog(full screen dialog)
            AnimatedVisibility(
                visible = spotDialog.showSetLocationDialog,
                enter = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it }),
                exit = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it }),
                modifier = Modifier.zIndex(1f)
            ) {
            SetLocationDialog(
                internetEnabled = spotUiInfo.internetEnabled,
                showingTrip = showingTrip,
                dateList = dateList,
                spotList = spotList,
                dateIndex = currentDateIndex,
                spotIndex = currentSpotIndex,
                isDarkMapTheme = spotUiInfo.isDarkMapTheme,
                updateTripState = updateTripState,
                setShowSetLocationDialogToFalse = { spotDialog.setShowSetLocationDialog(false) },
                fusedLocationClient = spotMap.fusedLocationClient,
                setUserLocationEnabled = spotMap::setUserLocationEnabled,
                showSnackBar = { text, actionLabel, duration, onActionClick ->
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = text,
                            actionLabel = actionLabel,
                            duration = duration
                        ).run {
                            when (this){
                                SnackbarResult.Dismissed -> { }
                                SnackbarResult.ActionPerformed -> onActionClick()
                            }
                        }
                    }
                }
            )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                MySpacerColumn(height = 8.dp)

                //progress bar
                SpotListProgressBar(
                    useLargeItemWidth = !isCompactWidth,
                    spacerValue = spotUiInfo.spacerValue,
                    progressBarState = spotState.progressBarState,
                    isEditMode = spotUiInfo.isEditMode,

                    dateTimeFormat = spotUiInfo.dateTimeFormat,
                    dateList = dateList,
                    dateIndex = currentDateIndex,
                    spotList = spotList,
                    currentSpotIndex = currentSpotIndex,
                    addNewSpot = {
                        coroutineScope.launch {
                            dateList[spotData.currentDateIndex].id
                            addNewSpot(currentDateIndex)
                            delay(70)
                            val lastIdx = spotList.size
                            spotData.setCurrentSpotIndex(lastIdx)
                            spotState.spotPagerState.animateScrollToPage(lastIdx)
                        }
                    },
                    onClickSpot = {toSpotIndex ->
                        spotUiInfo.setUserSwiping(false)
                        spotData.setCurrentSpotIndex(toSpotIndex)
                    },
                    onPrevDateClick = {toDateIndex ->
                        spotUiInfo.setUserSwiping(false)
                        spotData.setCurrentDateIndex(toDateIndex)
                        spotData.setCurrentSpotIndex(dateList[toDateIndex].spotList.lastIndex)
                    },
                    onNextDateClick = {toDateIndex ->
                        spotUiInfo.setUserSwiping(false)
                        spotData.setCurrentDateIndex(toDateIndex)
                        spotData.setCurrentSpotIndex(0)
                    }
                )

                MySpacerColumn(height = 16.dp)

                if (!spotUiInfo.use2Panes){
                    Spot1Pane(
                        spotUiInfo = spotUiInfo,
                        spotData = spotData,
                        spotState = spotState,
                        spotMap = spotMap,
                        errorCount = spotErrorCount,
                        dialog = spotDialog,
                        navigate = spotNavigate,
                        image = spotImage,
                        spotFrom = spotMap.spotFrom,
                        spotTo = spotMap.spotTo,
                        cameraPositionState = spotMap.cameraPositionState,
                        snackBarHostState = snackBarHostState,
                        focusManager = focusManager,
                        updateTripState = updateTripState,
                        setUserSwiping = spotUiInfo::setUserSwiping,
                        deleteTime = { }

                    )
                }
                else{
                    MySpacerColumn(height = 8.dp)
                    Spot2Panes(
                        spotUiInfo = spotUiInfo,
                        spotData = spotData,
                        spotState = spotState,
                        spotMap = spotMap,
                        errorCount = spotErrorCount,
                        dialog = spotDialog,
                        navigate = spotNavigate,
                        image = spotImage,
                        spotFrom = spotMap.spotFrom,
                        spotTo = spotMap.spotTo,
                        cameraPositionState = spotMap.cameraPositionState,
                        snackBarHostState = snackBarHostState,
                        focusManager = focusManager,
                        updateTripState = updateTripState,
                        setUserSwiping = spotUiInfo::setUserSwiping,
                        deleteTime = {  }
                    )
                }
            }
        }
    }
}





@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Spot1Pane(
    spotUiInfo: SpotUiInfo,
    spotData: SpotData,
    spotState: SpotState,
    spotMap: SpotMap,
    errorCount: SpotErrorCount,
    dialog: SpotDialog,
    navigate: SpotNavigate,
    image: SpotImage,

    spotFrom: Spot?,
    spotTo: Spot?,

    cameraPositionState: CameraPositionState,
    snackBarHostState: SnackbarHostState,
    focusManager: FocusManager,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    setUserSwiping: (userSwiping: Boolean) -> Unit,
    deleteTime: (isStartTime: Boolean) -> Unit,
){
    var lazyColumnHeight by rememberSaveable { mutableIntStateOf(0) }

    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val dateList = showingTrip.dateList
    val spotList = dateList[currentDateIndex].spotList

    val currentDate = dateList[currentDateIndex]
    val currentSpot = currentDate.spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()

    //map + spot page
    LazyColumn(
        state = spotState.scrollState,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                lazyColumnHeight = it.size.height
            },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = !spotMap.isMapExpand
    ) {

        item {
            //map
            SpotMapCard(
                isEditMode = spotUiInfo.isEditMode,
                isMapExpand = spotMap.isMapExpand,
                expandHeight = (lazyColumnHeight / LocalDensity.current.density).toInt() + 1,
                mapSize = spotMap.mapSize,

                isDarkMapTheme = spotUiInfo.isDarkMapTheme,
                fusedLocationClient = spotMap.fusedLocationClient,
                userLocationEnabled = spotMap.userLocationEnabled,
                setUserLocationEnabled = spotMap::setUserLocationEnabled,
                cameraPositionState = cameraPositionState,

                dateList = dateList,
                dateIndex = currentDateIndex,
                spotList = spotList,
                currentSpot = currentSpot,
                onFullScreenClicked = {
                    spotMap.setIsMapExpanded(!spotMap.isMapExpand)

                    //scroll to top(map)
                    if (spotMap.isMapExpand)
                        coroutineScope.launch {
                            spotState.scrollState.animateScrollToItem(0)
                        }
                },

                toPrevSpot = {
                    setUserSwiping(false)
                    if (currentSpotIndex > 0) {
                        spotData.setCurrentSpotIndex(currentSpotIndex - 1)
                    } else {
                        //first date and first spot will not be here
                        spotData.setCurrentDateIndex(currentDateIndex - 1)
                        spotData.setCurrentSpotIndex(dateList[currentDateIndex - 1].spotList.lastIndex)
                    }
                },
                toNextSpot = {
                    setUserSwiping(false)
                    if (currentSpotIndex < spotList.lastIndex) {
                        spotData.setCurrentSpotIndex(currentSpotIndex + 1)
                    } else {
                        spotData.setCurrentDateIndex(currentDateIndex + 1)
                        spotData.setCurrentSpotIndex(0)
                    }
                },

                toggleIsEditLocation = {
                    dialog.setShowSetLocationDialog(!dialog.showSetLocationDialog)
                },
                deleteLocation = {
                    currentSpot?.setLocationAndUpdateTravelDistance(
                        showingTrip, currentDateIndex, updateTripState, null, null
                    )
                },
                setMapSize = {
                    spotMap.setMapSize(it)
                },
                showSnackBar = { text, actionLabel, duration, onActionClick ->
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = text,
                            actionLabel = actionLabel,
                            duration = duration
                        ).run {
                            when (this) {
                                SnackbarResult.Dismissed -> {}
                                SnackbarResult.ActionPerformed -> onActionClick()
                            }
                        }
                    }
                },
                spotFrom = spotFrom,
                spotTo = spotTo
            )


            if (spotList.isEmpty()) {
                NoSpotCard(modifier = Modifier.height(240.dp))
            }
            else {
                //each spot page
                //each page
                //pageIndex == spotIndex
                HorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    state = spotState.spotPagerState,
                    pageContent = { pageIndex ->
                        //pageIndex == spotIndex

                        if (!dialog.showSetLocationDialog) {
                            //each page
                            SpotDetailPage(
                                spotUiInfo = spotUiInfo,
                                spotData = spotData.copy(currentSpotIndex = pageIndex),
                                errorCount = errorCount,
                                dialog = dialog,
                                navigate = navigate,
                                image = image,
                                focusManager = focusManager,
                                deleteTime = deleteTime,
                                updateTripState = updateTripState
                            )
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Spot2Panes(
    spotUiInfo: SpotUiInfo,
    spotData: SpotData,
    spotState: SpotState,
    spotMap: SpotMap,
    errorCount: SpotErrorCount,
    dialog: SpotDialog,
    navigate: SpotNavigate,
    image: SpotImage,

    spotFrom: Spot?,
    spotTo: Spot?,

    cameraPositionState: CameraPositionState,
    snackBarHostState: SnackbarHostState,
    focusManager: FocusManager,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    setUserSwiping: (userSwiping: Boolean) -> Unit,
    deleteTime: (isStartTime: Boolean) -> Unit,
){
    var lazyColumnHeight by rememberSaveable { mutableIntStateOf(0) }

    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val dateList = showingTrip.dateList
    val spotList = dateList[currentDateIndex].spotList

    val currentDate = dateList[currentDateIndex]
    val currentSpot = currentDate.spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()

    Row {
        MyCard(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp, bottom = 24.dp)
        ) {
            SpotMapCard(
                use2Panes = true,
                isEditMode = spotUiInfo.isEditMode,
                isMapExpand = true,
                expandHeight = (lazyColumnHeight / LocalDensity.current.density).toInt() - 1,
                mapSize = spotMap.mapSize,

                isDarkMapTheme = spotUiInfo.isDarkMapTheme,
                fusedLocationClient = spotMap.fusedLocationClient,
                userLocationEnabled = spotMap.userLocationEnabled,
                setUserLocationEnabled = spotMap::setUserLocationEnabled,
                cameraPositionState = cameraPositionState,

                dateList = dateList,
                dateIndex = currentDateIndex,
                spotList = spotList,
                currentSpot = currentSpot,
                onFullScreenClicked = {
                    spotMap.setIsMapExpanded(!spotMap.isMapExpand)

                    //scroll to top(map)
                    if (spotMap.isMapExpand)
                        coroutineScope.launch {
                            spotState.scrollState.animateScrollToItem(0)
                        }
                },

                toPrevSpot = {
                    setUserSwiping(false)
                    if (currentSpotIndex > 0) {
                        spotData.setCurrentSpotIndex(currentSpotIndex - 1)
                    } else {
                        //first date and first spot will not be here
                        spotData.setCurrentDateIndex(currentDateIndex - 1)
                        spotData.setCurrentSpotIndex(dateList[currentDateIndex - 1].spotList.lastIndex)
                    }
                },
                toNextSpot = {
                    setUserSwiping(false)
                    if (currentSpotIndex < spotList.lastIndex) {
                        spotData.setCurrentSpotIndex(currentSpotIndex + 1)
                    } else {
                        spotData.setCurrentDateIndex(currentDateIndex + 1)
                        spotData.setCurrentSpotIndex(0)
                    }
                },

                toggleIsEditLocation = {
                    dialog.setShowSetLocationDialog(!dialog.showSetLocationDialog)
                },
                deleteLocation = {
                    currentSpot?.setLocationAndUpdateTravelDistance(
                        showingTrip, currentDateIndex, updateTripState, null, null
                    )
                },
                setMapSize = {
                    spotMap.setMapSize(it)
                },
                showSnackBar = { text, actionLabel, duration, onActionClick ->
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            message = text,
                            actionLabel = actionLabel,
                            duration = duration
                        ).run {
                            when (this){
                                SnackbarResult.Dismissed -> { }
                                SnackbarResult.ActionPerformed -> onActionClick()
                            }
                        }
                    }
                },
                spotFrom = spotFrom,
                spotTo = spotTo
            )
        }

        if (spotList.isEmpty()){

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                NoSpotCard()
            }
        }
        else {
            LazyColumn(
                state = spotState.scrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .onGloballyPositioned {
                        lazyColumnHeight = it.size.height
                    }
                    .imePadding(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                userScrollEnabled = !spotMap.isMapExpand
            ) {
                //each spot page
                item {
                    //each page
                    //pageIndex == spotIndex

                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Top,
                        state = spotState.spotPagerState,
                        pageContent = {pageIndex ->
                            //pageIndex == spotIndex

                            if (!dialog.showSetLocationDialog) {
                                //each page
                                SpotDetailPage(
                                    spotUiInfo = spotUiInfo,
                                    spotData = spotData.copy(currentSpotIndex = pageIndex),
                                    errorCount = errorCount,
                                    dialog = dialog,
                                    navigate = navigate,
                                    image = image,
                                    focusManager = focusManager,
                                    deleteTime = deleteTime,
                                    updateTripState = updateTripState,
                                    minHeight = (lazyColumnHeight / LocalDensity.current.density).toInt().dp
                                )
                            }
                        }
                    )
                }
            }
        }
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
    val spotFrom = currentSpot.getPrevSpot(dateList, dateId)
    val spotTo = currentSpot.getNextSpot(dateList, dateId)

    if (scrollState.firstVisibleItemIndex == 0) {
        //if spot is move
        if (currentSpot.spotType.isMove() && spotFrom?.location != null && spotTo?.location != null) {
            coroutineScope.launch {
                focusOnToSpots(cameraPositionState, mapSize, listOf(spotFrom, spotTo), density)
            }
        }
        //if spot is not move
        else if (currentSpot.spotType.isNotMove() && currentSpot.location != null) {
            coroutineScope.launch {
                focusOnToSpots(cameraPositionState, mapSize, listOf(currentSpot))
            }
        }
    }
}