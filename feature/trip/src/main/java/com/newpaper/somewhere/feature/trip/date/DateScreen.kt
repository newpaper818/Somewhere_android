package com.newpaper.somewhere.feature.trip.date

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.FilterButtons
import com.newpaper.somewhere.core.designsystem.component.button.NewItemButton
import com.newpaper.somewhere.core.designsystem.component.button.SeeOnMapExtendedFAB
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.InformationCard
import com.newpaper.somewhere.core.ui.card.trip.MAX_TITLE_LENGTH
import com.newpaper.somewhere.core.ui.card.trip.MemoCard
import com.newpaper.somewhere.core.ui.card.trip.TitleWithColorCard
import com.newpaper.somewhere.core.ui.card.trip.budgetItem
import com.newpaper.somewhere.core.ui.card.trip.travelDistanceItem
import com.newpaper.somewhere.core.ui.tripScreenUtils.DateListProgressBar
import com.newpaper.somewhere.core.ui.tripScreenUtils.DummySpaceWithLine
import com.newpaper.somewhere.core.ui.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getFirstLocation
import com.newpaper.somewhere.core.utils.convert.getNextSpot
import com.newpaper.somewhere.core.utils.convert.getPrevSpot
import com.newpaper.somewhere.core.utils.convert.getSpotTypeGroupCount
import com.newpaper.somewhere.core.utils.convert.getTotalBudgetText
import com.newpaper.somewhere.core.utils.convert.getTotalTravelDistanceText
import com.newpaper.somewhere.core.utils.convert.setColor
import com.newpaper.somewhere.core.utils.convert.setMemoText
import com.newpaper.somewhere.core.utils.convert.setSpotType
import com.newpaper.somewhere.core.utils.convert.setStartTime
import com.newpaper.somewhere.core.utils.convert.setTitleText
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.dialog.memo.MemoDialog
import com.newpaper.somewhere.feature.dialog.setColor.SetColorDialog
import com.newpaper.somewhere.feature.dialog.setSpotType.SetSpotTypeDialog
import com.newpaper.somewhere.feature.dialog.setTime.SetTimeDialog
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.date.component.NoChosenSpotTypeGroupText
import com.newpaper.somewhere.feature.trip.date.component.NoPlanCard
import com.newpaper.somewhere.feature.trip.date.component.SpotListItem
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateRoute(
    use2Panes: Boolean,
    spacerValue: Dp,
    appUserId: String,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    showTripBottomSaveCancelBar: Boolean,

    commonTripViewModel: CommonTripViewModel,

    //navigate
    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    navigateToDateMap: () -> Unit,

    setIsShowingDialog: (isShowingDialog: Boolean) -> Unit,

    //
    modifier: Modifier = Modifier,
    dateViewModel: DateViewModel = hiltViewModel()
){
    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()
    val dateUiState by dateViewModel.dateUiState.collectAsState()

    val originalTrip = commonTripUiState.tripInfo.trip!!
    val tempTrip = commonTripUiState.tripInfo.tempTrip!!
    val isEditMode = commonTripUiState.isEditMode

    val dateIndex = commonTripUiState.tripInfo.dateIndex

    val showingTrip = if (isEditMode) tempTrip
                        else            originalTrip
    val dateList = showingTrip.dateList


    val progressBarState = rememberLazyListState(
        initialFirstVisibleItemIndex = dateIndex ?: 0
    )

    val datePagerState = rememberPagerState(
        initialPage = dateIndex ?: 0,
        pageCount = { dateList.size }
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(!isEditMode) {
        dateViewModel.initAllErrorCount()
    }

    var userSwiping by rememberSaveable { mutableStateOf(true) }

    val userDragTouching by datePagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(userDragTouching){
        if (userDragTouching) userSwiping = true
    }

    //when use2panes, when change dateIndex(click date at trip screen), animate progress bar and date page
    LaunchedEffect(dateIndex){
        if (use2Panes && dateIndex != null && !datePagerState.isScrollInProgress) {
            coroutineScope.launch {
                progressBarState.animateScrollToItem(dateIndex)
            }
            coroutineScope.launch {
                datePagerState.animateScrollToPage(
                    page = dateIndex,
                    animationSpec = tween(400)
                )
            }
            userSwiping = false
        }
    }

    //when current page changed(user swipe), animate progress bar, update current date index
    LaunchedEffect(datePagerState.currentPage){
        if (userSwiping) {
            coroutineScope.launch {
                commonTripViewModel.setCurrentDateIndex(datePagerState.currentPage)
            }
            coroutineScope.launch {
                progressBarState.animateScrollToItem(datePagerState.currentPage)
            }
        }
    }

    LaunchedEffect(dateUiState.isShowingDialog) {
        setIsShowingDialog(dateUiState.isShowingDialog)
    }

    //when back button click
    val onClickBackButton = {
        if (!isEditMode) navigateUp()
        else {
            if (originalTrip != tempTrip)
                dateViewModel.setShowExitDialog(true)
            else {
                commonTripViewModel.setIsEditMode(false)
            }
        }
    }

    BackHandler {
        onClickBackButton()
    }

    var isFABExpanded by rememberSaveable { mutableStateOf(true) }


    DateScreen(
        dateUiInfo = DateUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            isFABExpanded = isFABExpanded,
            isEditMode = isEditMode,
            _setIsEditMode = commonTripViewModel::setIsEditMode
        ),
        dateData = DateData(
            originalTrip = originalTrip,
            tempTrip = tempTrip,
            showingTrip = showingTrip
        ),
        errorCount = DateErrorCount(
            totalErrorCount = dateUiState.totalErrorCount,
            spotTitleErrorCount = dateUiState.spotTitleErrorCount,
            _increaseTotalErrorCount = dateViewModel::increaseTotalErrorCount,
            _decreaseTotalErrorCount = dateViewModel::decreaseTotalErrorCount,
            _increaseSpotTitleErrorCount = dateViewModel::increaseSpotTitleErrorCount,
            _decreaseSpotTitleErrorCount = dateViewModel::decreaseSpotTitleErrorCount,
        ),
        dialog = DateDialog(
            isShowingDialog = commonTripUiState.isShowingDialog,
            showExitDialog = dateUiState.showExitDialog,
            showMemoDialog = dateUiState.showMemoDialog,
            showSetColorDialog = dateUiState.showSetColorDialog,
            showSetTimeDialog = dateUiState.showSetTimeDialog,
            showSetSpotTypeDialog = dateUiState.showSetSpotTypeDialog,
            _setShowExitDialog = dateViewModel::setShowExitDialog,
            _setShowMemoDialog = dateViewModel::setShowMemoDialog,
            _setShowSetColorDialog = dateViewModel::setShowSetColorDialog,
            _setShowSetTimeDialog = dateViewModel::setShowSetTimeDialog,
            _setShowSetSpotTypeDialog = dateViewModel::setShowSetSpotTypeDialog,
            selectedSpot = dateUiState.selectedSpot,
            _setSelectedSpot = dateViewModel::setSelectedSpot
        ),
        navigate = DateNavigate(
            _navigateUp = onClickBackButton,
            _navigateToSpot = navigateToSpot,
            _navigateToDateMap = navigateToDateMap
        ),
        image = DateImage(
            _addDeletedImages = commonTripViewModel::addDeletedImages,
            _organizeAddedDeletedImages = {
                commonTripViewModel.organizeAddedDeletedImages(showingTrip.managerId, it)
            }
        ),
        showTripBottomSaveCancelBar = showTripBottomSaveCancelBar,
        progressBarState = progressBarState,
        datePagerState = datePagerState,
        updateTripState = commonTripViewModel::updateTripState,
        addNewSpot = commonTripViewModel::addNewSpot,
        deleteSpot = commonTripViewModel::deleteSpot,
        reorderSpotList = commonTripViewModel::reorderSpotListAndUpdateTravelDistance,
        onClickSave = {
            coroutineScope.launch {
                if (originalTrip != tempTrip){
                    //save tripUiState trip
                    commonTripViewModel.saveTrip(appUserId = appUserId)

                    commonTripViewModel.setIsEditMode(false)

                    //save to firestore
                    commonTripViewModel.saveTripAndAllDates(trip = tempTrip)
                }
                else {
                    commonTripViewModel.setIsEditMode(false)
                }

                commonTripViewModel.organizeAddedDeletedImages(
                    tripManagerId = tempTrip.managerId,
                    isClickSave = true
                )
            }
        },
        onClickProgressBarDateItem = { toDateIndex ->
            userSwiping = false
            coroutineScope.launch {
                commonTripViewModel.setCurrentDateIndex(toDateIndex)
            }

            //progress bar animate
            coroutineScope.launch {
                progressBarState.animateScrollToItem(toDateIndex)
            }

            //page animate
            coroutineScope.launch {
                datePagerState.animateScrollToPage(
                    page = toDateIndex,
                    animationSpec = tween(400)
                )
            }
        },
        setIsFABExpanded = {
            isFABExpanded = it
        },
        modifier = modifier
    )
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DateScreen(
    dateUiInfo: DateUiInfo,
    dateData: DateData,
    errorCount: DateErrorCount,
    dialog: DateDialog,
    navigate: DateNavigate,
    image: DateImage,

    showTripBottomSaveCancelBar: Boolean,

    progressBarState: LazyListState,
    datePagerState: PagerState,


    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    reorderSpotList: (dateIndex: Int, currentIndex: Int, destinationIndex: Int) -> Unit,
    onClickSave: () -> Unit,
    onClickProgressBarDateItem: (dateIndex: Int) -> Unit,

    setIsFABExpanded: (Boolean) -> Unit,

    modifier: Modifier = Modifier,
){
    val startSpacerValue = if (dateUiInfo.use2Panes) dateUiInfo.spacerValue / 2
                            else dateUiInfo.spacerValue
    val endSpacerValue = dateUiInfo.spacerValue

    val focusManager = LocalFocusManager.current

    val showingTrip = if (dateUiInfo.isEditMode) dateData.tempTrip
                        else            dateData.originalTrip

    val dateList = showingTrip.dateList
    val enabledDateList = showingTrip.dateList.filter { it.enabled }





    val currentDate = dateData.showingTrip.dateList.getOrNull(datePagerState.currentPage)

    val snackBarHostState = remember { SnackbarHostState() }




    //set top bar title
    val topBarTitle =
        if (dateUiInfo.isEditMode) stringResource(id = R.string.edit_date)
        else if (dateUiInfo.use2Panes) ""
        else {
            if (showingTrip.titleText == null || showingTrip.titleText == "")
                stringResource(id = R.string.no_title)
            else
                showingTrip.titleText!!
        }








    MyScaffold(
        modifier = modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding(),
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
                startPadding = startSpacerValue,
                title = topBarTitle,
                internetEnabled = dateUiInfo.internetEnabled,

                //back button
                navigationIcon = if (!dateUiInfo.use2Panes) TopAppBarIcon.back else null,
                onClickNavigationIcon = { navigate.navigateUp() },

                actionIcon2 = if (!dateUiInfo.isEditMode && showingTrip.editable) TopAppBarIcon.edit else null,
                actionIcon2Onclick = {
                    dateUiInfo.setIsEditMode(true)
                }
            )
        },

        //bottom floating button: see on map
        floatingActionButton = {
            SeeOnMapExtendedFAB(
                visible = !dateUiInfo.isEditMode && showingTrip.getFirstLocation() != null,
                onClick = navigate::navigateToDateMap,
                expanded = dateUiInfo.isFABExpanded
            )
        },

        //bottom save cancel bar
        bottomSaveCancelBarVisible = dateUiInfo.isEditMode && !dialog.isShowingDialog && showTripBottomSaveCancelBar,
        onClickCancel = {
            focusManager.clearFocus()
            navigate.navigateUp()
        },
        onClickSave = onClickSave,
        saveEnabled = errorCount.totalErrorCount <= 0

    ) { paddingValues ->

        //dialogs
        if(dialog.showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { dialog.setShowExitDialog(false) },
                onClickDelete = {
                    dialog.setShowExitDialog(false)
                    dateUiInfo.setIsEditMode(false)
                    updateTripState(true, dateData.originalTrip)

                    image.organizeAddedDeletedImages(false)
                }
            )
        }

        if (dialog.showMemoDialog && currentDate != null){
            MemoDialog(
                memoText = currentDate.memo ?: "",
                onDismissRequest = { dialog.setShowMemoDialog(false) }
            )
        }

        if (dialog.showSetColorDialog && currentDate != null){
            SetColorDialog(
                initialColor = currentDate.color,
                onDismissRequest = {
                    dialog.setShowSetColorDialog(false)
                },
                onOkClick = {
                    dialog.setShowSetColorDialog(false)
                    currentDate.setColor(dateData.showingTrip, updateTripState, it)
                }
            )
        }

        if (dialog.showSetTimeDialog && dialog.selectedSpot != null){
            SetTimeDialog(
                initialTime = dialog.selectedSpot.startTime ?: LocalTime.of(12,0),
                timeFormat = dateUiInfo.dateTimeFormat.timeFormat,
                isSetStartTime = true,
                onDismissRequest = {
                    dialog.setShowSetTimeDialog(false)
                    dialog.setSelectedDate(null)
                },
                onConfirm = {newTime ->
                    dialog.selectedSpot.setStartTime(showingTrip, datePagerState.currentPage, updateTripState, newTime)
                    dialog.setShowSetTimeDialog(false)
                    dialog.setSelectedDate(null)
                }
            )
        }

        if (dialog.showSetSpotTypeDialog && dialog.selectedSpot != null) {
            SetSpotTypeDialog(
                initialSpotType = dialog.selectedSpot.spotType,
                onDismissRequest = {
                    dialog.setShowSetSpotTypeDialog(false)
                    dialog.setSelectedDate(null)
                },
                onClickOk = { newSpotType ->
                    dialog.setShowSetSpotTypeDialog(false)
                    dialog.selectedSpot.setSpotType(
                        showingTrip,
                        dateList,
                        datePagerState.currentPage,
                        updateTripState,
                        newSpotType
                    )
                    dialog.setSelectedDate(null)
                }
            )
        }







        //show when use 2 panes and no date
        AnimatedVisibility(
            visible = enabledDateList.isEmpty(),
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(800))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.no_date),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        AnimatedVisibility(
            visible = enabledDateList.isNotEmpty(),
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(800))
        ) {
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
                        startSpacerValue = startSpacerValue,
                        endSpacerValue = endSpacerValue,
                        progressBarState = progressBarState,
                        dateList = enabledDateList,
                        currentDateIdx = datePagerState.currentPage,
                        dateTimeFormat = dateUiInfo.dateTimeFormat,
                        onClickDate = onClickProgressBarDateItem
                    )

                    MySpacerColumn(height = 16.dp)

                    //spot pages
                    //pageIndex == dateOrderId
                    HorizontalPager(
                        state = datePagerState,
                        modifier = Modifier.weight(1f),
                        pageContent = { pageIndex ->
                            //pageIndex == dateOrderId

                            DatePage(
                                dateUiInfo = dateUiInfo,
                                dateData = dateData,
                                errorCount = errorCount,
                                dialog = dialog,
                                navigate = navigate,
                                dateIndex = pageIndex,
                                focusManager = focusManager,
                                updateTripState = updateTripState,
                                addNewSpot = { dateIndex->
                                    addNewSpot(dateIndex)
                                },
                                deleteSpot = { dateIndex, spotIndex ->
                                    deleteSpot(dateIndex, spotIndex)
                                    image.addDeletedImages(dateList[dateIndex].spotList[spotIndex].imagePathList)
                                },
                                setIsFABExpanded = setIsFABExpanded,
                                reorderSpotList = { currentIndex, destinationIndex ->
                                    reorderSpotList(pageIndex, currentIndex, destinationIndex)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}









@Composable
private fun DatePage(
    dateUiInfo: DateUiInfo,
    dateData: DateData,
    errorCount: DateErrorCount,
    dialog: DateDialog,
    navigate: DateNavigate,

    dateIndex: Int,
    focusManager: FocusManager,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateIndex: Int) -> Unit,
    deleteSpot: (dateId: Int, spotId: Int) -> Unit,
    setIsFABExpanded: (Boolean) -> Unit,

    reorderSpotList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier
){
    val use2Panes = dateUiInfo.use2Panes
    val spacerValue = dateUiInfo.spacerValue

    val timeFormat = dateUiInfo.dateTimeFormat.timeFormat
    val isEditMode = dateUiInfo.isEditMode

    val startSpacerValue = if (use2Panes) spacerValue / 2 else spacerValue
    val endSpacerValue = spacerValue * 1

    val showingTrip = dateData.showingTrip
    val dateList = dateData.showingTrip.dateList
    val currentDate = dateList[dateIndex]
    val spotList = dateList[dateIndex].spotList

    val coroutineScope = rememberCoroutineScope()

    var spotTypeWithShownList by rememberSaveable{
        mutableStateOf(getSpotTypeGroupWithShownList(currentDate))
    }

    //slideStates
    val slideStates = remember { mutableStateMapOf(
        *showingTrip.dateList[dateIndex].spotList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    val scrollState = rememberLazyListState()

    val firstItemVisible by remember { derivedStateOf { scrollState.firstVisibleItemIndex == 0 } }

    LaunchedEffect(firstItemVisible){
        setIsFABExpanded(firstItemVisible)
    }



    //FIXME spotTypeWithShownList order problem
    //왜 순서가 바뀌지?? follow order of spotList

    LazyColumn(
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(0.dp, 8.dp, 0.dp, 200.dp),
        modifier = modifier.fillMaxSize()
    ) {
        //title card
        item {
            TitleWithColorCard(
                modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
                isEditMode = isEditMode,

                titleText = currentDate.titleText,
                onTitleChange = {newTitleText ->
                    currentDate.setTitleText(showingTrip, updateTripState, newTitleText)
                },
                focusManager = focusManager,
                isLongText = {
                    if (it) errorCount.increaseTotalErrorCount()
                    else errorCount.decreaseTotalErrorCount()
                },
                color = currentDate.color,
                onClickColorCard = {
                    dialog.setShowSetColorDialog(true)
                }
            )
        }

        //budget, travel distance info card
        item {
            AnimatedVisibility(
                visible = !isEditMode,
                enter = scaleIn(animationSpec = tween(300))
                        + expandVertically(animationSpec = tween(300))
                        + fadeIn(animationSpec = tween(300)),
                exit = scaleOut(animationSpec = tween(300))
                        + shrinkVertically(animationSpec = tween(300))
                        + fadeOut(animationSpec = tween(300))
            ) {
                Column {
                    InformationCard(
                        modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
                        isEditMode = false,
                        list = listOf(
                            budgetItem.copy(text = currentDate.getTotalBudgetText(showingTrip, showingTrip.unitOfCurrencyType.numberOfDecimalPlaces)),
                            travelDistanceItem.copy(text = currentDate.getTotalTravelDistanceText(2))
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        //memo card
        item {
            MemoCard(
                modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
                isEditMode = isEditMode,
                memoText = currentDate.memo,
                onMemoChanged = { newMemoText ->
                    currentDate.setMemoText(showingTrip, updateTripState, newMemoText)
                },
                isLongText = {
                    if (it) errorCount.increaseTotalErrorCount()
                    else errorCount.decreaseTotalErrorCount()
                },
                showMemoDialog = {
                    dialog.setShowMemoDialog(true)
                }
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
                FilterButtons(
                    startPadding = startSpacerValue,
                    endPadding = endSpacerValue,
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
                        modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
                        showLine = firstSpotShowUpperLine,
                        isFirst = true,
                        isLast = false
                    )
                }

                //spots title when edit mode
                item {
                    val textModifier =
                        if (showingTrip.dateList.isNotEmpty())
                            Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
                        else Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)

                    var textHeight by rememberSaveable { mutableIntStateOf(0) }

                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = expandVertically(tween(400)),
                        exit = shrinkVertically(tween(400))
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceBright)
                        ) {
                            Box {
                                Row(
                                    modifier = Modifier.padding(16.dp, 0.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.spots),
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        modifier = textModifier.onGloballyPositioned {
                                            textHeight = it.size.height
                                        }
                                    )

                                    //up to 100 characters
                                    if (errorCount.spotTitleErrorCount > 0){
                                        Spacer(modifier = Modifier.weight(1f))

                                        Text(
                                            text = stringResource(id = R.string.long_text, MAX_TITLE_LENGTH),
                                            style = MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError)
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
                    key(spotList.map { it.id }){
                        val slideState = slideStates[spot.id] ?: SlideState.NONE

                        if (checkSpotTypeGroupIsShown(spot.spotType.group, spotTypeWithShownList)) {

                            SpotListItem(
                                modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
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

                                        //reorder list & update travel distance
                                        reorderSpotList(currentIndex, destinationIndex)

                                        //all slideState to NONE
                                        slideStates.putAll(spotList.map { it.id }
                                            .associateWith { SlideState.NONE })
                                    }
                                },
                                onTitleTextChange = { spotTitleText ->
                                    spotList[spot.index].setTitleText(showingTrip, dateIndex, updateTripState, spotTitleText)
                                },
                                isLongText = {
                                    if (it) {
                                        errorCount.increaseTotalErrorCount()
                                        errorCount.increaseSpotTitleErrorCount()
                                    } else {
                                        errorCount.decreaseTotalErrorCount()
                                        errorCount.decreaseSpotTitleErrorCount()
                                    }
                                },
                                onClickItem =
                                if (!isEditMode){
                                    {
                                        navigate.navigateToSpot(dateIndex, spotList.indexOf(spot))
                                    }
                                }
                                else null,
                                onClickDelete = {
                                    //dialog: ask delete
                                    deleteSpot(dateIndex, spot.index)
                                    spotTypeWithShownList =
                                        updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)
                                },
                                onClickSideText =
                                if (isEditMode){
                                    {
                                        dialog.setSelectedDate(spot)
                                        dialog.setShowSetTimeDialog(true)
                                    }
                                }
                                else null,
                                onClickPoint =
                                if (isEditMode){
                                    {
                                        dialog.setSelectedDate(spot)
                                        dialog.setShowSetSpotTypeDialog(true)
                                    }
                                }
                                else null
                            )
                        }
                    }
                }

                item {
                    StartEndDummySpaceWithRoundedCorner(
                        modifier = Modifier.padding(startSpacerValue, 0.dp, endSpacerValue, 0.dp),
                        showLine = lastSpotShowLowerLine,
                        isFirst = false,
                        isLast = true,
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
                NoPlanCard()
            }
        }

        //new spot button
        if(isEditMode && spotList.size < 100){
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
//            list.add(Pair(SpotTypeGroup.TOUR, true))
            Log.d("qqq", "-${spot.spotType} ${spot.spotType.group}")
            Log.d("qqq", "-${SpotType.TOUR.group}")
            Log.d("qqq", "-${SpotTypeGroup.TOUR}")
            //FIXME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!<<<<<<<<<<<<<-------------------------------
            //date screen ㅇㅔ서 date2에서 spot 순서 변경 하고 (spot type 다른 두 spot)
            //date1에서 cancel 누르고 exit 누르면 infinite loop
        }
    }

    Log.d("qqq", "---$list")

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

private fun checkSpotTypeGroupIsShown(
    spotTypeGroup: SpotTypeGroup,
    spotTypeGroupWithShownList: List<Pair<SpotTypeGroup, Boolean>>
): Boolean {
    for (spotWithShown in spotTypeGroupWithShownList){
        if (spotWithShown.first == spotTypeGroup  && spotWithShown.second)
            return true
        else if (spotWithShown.first == spotTypeGroup)
            return false
    }
    return false
}
