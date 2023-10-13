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
import androidx.compose.material3.FabPosition
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
        if (isEditMode) stringResource(id = R.string.top_bar_title_edit_date)
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

                        setShowBottomSaveCancelBar = {
                            showBottomSaveCancelBar = it
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
                }
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

    setShowBottomSaveCancelBar: (Boolean) -> Unit,
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
                setShowBottomSaveCancelBar = setShowBottomSaveCancelBar,
                focusManager = focusManager,
                onTitleChange = {newTitleText ->
                    currentDate.setTitleText(showingTrip, updateTripState, newTitleText)
                    Log.d("page", "title ${currentDate.id}")
                },
                onTextSizeLimit = {
                    showSnackBar("over 100", null, SnackbarDuration.Short)
                },
                color = currentDate.color,
                modifier = modifier,
                onColorChange = {newDateColor ->
                    currentDate.setColor(showingTrip, updateTripState, newDateColor)
                    Log.d("page", "               color ${currentDate.id}")
                }
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
                onTextSizeLimit = {
                    showSnackBar("Memo is too long", null, SnackbarDuration.Short)
                }
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
                                        text = "Spots",
                                        style = getTextStyle(TextType.CARD__TITLE),
                                        modifier = textModifier.onGloballyPositioned {
                                            textHeight = it.size.height
                                        }
                                    )
                                }

                                if(firstSpotShowUpperLine)
                                    DummySpaceWithLine(height = (textHeight / LocalDensity.current.density).dp)
                            }
                        }
                    }
                }


                //list with line
                items(spotList) {

                    if (checkSpotTypeGroupIsShown(it.spotType.group, spotTypeWithShownList)) {

                        //set point color
                        val pointColor =
                            if (it.spotType.isMove())
                                Color.Transparent
                            else
                                Color(it.spotType.group.color.color)

                        var isExpanded by rememberSaveable { mutableStateOf(false) }

                        //list item
                        GraphListItem(
                            isEditMode = isEditMode,
                            isExpanded = isExpanded,
                            itemId = it.id,

                            iconText = if (it.spotType.isNotMove()) it.orderId.toString()
                                        else null,
                            iconTextColor = it.spotType.group.color.onColor,

                            sideText = it.getStartTimeText(timeFormat) ?: "",
                            mainText = it.titleText,
                            expandedText = it.getExpandedText(showingTrip, isEditMode),

                            onTitleTextChange = { spotId, spotTitleText ->
                                spotList[spotId].setTitleText(showingTrip, dateId, updateTripState, spotTitleText)
                            },

                            isFirstItem = it.spotType.isNotMove()
                                    && it == spotList.first()
                                    && it.getPrevSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,

                            isLastItem = it.spotType.isNotMove()
                                    && it == spotList.last()
                                    && it.getNextSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,

                            availableDelete = true,

                            onItemClick = { spotId ->
                                navigateToSpot(dateId, spotId)
                            },
                            onExpandedButtonClicked = { id ->
                                isExpanded = !isExpanded
                            },
                            onDeleteClick = { spotId ->
                                //dialog: ask delete
                                deleteSpot(dateId, spotId)
                                spotTypeWithShownList =
                                    updateSpotTypeGroupWithShownList(currentDate, spotTypeWithShownList)

                            },

                            pointColor = pointColor,
                            showLongTextSnackBar = showSnackBar,
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
                    text = "New Spot",
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
            text = "No Plan...",
            style = textStyle
        )
        Text(
            text = "Let's create a new plan!",
            style = textStyle
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
            text = "Choose a Spot Type!",
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