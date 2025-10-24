package com.newpaper.somewhere.feature.trip.trip

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.SeeOnMapExtendedFAB
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.ImageCard
import com.newpaper.somewhere.core.ui.card.trip.InformationCard
import com.newpaper.somewhere.core.ui.card.trip.MemoCard
import com.newpaper.somewhere.core.ui.card.trip.TitleCard
import com.newpaper.somewhere.core.ui.card.trip.currencyItem
import com.newpaper.somewhere.core.ui.card.trip.travelDistanceItem
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getDurationText
import com.newpaper.somewhere.core.utils.convert.getEndDateText
import com.newpaper.somewhere.core.utils.convert.getFirstLocation
import com.newpaper.somewhere.core.utils.convert.getLastEnabledDate
import com.newpaper.somewhere.core.utils.convert.getMaxInviteFriends
import com.newpaper.somewhere.core.utils.convert.getStartDateText
import com.newpaper.somewhere.core.utils.convert.getTalkbackStartEndDateText
import com.newpaper.somewhere.core.utils.convert.getTotalBudgetText
import com.newpaper.somewhere.core.utils.convert.getTotalTravelDistanceText
import com.newpaper.somewhere.core.utils.convert.setColor
import com.newpaper.somewhere.core.utils.convert.setCurrencyType
import com.newpaper.somewhere.core.utils.convert.setImage
import com.newpaper.somewhere.core.utils.convert.setMemoText
import com.newpaper.somewhere.core.utils.convert.setSpotType
import com.newpaper.somewhere.core.utils.convert.setStartTime
import com.newpaper.somewhere.core.utils.convert.setTitleText
import com.newpaper.somewhere.core.utils.getDateText
import com.newpaper.somewhere.core.utils.getTalkbackDateText
import com.newpaper.somewhere.core.utils.getTimeText
import com.newpaper.somewhere.core.utils.millisToLocalDate
import com.newpaper.somewhere.feature.dialog.dateRange.DateRangeDialog
import com.newpaper.somewhere.feature.dialog.deleteOrNot.TwoButtonsDialog
import com.newpaper.somewhere.feature.dialog.memo.MemoDialog
import com.newpaper.somewhere.feature.dialog.setColor.SetColorDialog
import com.newpaper.somewhere.feature.dialog.setCurrencyType.SetCurrencyTypeDialog
import com.newpaper.somewhere.feature.dialog.setSpotType.SetSpotTypeDialog
import com.newpaper.somewhere.feature.dialog.setTime.SetTimeDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trip.component.DateCard
import com.newpaper.somewhere.feature.trip.trip.component.ShareTripCards
import com.newpaper.somewhere.feature.trip.trip.component.TripDurationCard
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun TripRoute(
    isDarkAppTheme: Boolean,
    appUserData: UserData,
    use2Panes: Boolean,
    spacerValue: Dp,
    useBlurEffect: Boolean,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    commonTripViewModel: CommonTripViewModel,

    //navigate
    navigateUp: () -> Unit,
    navigateToShareTrip: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToInvitedFriends: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    navigateToTripMap: () -> Unit,

    setIsShowingDialog: (isShowingDialog: Boolean) -> Unit,

    //
    modifier: Modifier = Modifier,
    setIsErrorExit: (Boolean) -> Unit = {},
    tripViewModel: TripViewModel = hiltViewModel()
){
    val coroutineScope = rememberCoroutineScope()

    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()
    val tripUiState by tripViewModel.tripUiState.collectAsState()

    val isNewTrip = commonTripUiState.isNewTrip

    if (commonTripUiState.tripInfo.trip == null
        || commonTripUiState.tripInfo.tempTrip == null
    ){
        navigateUp()
        return
    }

    val originalTrip = commonTripUiState.tripInfo.trip!!
    val tempTrip = commonTripUiState.tripInfo.tempTrip!!
    val isEditMode = commonTripUiState.isEditMode

    //get trip data from firestore
    //on first load
    LaunchedEffect(Unit){
        if (!isNewTrip) {
            coroutineScope.launch(Dispatchers.IO) {
                commonTripViewModel.updateTrip(
                    internetEnabled = internetEnabled,
                    appUserId = appUserData.userId,
                    tripWithEmptyDateList = originalTrip
                )
                delay(150)
                tripViewModel.setLoadingTrip(false)
            }
        }
        else {
            tripViewModel.setLoadingTrip(false)

            if (tempTrip.dateList.isEmpty())
                tripViewModel.setShowDateRangeDialog(true)
        }
    }

    LaunchedEffect(isEditMode) {
        if (!isEditMode) {
            tripViewModel.initAllErrorCount()
            setIsErrorExit(false)
        }
    }

    LaunchedEffect(tripUiState.isShowingDialog) {
        setIsShowingDialog(tripUiState.isShowingDialog)
    }


    val onClickBackButton = {
        if (originalTrip != tempTrip)
            tripViewModel.setShowExitDialog(true)
        else {
            commonTripViewModel.setIsEditMode(false)

            if (isNewTrip){
                navigateUpAndDeleteNewTrip(originalTrip)
            }
        }
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onClickBackButton()
        }

    TripScreen(
        isDarkAppTheme = isDarkAppTheme,
        appUserId = appUserData.userId,
        isUsingSomewherePro = appUserData.isUsingSomewherePro,
        tripUiInfo = TripUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
            useBlurEffect = useBlurEffect,
            loadingTrip = tripUiState.loadingTrip,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            isEditMode = isEditMode,
            _setIsEditMode = commonTripViewModel::setIsEditMode
        ),
        tripData = TripData(
            originalTrip = originalTrip,
            tempTrip = tempTrip,
            isNewTrip = isNewTrip
        ),
        tripErrorCount = TripErrorCount(
            totalErrorCount = tripUiState.totalErrorCount,
            dateTitleErrorCount = tripUiState.dateTitleErrorCount,
            _increaseTotalErrorCount = {
                tripViewModel.increaseTotalErrorCount()
                setIsErrorExit(tripUiState.totalErrorCount + 1 > 0)
            },
            _decreaseTotalErrorCount = {
                tripViewModel.decreaseTotalErrorCount()
                setIsErrorExit(tripUiState.totalErrorCount - 1 > 0)
            },
            _increaseDateTitleErrorCount = tripViewModel::increaseDateTitleErrorCount,
            _decreaseDateTitleErrorCount = tripViewModel::decreaseDateTitleErrorCount
        ),
        tripDialog = TripDialog(
            isShowingDialog = commonTripUiState.isShowingDialog,
            showExitDialog = tripUiState.showExitDialog,
            showSetDateRangeDialog = tripUiState.showSetDateRangeDialog,
            showMemoDialog = tripUiState.showMemoDialog,
            showSetCurrencyDialog = tripUiState.showSetCurrencyDialog,
            showSetColorDialog = tripUiState.showSetColorDialog,
            showSetTimeDialog = tripUiState.showSetTimeDialog,
            showSetSpotTypeDialog = tripUiState.showSetSpotTypeDialog,

            _setShowExitDialog = tripViewModel::setShowExitDialog,
            _setShowDateRangeDialog = tripViewModel::setShowDateRangeDialog,
            _setShowMemoDialog = tripViewModel::setShowMemoDialog,
            _setShowSetCurrencyDialog = tripViewModel::setShowSetCurrencyDialog,
            _setShowSetColorDialog = tripViewModel::setShowSetColorDialog,
            _setShowSetTimeDialog = tripViewModel::setShowSetTimeDialog,
            _setShowSetSpotTypeDialog = tripViewModel::setShowSetSpotTypeDialog,

            selectedDate = tripUiState.selectedDate,
            _setSelectedDate = tripViewModel::setSelectedDate,
            selectedSpot = tripUiState.selectedSpot,
            _setSelectedSpot = tripViewModel::setSelectedSpot
        ),
        tripNavigate = TripNavigate(
            _navigateUp = {
                if (!isEditMode) navigateUp()
                else onClickBackButton()
            },
            _navigateToShareTrip = navigateToShareTrip,
            _navigateToInviteFriend = navigateToInviteFriend,
            _navigateToInvitedFriends = navigateToInvitedFriends,
            _navigateToImage = navigateToImage,
            _navigateToSpot = navigateToSpot,
            _navigateToTripMap = navigateToTripMap,
            _navigateUpAndDeleteNewTrip = navigateUpAndDeleteNewTrip
        ),
        updateTripState = commonTripViewModel::updateTripState,
        updateTripDurationAndTripState = {toTempTrip, startDate, endDate ->
            tripViewModel.updateTripDurationAndTripState(
                toTempTrip = toTempTrip,
                startDate = startDate,
                endDate = endDate,
                updateTripState = { newTrip ->
                    commonTripViewModel.updateTripState(toTempTrip, newTrip)
                }
            )
        },
        tripImage = TripImage(
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
            _reorderTripImageList = tripViewModel::reorderTripImageList
        ),

        reorderDateList = { currentIndex, destinationIndex ->
            tripViewModel.reorderDateList(currentIndex, destinationIndex)
        },
        addNewSpot = commonTripViewModel::addNewSpot,
        deleteSpot = commonTripViewModel::deleteSpot,
        reorderSpotList = commonTripViewModel::reorderSpotListAndUpdateTravelDistance,
        onClickSave = {
            coroutineScope.launch {
                if (isNewTrip || originalTrip != tempTrip){
                    //save tripUiState tripList
                    val beforeTempTripDateListLastIndex =
                        commonTripViewModel.saveTrip(
                            appUserId = appUserData.userId,
                            deleteNotEnabledDate = true
                        )

                    commonTripViewModel.setIsEditMode(false)
                    commonTripViewModel.setIsNewTrip(false)

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(
                        trip = commonTripViewModel.commonTripUiState.value.tripInfo.tempTrip!!,
                        tempTripDateListLastIndex = beforeTempTripDateListLastIndex
                    )
                }
                else {
                    commonTripViewModel.setIsEditMode(false)
                }

                commonTripViewModel.organizeAddedDeletedImages(
                    tripManagerId = tempTrip.managerId,
                    isClickSave = true,
                    isInTripsScreen = true
                )
            }
        },
        modifier = modifier,
        currentDateIndex = if (use2Panes) commonTripUiState.tripInfo.dateIndex ?: 0 else null,
        currentSpotIndex = if (use2Panes) commonTripUiState.tripInfo.spotIndex ?: 0 else null,
    )
}



















