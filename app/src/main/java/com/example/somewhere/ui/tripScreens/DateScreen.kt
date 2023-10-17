package com.example.somewhere.ui.tripScreens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Trip
import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.enumUtils.TimeFormat
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.tripScreenUtils.DateListProgressBar
import com.example.somewhere.ui.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.example.somewhere.ui.tripScreenUtils.DummySpaceWithLine
import com.example.somewhere.ui.tripScreenUtils.GraphListItem
import com.example.somewhere.ui.tripScreenUtils.cards.InformationCard
import com.example.somewhere.ui.tripScreenUtils.cards.MemoCard
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.example.somewhere.ui.tripScreenUtils.NewItemButton
import com.example.somewhere.ui.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.tripScreenUtils.cards.FilterCards
import com.example.somewhere.ui.tripScreenUtils.cards.TitleWithColorCard
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.AnimatedBottomSaveCancelBar
import com.example.somewhere.ui.tripScreenUtils.SeeOnMapExtendedFAB
import com.example.somewhere.ui.tripScreenUtils.cards.MAX_TITLE_LENGTH
import com.example.somewhere.viewModel.DateTimeFormat
import kotlinx.coroutines.launch

object DateDestination : NavigationDestination {
    override val route = "date"
    override var title = "Date screen"
    const val dateIdArg = "dateId"
    val routeWithArgs = "$route/{$dateIdArg}"
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DateScreen(
    isEditMode: Boolean,

    originalTrip: Trip,
    tempTrip: Trip,
    dateId: Int,

    dateTimeFormat: DateTimeFormat,

    changeEditMode: (editMode: Boolean?) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    addNewSpot: (dateId: Int) -> Unit,
    deleteSpot: (dateId: Int, spotId: Int) -> Unit,
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
        initialFirstVisibleItemIndex = dateId
    )

    val datePagerState = rememberPagerState(
        initialPage = dateId
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

                MySpacerColumn(height = 4.dp)

                //progress bar
                DateListProgressBar(
                    progressBarState = progressBarState,
                    dateList = dateList,
                    currentDateIdx = datePagerState.currentPage,
                    dateTimeFormat = dateTimeFormat,
                    onClickDate = { toDateId ->
                        //progress bar animate
                        coroutineScope.launch {
                            progressBarState.animateScrollToItem(toDateId)
                        }

                        //page animate
                        coroutineScope.launch {
                            datePagerState.animateScrollToPage(toDateId)
                        }
                    }
                )

                MySpacerColumn(height = 8.dp)

                //spot pages
                HorizontalPager(
                    pageCount = dateList.size,
                    state = datePagerState,
//                    beyondBoundsPageCount = 1,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->    //pageIndex == dateId

                    DatePage(
                        isEditMode = isEditMode,
                        showingTrip = showingTrip,
                        dateId = pageIndex,
                        currentDate = dateList[pageIndex],
                        timeFormat = dateTimeFormat.timeFormat,

                        focusManager = focusManager,
                        snackBarHostState = snackBarHostState,

                        setShowBottomSaveCancelBar = {
                            showBottomSaveCancelBar = it
                        },
                        onErrorCountChange = { plusError ->
                            if (plusError) errorCount ++
                            else    errorCount --
                            Log.d("err1", "err = $errorCount")
                        },
                        updateTripState = updateTripState,

                        addNewSpot = { dateId ->
                            addNewSpot(dateId)
                        },
                        deleteSpot = { dateId, spotId ->
                            deleteSpot(dateId, spotId)
                            addDeletedImages(dateList[dateId].spotList[spotId].imagePathList)
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
                        modifier = modifier.padding(16.dp, 0.dp)
                    )
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
fun DatePage(
    isEditMode: Boolean,
    showingTrip: Trip,
    dateId: Int,
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

    modifier: Modifier = Modifier
){
    val dateList = showingTrip.dateList
    val spotList = currentDate.spotList

    var spotTypeWithShownList by rememberSaveable{
        mutableStateOf(getSpotTypeGroupWithShownList(currentDate))
    }

    val scrollState = rememberLazyListState()

    var spotTitleErrorCount by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(isEditMode){
        spotTitleErrorCount = 0
    }

    LaunchedEffect(key1 = scrollState.firstVisibleItemIndex == 0 ){
        setIsFABExpanded(scrollState.firstVisibleItemIndex == 0)
    }


    //FIXME spotTypeWithShownList order problem
    //왜 순서가 바뀌지??
    //Log.d("test", "$spotTypeWithShownList")

    LazyColumn(
        state = scrollState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(top = 8.dp, bottom = 300.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        //title card
        item {
            TitleWithColorCard(
                isEditMode = isEditMode,

                titleText = currentDate.titleText,
                onTitleChange = {newTitleText ->
                    currentDate.setTitleText(showingTrip, updateTripState, newTitleText)
                    Log.d("page", "title ${currentDate.id}")
                },
                focusManager = focusManager,
                isLongText = { onErrorCountChange(it) },

                color = currentDate.color,
                onColorChange = {newDateColor ->
                    currentDate.setColor(showingTrip, updateTripState, newDateColor)
                    Log.d("page", "               color ${currentDate.id}")
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
                || spotList[0].getPrevSpot(dateList, spotList, dateId)?.spotType?.isMove() ?: false

            val lastSpotShowLowerLine = spotList.last().spotType.isMove()
                    || spotList[spotList.indexOf(spotList.last())].getNextSpot(dateList, spotList, dateId)?.spotType?.isMove() ?: false

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

                    if (checkSpotTypeGroupIsShown(spot.spotType.group, spotTypeWithShownList)) {

                        //set point color
                        val pointColor =
                            if (spot.spotType.isMove())
                                Color.Transparent
                            else
                                Color(spot.spotType.group.color.color)

                        var isExpanded by rememberSaveable { mutableStateOf(false) }

                        //list item
                        GraphListItem(
                            isEditMode = isEditMode,
                            isExpanded = isExpanded,

                            iconText = if (spot.spotType.isNotMove()) spot.orderId.toString()
                                        else null,
                            iconTextColor = spot.spotType.group.color.onColor,

                            sideText = spot.getStartTimeText(timeFormat) ?: "",
                            mainText = spot.titleText,
                            expandedText = spot.getExpandedText(showingTrip, isEditMode),

                            onTitleTextChange = { spotTitleText ->
                                spotList[spot.id].setTitleText(showingTrip, dateId, updateTripState, spotTitleText)
                            },

                            isFirstItem = spot.spotType.isNotMove()
                                    && spot == spotList.first()
                                    && spot.getPrevSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,

                            isLastItem = spot.spotType.isNotMove()
                                    && spot == spotList.last()
                                    && spot.getNextSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,

                            deleteEnabled = true,
                            dragEnabled = true,

                            onItemClick = {
                                navigateToSpot(dateId, spot.id)
                            },
                            onExpandedButtonClicked = {
                                isExpanded = !isExpanded
                            },
                            onDeleteClick = {
                                //dialog: ask delete
                                deleteSpot(dateId, spot.id)
                                spotTypeWithShownList =
                                    updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)
                            },

                            pointColor = pointColor,
                            isLongText = {
                                if (it) spotTitleErrorCount++
                                else    spotTitleErrorCount--
                                onErrorCountChange(it)
                            },
                            modifier = modifier
                        )
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
                        addNewSpot(dateId)
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