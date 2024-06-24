package com.newpaper.somewhere.feature.trip.trip

import android.net.Uri
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
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.SeeOnMapExtendedFAB
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.ImageCard
import com.newpaper.somewhere.core.ui.card.trip.InformationCard
import com.newpaper.somewhere.core.ui.card.trip.MAX_TITLE_LENGTH
import com.newpaper.somewhere.core.ui.card.trip.MemoCard
import com.newpaper.somewhere.feature.trip.trip.component.SharingWithFriendsCard
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
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.dialog.setColor.SetColorDialog
import com.newpaper.somewhere.feature.dialog.setCurrencyType.SetCurrencyTypeDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trip.component.DateListItem
import com.newpaper.somewhere.feature.trip.trip.component.TripDurationCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun TripRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    appUserId: String,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    commonTripViewModel: CommonTripViewModel,

    isNewTrip: Boolean,

    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToInvitedFriends: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateToDate: (dateIndex: Int) -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    saveTrip: () -> Unit,

    modifier: Modifier = Modifier,
    currentDateIndex: Int? = null,

    tripViewModel: TripViewModel = hiltViewModel(),
){
    val coroutineScope = rememberCoroutineScope()

    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()
    val tripUiState by tripViewModel.tripUiState.collectAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showSetCurrencyDialog by rememberSaveable { mutableStateOf(false) }

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


    val onBackButtonClick = {
        if (originalTrip != tempTrip)
            showExitDialog = true
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
            onBackButtonClick()
        }

    TripScreen(
        use2Panes = use2Panes,
        startSpacerValue = spacerValue,
        endSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue,
        appUserId = appUserId,
        isEditMode = isEditMode,
        dateTimeFormat = dateTimeFormat,
        internetEnabled = internetEnabled,
        tripUiState = tripUiState,
        originalTrip = originalTrip,
        tempTrip = tempTrip,
        isNewTrip = isNewTrip,
        showExitDialog = showExitDialog,
        showSetCurrencyDialog = showSetCurrencyDialog,
        setEditMode = commonTripViewModel::setIsEditMode,
        setShowExitDialog = { showExitDialog = it },
        setShowSetCurrencyDialog = { showSetCurrencyDialog = it },
        navigateUp = {
            if (!isEditMode) navigateUp()
            else onBackButtonClick()
        },
        navigateToInviteFriend = navigateToInviteFriend,
        navigateToInvitedFriends = navigateToInvitedFriends,
        navigateToImage = navigateToImage,
        navigateToDate = navigateToDate,
        navigateToTripMap = navigateToTripMap,
        navigateUpAndDeleteNewTrip = navigateUpAndDeleteNewTrip,
        updateTripState = updateTripState,
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
        saveImageToInternalStorage = { index, uri ->
            commonTripViewModel.saveImageToInternalStorage(originalTrip.id, index, uri)
        },
        downloadImage = commonTripViewModel::getImage,
        addAddedImages = addAddedImages,
        addDeletedImages = addDeletedImages,
        organizeAddedDeletedImages = organizeAddedDeletedImages,
        reorderTripImageList = { currentIndex, destinationIndex ->
            tripViewModel.reorderTripImageList(currentIndex, destinationIndex)
        },
        reorderDateList = { currentIndex, destinationIndex ->
            tripViewModel.reorderDateList(currentIndex, destinationIndex)
        },
        saveTrip = saveTrip,
        modifier = modifier,
        currentDateIndex = currentDateIndex
    )
}


