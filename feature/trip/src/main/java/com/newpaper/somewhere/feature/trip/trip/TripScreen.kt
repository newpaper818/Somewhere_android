package com.newpaper.somewhere.feature.trip.trip

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.SeeOnMapExtendedFAB
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.ImageCard
import com.newpaper.somewhere.core.ui.card.trip.InformationCard
import com.newpaper.somewhere.core.ui.card.trip.MemoCard
import com.newpaper.somewhere.core.ui.card.trip.TitleCard
import com.newpaper.somewhere.core.ui.card.trip.currencyItem
import com.newpaper.somewhere.core.ui.card.trip.travelDistanceItem
import com.newpaper.somewhere.core.ui.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getDurationText
import com.newpaper.somewhere.core.utils.convert.getEndDateText
import com.newpaper.somewhere.core.utils.convert.getFirstLocation
import com.newpaper.somewhere.core.utils.convert.getLastEnabledDate
import com.newpaper.somewhere.core.utils.convert.getStartDateText
import com.newpaper.somewhere.core.utils.convert.getTotalBudgetText
import com.newpaper.somewhere.core.utils.convert.getTotalTravelDistanceText
import com.newpaper.somewhere.core.utils.convert.setColor
import com.newpaper.somewhere.core.utils.convert.setCurrencyType
import com.newpaper.somewhere.core.utils.convert.setImage
import com.newpaper.somewhere.core.utils.convert.setMemoText
import com.newpaper.somewhere.core.utils.convert.setTitleText
import com.newpaper.somewhere.core.utils.millisToLocalDate
import com.newpaper.somewhere.feature.dialog.dateRange.DateRangeDialog
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.dialog.memo.MemoDialog
import com.newpaper.somewhere.feature.dialog.setColor.SetColorDialog
import com.newpaper.somewhere.feature.dialog.setCurrencyType.SetCurrencyTypeDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trip.component.DateListEmptyTextCard
import com.newpaper.somewhere.feature.trip.trip.component.DateListItem
import com.newpaper.somewhere.feature.trip.trip.component.DateListTopTitleCard
import com.newpaper.somewhere.feature.trip.trip.component.SharingWithFriendsCard
import com.newpaper.somewhere.feature.trip.trip.component.TripDurationCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun TripRoute(
    appUserId: String,
    use2Panes: Boolean,
    spacerValue: Dp,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    commonTripViewModel: CommonTripViewModel,

    //navigate
    navigateUp: () -> Unit,
    navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToInvitedFriends: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateToDate: (dateIndex: Int) -> Unit,
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
                    appUserId = appUserId,
                    tripWithEmptyDateList = originalTrip
                )
                delay(150)
                tripViewModel.setLoadingTrip(false)
            }
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
        appUserId = appUserId,
        tripUiInfo = TripUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
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

            _setShowExitDialog = tripViewModel::setShowExitDialog,
            _setShowDateRangeDialog = tripViewModel::setShowDateRangeDialog,
            _setShowMemoDialog = tripViewModel::setShowMemoDialog,
            _setShowSetCurrencyDialog = tripViewModel::setShowSetCurrencyDialog,
            _setShowSetColorDialog = tripViewModel::setShowSetColorDialog,

            selectedDate = tripUiState.selectedDate,
            _setSelectedDate = tripViewModel::setSelectedDate
        ),
        tripNavigate = TripNavigate(
            _navigateUp = {
                if (!isEditMode) navigateUp()
                else onClickBackButton()
            },
            _navigateToInviteFriend = navigateToInviteFriend,
            _navigateToInvitedFriends = navigateToInvitedFriends,
            _navigateToImage = navigateToImage,
            _navigateToDate = navigateToDate,
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
        onClickSave = {
            coroutineScope.launch {
                if (isNewTrip || originalTrip != tempTrip){
                    //save tripUiState tripList
                    val beforeTempTripDateListLastIndex =
                        commonTripViewModel.saveTrip(
                            appUserId = appUserId,
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
        currentDateIndex = commonTripUiState.tripInfo.dateIndex
    )
}



















@Composable
private fun TripScreen(
    appUserId: String,
    tripUiInfo: TripUiInfo,
    tripData: TripData,
    tripErrorCount: TripErrorCount,
    tripDialog: TripDialog,
    tripNavigate: TripNavigate,
    tripImage: TripImage,

    //update
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,

    //reorder date list
    reorderDateList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    onClickSave: () -> Unit,

    modifier: Modifier = Modifier,
    currentDateIndex: Int? = null,
){
    val coroutineScope = rememberCoroutineScope()

    val use2Panes = tripUiInfo.use2Panes
    val spacerValue = tripUiInfo.spacerValue

    val loadingTrip = tripUiInfo.loadingTrip
    val dateTimeFormat = tripUiInfo.dateTimeFormat
    val internetEnabled = tripUiInfo.internetEnabled

    val isEditMode = tripUiInfo.isEditMode

    val showingTrip =
        if (isEditMode) tripData.tempTrip
        else tripData.originalTrip

    val enabledDateList = showingTrip.dateList.filter { it.enabled }
    val isFirstLoading by rememberSaveable { mutableStateOf(enabledDateList.isEmpty()) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    //slideStates
    val slideStates = remember { mutableStateMapOf(
        *showingTrip.dateList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }


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





    LaunchedEffect(tripDialog.isShowingDialog) {
        focusManager.clearFocus()
    }


    MyScaffold (
        modifier = modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding(),
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
                internetEnabled = internetEnabled,
                navigationIcon = if(!isEditMode) TopAppBarIcon.back else null,
                onClickNavigationIcon = { tripNavigate.navigateUp() },
                actionIcon2 = if (!loadingTrip && !isEditMode && !use2Panes && showingTrip.editable) TopAppBarIcon.edit else null,
                actionIcon2Onclick = {
                    tripUiInfo.setIsEditMode(true)
                }
            )
        },

        //bottom floating action button
        floatingActionButton = {
            if (!use2Panes)
                SeeOnMapExtendedFAB(
                    visible = !isEditMode && showingTrip.getFirstLocation() != null,
                    onClick = tripNavigate::navigateToTripMap,
                    expanded = isFABExpanded
                )
        },

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
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { tripDialog.setShowExitDialog(false) },
                onClickDelete = {
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





        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    spacerValue, 16.dp, if (use2Panes) spacerValue / 2 else spacerValue, 200.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    SharingWithFriendsCard(
                        trip = showingTrip,
                        userIsManager = appUserId == showingTrip.managerId,
                        internetEnabled = internetEnabled,
                        isEditMode = isEditMode,
                        onClickInvitedFriends = tripNavigate::navigateToInvitedFriends,
                        onClickShareTrip = tripNavigate::navigateToInviteFriend
                    )
                }





                //title card
                item {
                    TitleCard(
                        isEditMode = isEditMode,
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
                        saveImageToInternalStorage = tripImage::saveImageToInternalStorage
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









                //dates card
                item {
                    StartEndDummySpaceWithRoundedCorner(isFirst = true, isLast = false)
                }

                //dates text
                item {
                    DateListTopTitleCard(
                        isEditMode = isEditMode,
                        dateListIsEmpty = showingTrip.dateList.isEmpty(),
                        dateTitleErrorCount = tripErrorCount.dateTitleErrorCount
                    )
                }

                //empty date
                item {
                    DateListEmptyTextCard(
                        isEditMode = isEditMode,
                        enabledDateListIsEmpty = enabledDateList.isEmpty()
                    )
                }

                //date list
                items(enabledDateList) { date ->

                    key(enabledDateList.map { it.id }) {
                        val slideState = slideStates[date.id] ?: SlideState.NONE

                        AnimatedVisibility(
                            visible = !loadingTrip || !isFirstLoading,
                            enter =  expandVertically(tween(500)),
                            exit = shrinkVertically(tween(500))
                        ) {
                            DateListItem(
                                trip = showingTrip,
                                date = date,
                                isEditMode = isEditMode,
                                isHighlighted = date.index == currentDateIndex && use2Panes,
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
                                        tripErrorCount.increaseTotalErrorCount()
                                        tripErrorCount.increaseDateTitleErrorCount()
                                    } else {
                                        tripErrorCount.decreaseTotalErrorCount()
                                        tripErrorCount.decreaseDateTitleErrorCount()
                                    }
                                },
                                onClickItem =
                                if (!isEditMode){
                                    { tripNavigate.navigateToDate(date.index) }
                                }
                                else null,
                                onClickSideText = null,
                                onClickPoint =
                                    if (isEditMode) {
                                        {
                                            tripDialog.setSelectedDate(date)
                                            tripDialog.setShowSetColorDialog(true)
                                        }
                                    } else null
                            )
                        }
                    }
                }

                item {
                    StartEndDummySpaceWithRoundedCorner(isFirst = false, isLast = true)
                }
            }
        }
    }
}