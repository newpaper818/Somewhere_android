package com.newpaper.somewhere.feature.trip.spot

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
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
import com.newpaper.somewhere.core.utils.fitBoundsToMarkers
import com.newpaper.somewhere.core.utils.getTalkbackDateText
import com.newpaper.somewhere.feature.dialog.deleteOrNot.TwoButtonsDialog
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotRoute(
    isDarkAppTheme: Boolean,
    use2Panes: Boolean,
    spacerValue: Dp,
    useBlurEffect: Boolean,
    appUserId: String,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,
    isErrorExitOnTripScreen: Boolean, //TODO

    commonTripViewModel: CommonTripViewModel,
    isCompactWidth: Boolean,
    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,

    //navigate
    navigateUp: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,

    //
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    //
    modifier: Modifier = Modifier,
    spotViewModel: SpotViewModel = hiltViewModel()
){

    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsStateWithLifecycle()
    val spotUiState by spotViewModel.spotUiState.collectAsStateWithLifecycle()

    if (commonTripUiState.tripInfo.trip == null
        || commonTripUiState.tripInfo.tempTrip == null
    ){
        navigateUp()
        return
    }

    val originalTrip = commonTripUiState.tripInfo.trip!!
    val tempTrip = commonTripUiState.tripInfo.tempTrip!!
    val isEditMode = commonTripUiState.isEditMode

    val showingTrip = if (commonTripUiState.isEditMode) tempTrip
                        else            originalTrip

    val currentDateIndex = commonTripUiState.tripInfo.dateIndex ?: 0
    val currentSpotIndex = commonTripUiState.tripInfo.spotIndex ?: 0

    var userSwiping by rememberSaveable { mutableStateOf(true) }

    val dateList = showingTrip.dateList
    val spotList = dateList.getOrNull(currentDateIndex)?.spotList ?: emptyList()
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
        else {
            delay(400)
            userSwiping = false
        }
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
                    repeat(10){
                        if (spotUiState.isMapLoaded){
                            mapAnimateToSpot(
                                scrollState,
                                spotList[currentSpotIndex],
                                dateList,
                                currentDateIndex,
                                cameraPositionState,
                                spotUiState.mapSize,
                                density,
                                coroutineScope
                            )
                            return@repeat
                        }
                        else{
                            delay(200)
                        }
                    }
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


        }

        if (currentSpotIndex < spotList.size && currentSpotIndex >= 0) {
            //animate pager
            coroutineScope.launch {
                spotPagerState.animateScrollToPage(currentSpotIndex)
            }

            //animate map spot
            coroutineScope.launch {
                repeat(10){
                    if (spotUiState.isMapLoaded){
                        mapAnimateToSpot(
                            scrollState,
                            spotList[currentSpotIndex],
                            dateList,
                            currentDateIndex,
                            cameraPositionState,
                            spotUiState.mapSize,
                            density,
                            coroutineScope
                        )
                        return@repeat
                    }
                    else{
                        delay(200)
                    }
                }

            }
        }
    }

    //if after set location, animate map spot
    LaunchedEffect(spotUiState.showSetLocationDialog) {
        if (!spotUiState.showSetLocationDialog && currentSpot?.location != null){
            repeat(10){
                if (spotUiState.isMapLoaded){
                    mapAnimateToSpot(
                        scrollState,
                        spotList[currentSpotIndex],
                        dateList,
                        currentDateIndex,
                        cameraPositionState,
                        spotUiState.mapSize,
                        density,
                        coroutineScope
                    )
                    return@repeat
                }
                else{
                    delay(200)
                }
            }
        }
    }


    SpotScreen(
        isDarkAppTheme = isDarkAppTheme,
        spotUiInfo = SpotUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
            useBlurEffect = useBlurEffect,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            isErrorExitOnTripScreen = isErrorExitOnTripScreen,
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
            _onMapLoaded = { spotViewModel.setIsMapLoaded(true) },
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






@Composable
private fun SpotScreen(
    isDarkAppTheme: Boolean,
    spotUiInfo: SpotUiInfo,
    spotData: SpotData,
    spotState: SpotState,
    spotMap: SpotMap,
    spotErrorCount: SpotErrorCount,
    spotDialog: SpotDialog,
    spotNavigate: SpotNavigate,
    spotImage: SpotImage,

    isCompactWidth: Boolean,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    onClickSave: () -> Unit,

    modifier: Modifier = Modifier
){
    val useBlurEffect = spotUiInfo.useBlurEffect

    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val dateList = showingTrip.dateList
    val spotList = dateList.getOrNull(currentDateIndex)?.spotList ?: emptyList()

    val currentDate = dateList.getOrNull(currentDateIndex)
    val currentSpot = spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current


    val snackBarHostState = remember { SnackbarHostState() }

    val topAppBarHazeState = if(useBlurEffect && spotUiInfo.use2Panes) rememberHazeState() else null



    //set top bar title
    val dateTitle = dateList.getOrNull(currentDateIndex)?.titleText

    val topBarTitle =
        if (spotUiInfo.isEditMode)
            stringResource(id = R.string.edit_spot)
        else if (spotUiInfo.use2Panes){
            if (currentSpot?.titleText == null || currentSpot.titleText == "")
                stringResource(id = R.string.no_title)
            else
                currentSpot.titleText!!
        }
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText!!
        }

    val subTitle =
        if (dateList.isNotEmpty()) {
            if (dateTitle == null)
                dateList[currentDateIndex].getDateText(spotUiInfo.dateTimeFormat, includeYear = true)
            else
                stringResource(id = R.string.sub_title, dateList[currentDateIndex].getDateText(spotUiInfo.dateTimeFormat, includeYear = true), dateTitle)
        }
        else null

    val talkbackSubTitle =
        if (dateList.isNotEmpty()) {
            if (dateTitle == null)
                getTalkbackDateText(dateList[currentDateIndex].date, spotUiInfo.dateTimeFormat, includeYear = true)
            else
                stringResource(id = R.string.sub_title, getTalkbackDateText(dateList[currentDateIndex].date, spotUiInfo.dateTimeFormat, includeYear = true), dateTitle)
        }
        else null




    val bottomSnackBarPadding by animateFloatAsState(
        targetValue = if (spotUiInfo.use2Panes) {
                            if (spotMap.isMapExpand) 66f + 24f
                            else if (spotUiInfo.isEditMode) 56f
                            else 0f
                        }
                        else {
                            if (spotMap.isMapExpand) 66f
                            else if (spotUiInfo.isEditMode) 56f
                            else 0f
                        },
        animationSpec = tween(300),
        label = "snackbar padding"
    )



    LaunchedEffect(spotDialog.isShowingDialog) {
        focusManager.clearFocus()
    }


    //set location dialog(full screen dialog)
    AnimatedVisibility(
        visible = spotDialog.showSetLocationDialog,
        enter = slideInVertically(animationSpec = tween(300), initialOffsetY = { it }),
        exit = slideOutVertically(animationSpec = tween(300), targetOffsetY = { it }),
        modifier = Modifier.zIndex(1f)
    ) {
        SetLocationDialog(
            internetEnabled = spotUiInfo.internetEnabled,
            dateTimeFormat = spotUiInfo.dateTimeFormat,
            use2Panes = false,
            showingTrip = showingTrip,
            dateList = dateList,
            spotList = spotList,
            dateIndex = currentDateIndex,
            spotIndex = currentSpotIndex,
            isDarkMapTheme = spotUiInfo.isDarkMapTheme,
            onClickCloseButton = spotNavigate::onClickBackButton,
            updateTripState = updateTripState,
            setShowSetLocationDialogToFalse = { spotDialog.setShowSetLocationDialog(false) },
            fusedLocationClient = spotMap.fusedLocationClient,
            setUserLocationEnabled = spotMap::setUserLocationEnabled
        )
    }

    val snackbarModifier = Modifier
            .width(500.dp)
            .padding(bottom = bottomSnackBarPadding.dp)
            .navigationBarsPadding()
            .imePadding()


    MyScaffold(
        modifier = modifier.imePadding(),
        snackbarHost = {
            Row {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = if (spotUiInfo.use2Panes) Modifier.padding(start = spotUiInfo.spacerValue / 2, end = spotUiInfo.spacerValue)
                                else Modifier
                ){
                    SnackbarHost(
                        hostState = snackBarHostState,
                        modifier = snackbarModifier,
                        snackbar = {
                            Snackbar(
                                snackbarData = it,
                                shape = MaterialTheme.shapes.small
                            )
                        }
                    )
                }
            }
        },
        //top bar
        topBar = {
            SomewhereTopAppBar(
                useHorizontalLayoutTitles = false,
                title = topBarTitle,
                subtitle = subTitle,
                talkbackSubTitle = talkbackSubTitle,
                internetEnabled = spotUiInfo.use2Panes || spotUiInfo.internetEnabled,

                //back button
                navigationIcon =
                    if (spotUiInfo.use2Panes || spotUiInfo.isEditMode) null
                    else                                    TopAppBarIcon.back,
                onClickNavigationIcon = spotNavigate::onClickBackButton,

                actionIcon1 = TopAppBarIcon.edit,
                actionIcon1Onclick = {
                    if (spotMap.isMapExpand)
                        spotMap.setIsMapExpanded(false)
                    spotUiInfo.setIsEditMode(true)
                },
                actionIcon1Visible = !spotUiInfo.isEditMode && showingTrip.editable,
                hazeState = topAppBarHazeState
            )
        },

        //bottom save cancel bar
        bottomSaveCancelBarVisible = spotUiInfo.isEditMode && !spotDialog.isShowingDialog,
        onClickCancel = {
            focusManager.clearFocus()
            spotNavigate.onClickBackButton()
        },
        onClickSave = onClickSave,
        saveEnabled = spotErrorCount.totalErrorCount <= 0 && !spotUiInfo.isErrorExitOnTripScreen

    ) { paddingValues ->

        //dialogs
        if(spotDialog.showExitDialog){
            TwoButtonsDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                positiveButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { spotDialog.setShowExitDialog(false) },
                onClickPositive = {
                    spotDialog.setShowExitDialog(false)
                    spotUiInfo.setIsEditMode(false)
                    updateTripState(true, spotData.originalTrip)

                    spotImage.organizeAddedDeletedImages(false)
                }
            )
        }

        if (spotDialog.showMemoDialog){
            MemoDialog(
                memoText = currentSpot?.memoText ?: "",
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
                onDismissRequest = { spotDialog.setShowSetBudgetDialog(false) },
                onSaveClick = { newBudget ->
                    spotDialog.setShowSetBudgetDialog(false)
                    currentSpot.setBudget(showingTrip, currentDateIndex, updateTripState, newBudget)
                }
            )
        }

        if (spotDialog.showSetDistanceDialog && currentSpot != null){
            SetBudgetOrDistanceDialog(
                initialValue = currentSpot.travelDistance.toDouble(),
                onDismissRequest = { spotDialog.setShowSetDistanceDialog(false) },
                onSaveClick = { newTravelDistance ->
                    spotDialog.setShowSetDistanceDialog(false)
                    currentSpot.setTravelDistance(showingTrip, currentDateIndex, updateTripState, newTravelDistance.toFloat())
                }
            )
        }

        if (spotDialog.showDeleteSpotDialog){
            TwoButtonsDialog(
                bodyText = stringResource(id = R.string.dialog_body_delete_spot),
                positiveButtonText = stringResource(id = R.string.dialog_button_delete),
                onDismissRequest = { spotDialog.setShowDeleteSpotDialog(false) },
                onClickPositive = {
                    deleteSpot(currentDateIndex, currentSpotIndex)
                    spotDialog.setShowDeleteSpotDialog(false)
                }
            )
        }


        //show when use 2 panes and no date
        AnimatedVisibility(
            visible = dateList.isEmpty(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.no_spot),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        AnimatedVisibility(
            visible = dateList.isNotEmpty(),
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {

                    if(!spotUiInfo.use2Panes) {
                        Spacer(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()))
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
                            onClickSpot = { toSpotIndex ->
                                spotUiInfo.setUserSwiping(false)
                                spotData.setCurrentSpotIndex(toSpotIndex)
                            },
                            onPrevDateClick = { toDateIndex ->
                                spotUiInfo.setUserSwiping(false)
                                spotData.setCurrentDateIndex(toDateIndex)
                                spotData.setCurrentSpotIndex(dateList[toDateIndex].spotList.lastIndex)
                            },
                            onNextDateClick = { toDateIndex ->
                                spotUiInfo.setUserSwiping(false)
                                spotData.setCurrentDateIndex(toDateIndex)
                                spotData.setCurrentSpotIndex(0)
                            }
                        )

                        MySpacerColumn(height = 16.dp)
                    }

                    Spot1Pane(
                        useBlurEffect= useBlurEffect,
                        isDarkAppTheme = isDarkAppTheme,
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
                        scaffoldPaddingValues = paddingValues,
                        topAppBarHazeState = topAppBarHazeState,
                        cameraPositionState = spotMap.cameraPositionState,
                        snackBarHostState = snackBarHostState,
                        focusManager = focusManager,
                        updateTripState = updateTripState,
                        setUserSwiping = spotUiInfo::setUserSwiping
                    )
                }
            }
        }
    }
}