@Composable
private fun TripScreen(
    isDarkAppTheme: Boolean,
    appUserId: String,
    isUsingSomewherePro: Boolean,
    tripUiInfo: TripUiInfo,
    tripData: TripData,
    tripErrorCount: TripErrorCount,
    tripDialog: TripDialog,
    tripNavigate: TripNavigate,
    tripImage: TripImage,

    //update
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,

    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateIndex: Int, spotIndex: Int) -> Unit,

    //reorder date list
    reorderDateList: (currentIndex: Int, destinationIndex: Int) -> Unit,
    reorderSpotList: (dateIndex: Int, currentSpotIndex: Int, destinationSpotIndex: Int) -> Unit,

    onClickSave: () -> Unit,

    modifier: Modifier = Modifier,
    currentDateIndex: Int? = null,
    currentSpotIndex: Int? = null,
){
    var prevDateIndex: Int? by remember { mutableStateOf(null) }

    val coroutineScope = rememberCoroutineScope()

    val use2Panes = tripUiInfo.use2Panes
    val spacerValue = tripUiInfo.spacerValue
    val useBlurEffect = tripUiInfo.useBlurEffect

    val loadingTrip = tripUiInfo.loadingTrip
    val dateTimeFormat = tripUiInfo.dateTimeFormat
    val internetEnabled = tripUiInfo.internetEnabled

    val isEditMode = tripUiInfo.isEditMode

    val showingTrip =
        if (isEditMode) tripData.tempTrip
        else tripData.originalTrip

    val enabledDateList = showingTrip.dateList.filter { it.enabled }
    var enabledDateListIsEmpty by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(enabledDateList.isEmpty()) {
        delay(100)
        enabledDateListIsEmpty = enabledDateList.isEmpty()
    }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    //slideStates
    val slideStates = remember { mutableStateMapOf(
        *showingTrip.dateList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }
    val dateItemHeights = remember { mutableStateMapOf(
        *showingTrip.dateList.map { it.id to 0f }.toTypedArray()
    ) }

    //images pager state
    val imagesPagerState = rememberPagerState { showingTrip.imagePathList.size }


    //set top bar title
    val topBarTitle =
        if (isEditMode) stringResource(id = R.string.edit_trip)
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText!!
        }

    val snackBarPadding by animateFloatAsState(
        targetValue = if (isEditMode) 10f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    //for expanded fab animation
    val isFABExpanded by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }


    val endDate = showingTrip.getLastEnabledDate()
    val endDateIndex = endDate?.index

    val defaultDateRange =
        if (showingTrip.dateList.isNotEmpty()) {
            showingTrip.dateList.first().date..
                    showingTrip.dateList[endDateIndex?: 0].date

        } else
            LocalDate.now().let { now -> now.plusDays(1)..now.plusDays(5) }

    val topAppBarHazeState = if(useBlurEffect) rememberHazeState() else null




    LaunchedEffect(tripDialog.isShowingDialog) {
        focusManager.clearFocus()
    }

    LaunchedEffect(currentDateIndex) {
        if (use2Panes && currentDateIndex != null && prevDateIndex != null){
            coroutineScope.launch {
                scrollState.animateScrollToItem(6 + currentDateIndex)
            }
        }
        if (currentDateIndex != null)
            prevDateIndex = currentDateIndex
    }







    MyScaffold (
        modifier = modifier.imePadding(),
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .padding(bottom = snackBarPadding.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        },

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                title = topBarTitle,
                internetEnabled = internetEnabled,
                navigationIcon = if(!isEditMode) TopAppBarIcon.back else null,
                onClickNavigationIcon = { tripNavigate.navigateUp() },

                actionIcon1 = TopAppBarIcon.edit,
                actionIcon1Onclick = { tripUiInfo.setIsEditMode(true) },
                actionIcon1Visible = !loadingTrip && !isEditMode && !use2Panes && showingTrip.editable,
                hazeState = topAppBarHazeState
            )
        },

        //bottom floating action button
        floatingActionButton = {
            SeeOnMapExtendedFAB(
                visible = !isEditMode && showingTrip.getFirstLocation() != null,
                onClick = tripNavigate::navigateToTripMap,
                expanded = isFABExpanded
            )
        },
        floatingActionButtonPosition = if (use2Panes) FabPosition.Start else FabPosition.End,

        //bottom save cancel button
        bottomSaveCancelBarVisible = isEditMode && !tripDialog.isShowingDialog && !use2Panes,
        onClickCancel = {
            focusManager.clearFocus()
            tripNavigate.navigateUp()
        },
        onClickSave = onClickSave,
        saveEnabled = tripErrorCount.totalErrorCount <= 0

    ) { paddingValues ->

        //dialogs
        if (tripDialog.showExitDialog) {
            TwoButtonsDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                positiveButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { tripDialog.setShowExitDialog(false) },
                onClickPositive = {
                    tripDialog.setShowExitDialog(false)
                    tripUiInfo.setIsEditMode(false)
                    updateTripState(true, tripData.originalTrip)

                    if (tripData.isNewTrip)
                        tripNavigate.navigateUpAndDeleteNewTrip(tripData.originalTrip)

                    tripImage.organizeAddedDeletedImages(false)
                }
            )
        }

        if (tripDialog.showSetDateRangeDialog){
            DateRangeDialog(
                defaultDateRange = defaultDateRange,
                dateTimeFormat = dateTimeFormat,
                onDismissRequest = {
                    tripDialog.setShowDateRangeDialog(false)
                },
                onConfirm = {startDateMillis, endDateMillis ->
                    if (startDateMillis != null && endDateMillis != null){
                        updateTripDurationAndTripState(
                            true,
                            millisToLocalDate(startDateMillis),
                            millisToLocalDate(endDateMillis)
                        )
                    }
                    tripDialog.setShowDateRangeDialog(false)
                }
            )
        }

        if (tripDialog.showMemoDialog){
            MemoDialog(
                memoText = showingTrip.memoText ?: "",
                onDismissRequest = { tripDialog.setShowMemoDialog(false) }
            )
        }

        if (tripDialog.showSetCurrencyDialog) {
            SetCurrencyTypeDialog(
                initialCurrencyType = showingTrip.unitOfCurrencyType,
                onOkClick = { newCurrencyType ->
                    showingTrip.setCurrencyType(updateTripState, newCurrencyType)
                    tripDialog.setShowSetCurrencyDialog(false)
                },
                onDismissRequest = {
                    tripDialog.setShowSetCurrencyDialog(false)
                }
            )
        }

        if (tripDialog.showSetColorDialog && tripDialog.selectedDate != null) {
            SetColorDialog(
                initialColor = tripDialog.selectedDate.color,
                onDismissRequest = {
                    tripDialog.setShowSetColorDialog(false)
                    tripDialog.setSelectedDate(null)
                },
                onOkClick = {
                    tripDialog.setShowSetColorDialog(false)
                    tripDialog.selectedDate.setColor(showingTrip, updateTripState, it)
                    tripDialog.setSelectedDate(null)
                }
            )
        }

        if (tripDialog.showSetTimeDialog && tripDialog.selectedDate != null && tripDialog.selectedSpot != null){
            SetTimeDialog(
                initialTime = tripDialog.selectedSpot.startTime ?: LocalTime.of(12,0),
                timeFormat = dateTimeFormat.timeFormat,
                isSetStartTime = true,
                onDismissRequest = {
                    tripDialog.setShowSetTimeDialog(false)
                    tripDialog.setSelectedDate(null)
                },
                onConfirm = {newTime ->
                    tripDialog.selectedSpot.setStartTime(showingTrip, tripDialog.selectedDate.index, updateTripState, newTime)
                    tripDialog.setShowSetTimeDialog(false)
                    tripDialog.setSelectedDate(null)
                }
            )
        }

        if (tripDialog.showSetSpotTypeDialog && tripDialog.selectedDate != null && tripDialog.selectedSpot != null) {
            SetSpotTypeDialog(
                initialSpotType = tripDialog.selectedSpot.spotType,
                onDismissRequest = {
                    tripDialog.setShowSetSpotTypeDialog(false)
                    tripDialog.setSelectedDate(null)
                },
                onClickOk = { newSpotType ->
                    tripDialog.setShowSetSpotTypeDialog(false)
                    tripDialog.selectedSpot.setSpotType(
                        showingTrip,
                        enabledDateList,
                        tripDialog.selectedDate.index,
                        updateTripState,
                        newSpotType
                    )
                    tripDialog.setSelectedDate(null)
                }
            )
        }





        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    spacerValue, 16.dp + paddingValues.calculateTopPadding(), if (use2Panes) spacerValue / 2 else spacerValue, 200.dp
                ),
                modifier = if (topAppBarHazeState != null) Modifier
                    .fillMaxSize()
                    .hazeSource(state = topAppBarHazeState)
                    .background(MaterialTheme.colorScheme.background)
                            else Modifier.fillMaxSize()
            ) {

                //share trip (trip mate / share)
                item {
                    ShareTripCards(
                        trip = showingTrip,
                        maxInviteFriends = getMaxInviteFriends(isUsingSomewherePro),
                        userIsManager = appUserId == showingTrip.managerId,
                        internetEnabled = internetEnabled,
                        isEditMode = isEditMode,
                        onClickInvitedFriends = tripNavigate::navigateToInvitedFriends,
                        onClickAddFriend = tripNavigate::navigateToInviteFriend,
                        onClickShareTrip = { tripNavigate.navigateToShareTrip(showingTrip.imagePathList, imagesPagerState.currentPage) }
                    )
                }





                //title card
                item {
                    TitleCard(
                        isEditMode = isEditMode,
                        useDelayEnter = true,
                        upperTitleText = stringResource(id = R.string.trip_title),
                        titleText = showingTrip.titleText,
                        onTitleChange = { newTitleText ->
                            showingTrip.setTitleText(updateTripState, newTitleText)
                        },
                        focusManager = focusManager,
                        isLongText = {
                            if (it) tripErrorCount.increaseTotalErrorCount()
                            else tripErrorCount.decreaseTotalErrorCount()
                        }
                    )
                }






                //image card
                item {
                    ImageCard(
                        isDarkAppTheme = isDarkAppTheme,
                        imageUserId = showingTrip.managerId,
                        internetEnabled = internetEnabled,
                        isEditMode = isEditMode,
                        imagePathList = showingTrip.imagePathList,
                        onClickImage = { initialImageIndex ->
                            tripNavigate.navigateToImage(showingTrip.imagePathList, initialImageIndex)
                        },
                        onAddImages = { imageFiles ->
                            tripImage.addAddedImages(imageFiles)
                            showingTrip.setImage(
                                updateTripState,
                                showingTrip.imagePathList + imageFiles
                            )
                        },
                        deleteImage = { imageFile ->
                            tripImage.addDeletedImages(listOf(imageFile))

                            val newList: MutableList<String> =
                                showingTrip.imagePathList.toMutableList()
                            newList.remove(imageFile)

                            showingTrip.setImage(updateTripState, newList.toList())
                        },
                        isOverImage = {
                            if (it) tripErrorCount.increaseTotalErrorCount()
                            else tripErrorCount.decreaseTotalErrorCount()
                        },
                        reorderImageList = tripImage::reorderTripImageList,
                        downloadImage = tripImage::downloadImage,
                        saveImageToInternalStorage = tripImage::saveImageToInternalStorage,
                        pagerState = imagesPagerState
                    )
                }





                //trip duration card
                item {
                    val startDate = showingTrip.dateList.firstOrNull()
                    val sameYear = startDate?.date?.year == endDate?.date?.year

                    TripDurationCard(
                        defaultDateRange = defaultDateRange,
                        isDateListEmpty = showingTrip.dateList.isEmpty(),
                        startDateText = showingTrip.getStartDateText(dateTimeFormat, true),
                        endDateText = showingTrip.getEndDateText(dateTimeFormat, !sameYear),
                        talkbackDateText = showingTrip.getTalkbackStartEndDateText(dateTimeFormat),
                        durationText = showingTrip.getDurationText(),
                        isEditMode = isEditMode,
                        onClick = { tripDialog.setShowDateRangeDialog(true) },
                    )
                }





                //information card
                item {
                    InformationCard(
                        isEditMode = isEditMode,
                        list = listOf(
                            currencyItem.copy(
                                text = showingTrip.getTotalBudgetText(),
                                onClick = {
                                    tripDialog.setShowSetCurrencyDialog(true)
                                }),
                            travelDistanceItem.copy(text =  showingTrip.getTotalTravelDistanceText())
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
                        isLongText = {
                            if (it) tripErrorCount.increaseTotalErrorCount()
                            else tripErrorCount.decreaseTotalErrorCount()
                        },
                        showMemoDialog = {
                            tripDialog.setShowMemoDialog(true)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }





                // all dates
                itemsIndexed(enabledDateList) { dateIndex, date ->

                    key(enabledDateList.map { it.id }) {
                        val slideState = slideStates[date.id] ?: SlideState.NONE

                        DateCard(
                            currentDateIndex = currentDateIndex,
                            currentSpotIndex = currentSpotIndex,
                            visible = !loadingTrip || !enabledDateListIsEmpty,
                            trip = showingTrip,
                            dateIndex = dateIndex,
                            isEditMode = isEditMode,
                            dateTimeFormat = dateTimeFormat,
                            focusManager = focusManager,

                            slideState = slideState,
                            upperItemHeight = if (dateIndex == 0) 0f
                                                else dateItemHeights[enabledDateList[dateIndex - 1].id] ?: 0f,
                            lowerItemHeight = if (dateIndex == enabledDateList.lastIndex) 0f
                                                else dateItemHeights[enabledDateList[dateIndex + 1].id] ?: 0f,
                            onItemHeightChanged = { dateId, itemHeight ->
                                dateItemHeights[dateId] = itemHeight
                            },
                            onDateTitleTextChange = {
                                date.setTitleText(showingTrip, updateTripState, it)
                            },
                            isLongText = {
                                if (it) tripErrorCount.increaseTotalErrorCount()
                                else tripErrorCount.decreaseTotalErrorCount()
                            },
                            onClickDateMoveUp = {
                                val destinationIndex = if (dateIndex - 1 < 0) 0
                                                        else dateIndex - 1

                                if (dateIndex != destinationIndex) {
                                    slideStates[enabledDateList[dateIndex].id] = SlideState.UP
                                    slideStates[enabledDateList[destinationIndex].id] = SlideState.DOWN

                                    //on drag end
                                    coroutineScope.launch {
                                        delay(405)
                                        //reorder list
                                        reorderDateList(dateIndex, destinationIndex)

                                        //all slideState to NONE
                                        slideStates.putAll(enabledDateList.map { it.id }
                                            .associateWith { SlideState.NONE })
                                    }
                                }
                            },
                            onClickDateMoveDown = {
                                val destinationIndex =
                                    if (dateIndex + 1 > enabledDateList.lastIndex) dateIndex
                                    else dateIndex + 1

                                if (dateIndex != destinationIndex) {
                                    slideStates[enabledDateList[dateIndex].id] = SlideState.DOWN
                                    slideStates[enabledDateList[destinationIndex].id] = SlideState.UP


                                    //on drag end
                                    coroutineScope.launch {
                                        delay(405)
                                        //reorder list
                                        reorderDateList(dateIndex, destinationIndex)

                                        //all slideState to NONE
                                        slideStates.putAll(enabledDateList.map { it.id }
                                            .associateWith { SlideState.NONE })
                                    }
                                }
                            },
                            onClickSetDateColor = {
                                tripDialog.setSelectedDate(date)
                                tripDialog.setShowSetColorDialog(true)
                            },
                            onSpotTitleTextChange = { spot, spotTitleText ->
                                spot.setTitleText(
                                    showingTrip,
                                    dateIndex,
                                    updateTripState,
                                    spotTitleText
                                )
                            },
                            onClickSpotItem = { spot ->
                                tripNavigate.navigateToSpot(dateIndex, spot.index)
                            },
                            onClickDeleteSpot = { spot ->
                                deleteSpot(dateIndex, spot.index)
                                tripImage.addDeletedImages(enabledDateList[dateIndex].spotList[spot.index].imagePathList)
                            },
                            onClickSpotSideText = { spot ->
                                tripDialog.setSelectedDate(date)
                                tripDialog.setSelectedSpot(spot)
                                tripDialog.setShowSetTimeDialog(true)
                            },
                            onClickSpotPoint = { spot ->
                                tripDialog.setSelectedDate(date)
                                tripDialog.setSelectedSpot(spot)
                                tripDialog.setShowSetSpotTypeDialog(true)
                            },
                            onClickNewSpot = {
                                addNewSpot(dateIndex)
                            },
                            reorderSpotList = { dateIndex, currentSpotIndex, destinationSpotIndex ->
                                reorderSpotList(dateIndex, currentSpotIndex, destinationSpotIndex)
                            }
                        )
                    }
                }



                if (appUserId == tripData.originalTrip.managerId) {
                    item {
                        MySpacerColumn(height = 100.dp)

                        val firstCreatedTime = tripData.originalTrip.firstCreatedTime
                        val localZoneId = ZoneId.systemDefault()
                        val firstCreatedTimeZoned = firstCreatedTime.withZoneSameInstant(localZoneId)

                        val dateText = getDateText(
                            date = firstCreatedTimeZoned.toLocalDate(),
                            dateTimeFormat = dateTimeFormat,
                        )
                        val talkbackDateText = getTalkbackDateText(
                            date = firstCreatedTimeZoned.toLocalDate(),
                            dateTimeFormat = dateTimeFormat
                        )

                        val timeText = getTimeText(
                            time = firstCreatedTimeZoned.toLocalTime(),
                            timeFormat = dateTimeFormat.timeFormat
                        )

                        val tripCreatedTimeText = stringResource(id = R.string.trip_created_time)

                        Text(
                            text = "${tripCreatedTimeText}\n$dateText, $timeText",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics{
                                contentDescription = "${tripCreatedTimeText}\n$talkbackDateText, $timeText"
                            }
                        )
                    }
                }
            }
        }
    }
}