@Composable
private fun TripScreen(
    use2Panes: Boolean,
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    appUserId: String,
    isEditMode: Boolean,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    tripUiState: TripUiState,

    originalTrip: Trip,
    tempTrip: Trip,
    isNewTrip: Boolean,

    showExitDialog: Boolean,
    showSetCurrencyDialog: Boolean,

    setEditMode: (editMode: Boolean?) -> Unit,

    setShowExitDialog: (Boolean) -> Unit,
    setShowSetCurrencyDialog: (Boolean) -> Unit,

    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToInvitedFriends: () -> Unit,
    navigateToImage: (imageList: List<String>, initialImageIndex: Int) -> Unit,
    navigateToDate: (dateIndex: Int) -> Unit,
    navigateToTripMap: () -> Unit,
    navigateUpAndDeleteNewTrip: (deleteTrip: Trip) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    updateTripDurationAndTripState: (toTempTrip: Boolean, startDate: LocalDate, endDate: LocalDate) -> Unit,

    saveImageToInternalStorage: (index: Int, uri: Uri) -> String?,
    downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit,
    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,
    reorderTripImageList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    reorderDateList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    saveTrip: () -> Unit,

    modifier: Modifier = Modifier,
    currentDateIndex: Int? = null,
//    setShowTripBottomSaveCancelBar: (Boolean) -> Unit = {}
){
    val coroutineScope = rememberCoroutineScope()

    val showingTrip =
        if (isEditMode) tempTrip
        else originalTrip

    val enabledDateList = showingTrip.dateList.filter { it.enabled }
    val isFirstLoading by rememberSaveable { mutableStateOf(enabledDateList.isEmpty()) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showBottomSaveCancelBar by rememberSaveable { mutableStateOf(true) }

    var errorCount by rememberSaveable { mutableIntStateOf(0) }
    var dateTitleErrorCount by rememberSaveable { mutableIntStateOf(0) }

    //slideStates
    val slideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingTrip.dateList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }



    //set top bar title
    var topBarTitle by rememberSaveable { mutableStateOf("") }

    topBarTitle =
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

                navigationIcon = TopAppBarIcon.back,
                navigationIconOnClick = {
                    navigateUp()
                },

                actionIcon2 = if (!tripUiState.loadingTrip && !isEditMode && !use2Panes && showingTrip.editable) TopAppBarIcon.edit else null,
                actionIcon2Onclick = {
                    setEditMode(null)
                }
            )
        },

        //bottom floating button
        floatingActionButton = {
            if (!use2Panes)
                SeeOnMapExtendedFAB(
                    visible = !isEditMode && showingTrip.getFirstLocation() != null,
                    onClick = navigateToTripMap,
                    expanded = isFABExpanded
                )
        },

        //bottom save cancel button
        bottomSaveCancelBarVisible = isEditMode && showBottomSaveCancelBar && !use2Panes,
        onCancelClick = {
            focusManager.clearFocus()
            navigateUp()
        },
        onSaveClick = {
            coroutineScope.launch {
                if (isNewTrip || originalTrip != tempTrip)
                //save to firestore
                    saveTrip()
                else {
                    setEditMode(false)
                }

                organizeAddedDeletedImages(true)
            }
        },
        saveEnabled = errorCount <= 0

    ) { paddingValues ->

        //dialogs
        if (showExitDialog) {
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { setShowExitDialog(false) },
                onDeleteClick = {
                    errorCount = 0
                    setShowExitDialog(false)
                    setEditMode(false)
                    updateTripState(true, originalTrip)

                    if (isNewTrip) {
                        navigateUpAndDeleteNewTrip(originalTrip)
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
                    setShowSetCurrencyDialog(false)
                    showBottomSaveCancelBar = true
//                    setShowTripBottomSaveCancelBar(true)
                },
                onDismissRequest = {
                    setShowSetCurrencyDialog(false)
                    showBottomSaveCancelBar = true
//                    setShowTripBottomSaveCancelBar(true)
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
                contentPadding = PaddingValues(startSpacerValue, 16.dp, endSpacerValue, 200.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                item {
                    SharingWithFriendsCard(
                        trip = showingTrip,
                        userIsManager = appUserId == showingTrip.managerId,
                        internetEnabled = internetEnabled,
                        isEditMode = isEditMode,
                        onClickInvitedFriends = navigateToInvitedFriends,
                        onClickShareTrip = navigateToInviteFriend
                    )
                }

                //title card
                item {
//                    val snackBarLongText = stringResource(id = R.string.long_text, 100)

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
                        imageUserId = showingTrip.managerId,
                        internetEnabled = internetEnabled,
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
                        reorderImageList = reorderTripImageList,
                        downloadImage = downloadImage,
                        saveImageToInternalStorage = saveImageToInternalStorage
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
                            showingTrip.dateList.first().date..
                                    showingTrip.dateList[endDateIndex?: 0].date

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
//                            setShowTripBottomSaveCancelBar(it)
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
                            currencyItem.copy(
                                text = showingTrip.getTotalBudgetText(),
                                onClick = {
                                    setShowSetCurrencyDialog(true)
                                    showBottomSaveCancelBar = false
//                                    setShowTripBottomSaveCancelBar(false)
                                }),
                            travelDistanceItem.copy(text =  showingTrip.getTotalTravelDistanceText())
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                //memo card
                item {
//                    val snackBarLongText = stringResource(id = R.string.long_text, 100)

                    MemoCard(
                        isEditMode = isEditMode,
                        memoText = showingTrip.memoText,
                        onMemoChanged = { newMemoText ->
                            showingTrip.setMemoText(updateTripState, newMemoText)
                        },
                        isLongText = {
                            if (it) errorCount++
                            else errorCount--
                        },
                        showMemoDialog = {
                            //TODO
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
                                .background(MaterialTheme.colorScheme.surfaceBright)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp, 0.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.dates),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
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
                                        style = MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError)
                                    )
                                }
                            }
                        }
                    }
                }

                if (enabledDateList.isNotEmpty()) {
                    items(enabledDateList) { date ->

                        var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }

                        if (showColorPickerDialog) {
                            SetColorDialog(
                                initialColor = date.color,
                                onDismissRequest = {
                                    showColorPickerDialog = false
                                    showBottomSaveCancelBar = true
//                                    setShowTripBottomSaveCancelBar(true)
                                },
                                onOkClick = {
                                    showColorPickerDialog = false
                                    showBottomSaveCancelBar = true
//                                    setShowTripBottomSaveCancelBar(true)
                                    date.setColor(showingTrip, updateTripState, it)
                                }
                            )
                        }


                        key(enabledDateList.map { it.id }) {
                            val slideState = slideStates[date.id] ?: SlideState.NONE

                            AnimatedVisibility(
                                visible = !tripUiState.loadingTrip || !isFirstLoading,
                                enter = expandVertically(tween(500)),
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

                                    isTextSizeLimit = false,
//                                    isLongText = {
//                                        if (it) {
//                                            errorCount++
//                                            dateTitleErrorCount++
//                                        } else {
//                                            errorCount--
//                                            dateTitleErrorCount--
//                                        }
//                                    },
                                    onItemClick = {
                                        navigateToDate(date.index)
                                    },
                                    onPointClick =
                                    if (isEditMode) {
                                        {
                                            showBottomSaveCancelBar = false
//                                            setShowTripBottomSaveCancelBar(false)
                                            showColorPickerDialog = true
                                        }
                                    } else null
                                )
                            }
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

                        MyCard(
                            shape = RectangleShape,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp, 0.dp)
                            ) {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
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