@Composable
private fun Spot1Pane(
    useBlurEffect: Boolean,
    isDarkAppTheme: Boolean,
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

    scaffoldPaddingValues: PaddingValues,
    topAppBarHazeState: HazeState?,
    cameraPositionState: CameraPositionState,
    snackBarHostState: SnackbarHostState,
    focusManager: FocusManager,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    setUserSwiping: (userSwiping: Boolean) -> Unit,
){
    var lazyColumnHeight by rememberSaveable { mutableIntStateOf(0) }

    val showingTrip = if (spotUiInfo.isEditMode) spotData.tempTrip
                        else            spotData.originalTrip

    val currentDateIndex = spotData.currentDateIndex
    val currentSpotIndex = spotData.currentSpotIndex ?: 0

    val dateList = showingTrip.dateList
    val spotList = dateList.getOrNull(currentDateIndex)?.spotList ?: listOf()

    val currentDate = dateList.getOrNull(currentDateIndex) ?: Date(date = LocalDate.now())
    val currentSpot = currentDate.spotList.getOrNull(currentSpotIndex)

    val coroutineScope = rememberCoroutineScope()

    val lazyColumnModifier = Modifier
        .fillMaxSize()
        .imePadding()
        .onGloballyPositioned { lazyColumnHeight = it.size.height }

    //map + spot page
    LazyColumn(
        state = spotState.scrollState,
        modifier = if (topAppBarHazeState != null) lazyColumnModifier
            .hazeSource(state = topAppBarHazeState)
            .background(MaterialTheme.colorScheme.background)
            else lazyColumnModifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = !spotMap.isMapExpand,
        contentPadding = if (spotUiInfo.use2Panes) PaddingValues(top = 16.dp + scaffoldPaddingValues.calculateTopPadding())
                            else PaddingValues(0.dp)
    ) {
        item {
            //map
            SpotMapCard(
                modifier = if (spotUiInfo.use2Panes) Modifier
                    .padding(
                        spotUiInfo.spacerValue / 2,
                        0.dp,
                        spotUiInfo.spacerValue,
                        spotUiInfo.spacerValue
                    )
                    .clip(SmoothRoundedCornerShape(16.dp))
                    else Modifier,
                useBlurEffect = useBlurEffect,
                isEditMode = spotUiInfo.isEditMode,
                isMapExpand = spotMap.isMapExpand,
                expandHeight = if (spotUiInfo.use2Panes)
                                    (lazyColumnHeight / LocalDensity.current.density).toInt()
                                    - spotUiInfo.spacerValue.value.toInt()
                                    - 16 - scaffoldPaddingValues.calculateTopPadding().value.toInt()

                                else (lazyColumnHeight / LocalDensity.current.density).toInt() + 1,
                mapSize = spotMap.mapSize,
                onMapLoaded = spotMap::onMapLoaded,

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
                    coroutineScope.launch {
                        //scroll to top(map)
                        if (!spotMap.isMapExpand)
                            spotState.scrollState.animateScrollToItem(0)

                        //expand map
                        spotMap.setIsMapExpanded(!spotMap.isMapExpand)
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
                        showingTrip, currentDateIndex, updateTripState, null, null, null
                    )
                },
                setMapSize = {
                    spotMap.setMapSize(it)
                },
                openInGoogleMapEnabled = currentSpot?.googleMapsPlacesId != null && currentSpot.googleMapsPlacesId != "",
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
                spotTo = spotTo,
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

                        //each page
                        SpotDetailPage(
                            isDarkAppTheme = isDarkAppTheme,
                            spotUiInfo = spotUiInfo,
                            spotData = spotData.copy(currentSpotIndex = pageIndex),
                            errorCount = errorCount,
                            dialog = dialog,
                            navigate = navigate,
                            image = image,
                            focusManager = focusManager,
                            deleteTime = { isStartTime ->
                                if (isStartTime)
                                    currentSpot?.setStartTime(showingTrip, currentDateIndex, updateTripState, null)
                                else
                                    currentSpot?.setEndTime(showingTrip, currentDateIndex, updateTripState, null)
                            },
                            updateTripState = updateTripState
                        )
                    }
                )
            }
        }
    }
}





private fun mapAnimateToSpot(
    scrollState: LazyListState,
    currentSpot: Spot,
    dateList: List<Date>,
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
                fitBoundsToMarkers(cameraPositionState, mapSize, listOf(spotFrom, spotTo), density)
            }
        }
        //if spot is not move
        else if (currentSpot.spotType.isNotMove() && currentSpot.location != null) {
            coroutineScope.launch {
                fitBoundsToMarkers(cameraPositionState, mapSize, listOf(currentSpot))
            }
        }
    }
}