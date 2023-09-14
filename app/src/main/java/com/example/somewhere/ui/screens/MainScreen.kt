package com.example.somewhere.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.somewhere.R
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.DeleteOrNotDialog
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.cards.DisplayImage
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.NewItemButton
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.utils.SlideState
import com.example.somewhere.utils.calculateNumberOfSlideItems
import com.example.somewhere.utils.dragHandle
import com.example.somewhere.viewModel.AppViewModel
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.roundToInt
import kotlin.math.sign

object MainDestination : NavigationDestination {
    override val route = "main"
    override var title = "Somewhere"
}

@Composable
fun MainScreen(
    isEditMode: Boolean,

//    originalTripList: List<Trip>,
//    tempTripList: List<Trip>,

    changeEditMode: (editMode: Boolean?) -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,

    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
){
    val appUiState by appViewModel.appUiState.collectAsState()
    val originalTripList = appUiState.tripList ?: listOf()
    val tempTripList = appUiState.tempTripList ?: listOf()

    val showingTripList =
        if (isEditMode) tempTripList
        else            originalTripList

    Log.d("test2", "----------------------showingTripList: ${showingTripList.map { it.titleText }}")

    val coroutineScope = rememberCoroutineScope()

    //TODO move to viewModel?
    val slideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingTripList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBackButtonClick = {
        if (originalTripList != tempTripList)
            showExitDialog = true
        else
            changeEditMode(false)
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }

    Scaffold (
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                isEditMode = isEditMode,
                title = if (!isEditMode) MainDestination.title
                        else stringResource(id = R.string.top_bar_title_edit_trips),

                navigationIcon = MyIcons.menu,
                navigationIconOnClick = {/*TODO*/},

                actionIcon1 = if (!isEditMode) MyIcons.edit
                                else null,
                actionIcon1Onclick = {
                    changeEditMode(null)
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
                    appViewModel.updateTripListState(true, originalTripList)
                }
            )
        }

        //display trips list
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 200.dp),
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                if (showingTripList.isNotEmpty()) {
                    Log.d("test2", "=============${showingTripList.map { it.titleText }}")

                    items(showingTripList){ trip ->

                        //delete trip dialog
                        var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                        if (showDeleteDialog) {
                            DeleteOrNotDialog(
                                bodyText = stringResource(id = R.string.dialog_body_delete_trip),
                                deleteText = stringResource(id = R.string.dialog_button_delete),
                                onDismissRequest = { showDeleteDialog = false },
                                onDeleteClick = {
                                    coroutineScope.launch {
                                        appViewModel.deleteTempTrip(trip)
                                    }
                                    showDeleteDialog = false
                                }
                            )
                        }

                        //each trip item
                        key(trip){
                            val slideState = slideStates[trip.id] ?: SlideState.NONE

                            if (trip == showingTripList.first())
                                Log.d("test", "--------------------------------")

                            Log.d("test", "draw item-${trip.titleText}-${trip.id}-${trip.orderId}")
                            //Log.d("test2", "[[[ ${trip.titleText} - ${showingTripList.map { it.titleText }}")

                            TripListItem(
                                trip = trip,
                                tripList = showingTripList.toMutableList(),
                                isEditMode = isEditMode,
                                onTripClick = { trip ->
                                    navigateToTrip(false, trip)
                                },
                                deleteTrip = {
                                    showDeleteDialog = true
                                },
                                slideState = slideState,
                                updateSlideState = { trip_, slideState_ ->
//                                    Log.d("test2", "showingTripLIst: ${showingTripList.map { it.titleText }}")
//                                    Log.d("test2", "${trip_.titleText} $slideState_")
                                    slideStates[trip_.id] = slideState_
                                },
                                updateItemPosition = { currentIndex_, destinationIndex_ ->
                                    //at on drag end
                                    Log.d("test2", "***************************")

                                    Log.d("test2", "before: ${showingTripList.map { it.titleText }}")
                                    coroutineScope.launch {
                                        Log.d("test1", "--------- reorder trip list")
                                        appViewModel.reorderTempTripList(currentIndex_, destinationIndex_)

//                                        Log.d("test2", "- showingTripList: ${appViewModel.appUiState.value.tempTripList?.map { it.titleText }}")
//                                        Log.d("test2", "- showingTripList: ${showingTripList.map { it.titleText }}")
//                                        Log.d("test2", "- showingTripList: ${tempTripList.map { it.titleText }}")
                                        Log.d("test2", "1after: ${appViewModel.appUiState.value.tempTripList?.map { it.titleText }}")
                                        Log.d("test2", "2after: ${appUiState.tempTripList?.map { it.titleText }}")
                                        Log.d("test2", "3after: ${showingTripList.map { it.titleText }}")
                                    }


                                    coroutineScope.launch {
                                        Log.d("test1", "--------- slide state all none")
                                        slideStates.putAll(showingTripList.map { trip -> trip.id }.associateWith { SlideState.NONE })
                                    }
                                }
                            )
                        }

                        MySpacerColumn(height = 16.dp)
                    }
                } else {
                    //TODO prettier img / text
                    item {
                        Text(
                            text = stringResource(id = R.string.no_trip),
                            style = getTextStyle(TextType.CARD__BODY)
                        )
                        MySpacerColumn(height = 16.dp)
                    }
                }

                //new trip button
                item {
                    MySpacerColumn(height = 16.dp)

                    NewItemButton(
                        text = stringResource(id = R.string.new_trip),
                        onClick = {
                            //add new trip view model
                            coroutineScope.launch {
                                val newTrip = appViewModel.addAndGetNewTrip()

                                if (newTrip != null) {
                                    navigateToTrip(true, newTrip)
                                    changeEditMode(true)
                                } else
                                    Log.d(
                                        "debug",
                                        "New Trip Button onClick - navigate to new trip - Can't find new trip"
                                    )
                            }
                        }
                    )
                }

                //TODO remove item{} before release!
                item {
                    MySpacerColumn(height = 60.dp)
                    Test()
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
                        onBackButtonClick()
                    },
                    onSaveClick = {
                        coroutineScope.launch {
                            appViewModel.updateTempToRepository {
                                changeEditMode(false)
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TripListItem(
    trip: Trip,
    tripList: MutableList<Trip>,
    isEditMode: Boolean,
    onTripClick: (Trip) -> Unit,
    deleteTrip: (Trip) -> Unit,

    slideState: SlideState,
    updateSlideState: (trip: Trip, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE),
    titleNullTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE_NULL),
    subtitleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__SUBTITLE)
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val titleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                    else trip.titleText
    val titleTextStyle1 = if (trip.titleText == null || trip.titleText == "") titleNullTextStyle
                            else titleTextStyle

    val subTitleText = trip.getStartEndDateText(true) ?: stringResource(id = R.string.trip_item_no_date)

    val haptic = LocalHapticFeedback.current


    var cardHeightInt: Int
    val cardHeightDp = 120.dp
    var spacerHeightInt: Int
    val spacerHeightDp = 16.dp

    with(LocalDensity.current){
        cardHeightInt = cardHeightDp.toPx().toInt()
        spacerHeightInt = spacerHeightDp.toPx().toInt()
    }


    Log.d("test2", "++++title = ${trip.titleText} | tripList: ${tripList.map { it.titleText }}")

    var isDragged by remember { mutableStateOf(false) }
    val itemOffsetY = remember { Animatable(0f) }


    val zIndex = if (isDragged) 1.0f else 0.0f

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -(cardHeightInt + spacerHeightInt)
            SlideState.DOWN -> cardHeightInt + spacerHeightInt
            else -> 0
        }
    )

    var currentIndex by remember { mutableStateOf(0) }
    var destinationIndex by remember { mutableStateOf(0) }

    //========================================================================
    //card y offset
    val offsetY = remember { Animatable(0f) }

    val itemHeight = cardHeightInt + spacerHeightInt
    val itemIdx = tripList.indexOf(trip)
    val offsetToSlide = itemHeight / 4
    var numberOfItems = 0
    var previousNumberOfItems: Int
    var listOffset = 0

    //on drag start
    val onDragStart = {
        // Interrupt any ongoing animation of other items.
        coroutineScope.launch {
            offsetY.stop()
        }
        isDragged = true
    }

    //on drag
    val onDrag = { change: PointerInputChange ->
        Log.d("test2", "  **on drag item:${trip.titleText} | tripList: ${tripList.map { it.titleText }}")
        //card y offset
        val verticalDragOffset = offsetY.value + change.positionChange().y

        coroutineScope.launch {

            offsetY.snapTo(verticalDragOffset)

            //is offset +(to down) or -(to up)
            val offsetSign = offsetY.value.sign.toInt()

            previousNumberOfItems = numberOfItems
            numberOfItems = calculateNumberOfSlideItems(
                offsetY.value * offsetSign,
                itemHeight,
                offsetToSlide,
                previousNumberOfItems
            )


            if (previousNumberOfItems > numberOfItems) {
                var idx = itemIdx + previousNumberOfItems * offsetSign
                if (idx > tripList.size - 1)    idx = tripList.size - 1
                else if (idx < 0)           idx = 0

                updateSlideState(tripList[idx], SlideState.NONE)
            } else if (numberOfItems != 0) {
                try {
                    //val idx = itemIdx + numberOfItems * offsetSign
//                    Log.d("test2", "=====================================")
//                    Log.d("test2", "tripList: ${tripList.map { it.titleText }}")
                    //Log.d("test2", "idx: $idx | itemIdx: $itemIdx | numberOfItems: $numberOfItems | offsetSign: $offsetSign")

                    updateSlideState(
                        tripList[itemIdx + numberOfItems * offsetSign],
                        if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                    )
                } catch (e: IndexOutOfBoundsException) {
                    numberOfItems = previousNumberOfItems
                    Log.i("DragToReorder", "Item is outside or at the edge")
                }
            }
            listOffset = numberOfItems * offsetSign
        }
        // Consume the gesture event, not passed to external
        change.consume()
    }

    //on drag end
    val onDragEnd = {
        coroutineScope.launch {


            Log.d("test1", "===animate to")
            offsetY.animateTo(itemHeight * numberOfItems * offsetY.value.sign)
//            delay(2000)

//            Log.d("test1", "===isPlaced = true") //x
//            isPlaced = true
//            delay(2000)


            Log.d("test1", "===update idx") //show
            currentIndex = itemIdx
            destinationIndex = itemIdx + listOffset

            if (currentIndex != destinationIndex){
                updateItemPosition(currentIndex, destinationIndex)
            }
//            delay(6000)

            Log.d("test1", "===isDragged = false") //x
            isDragged = false
        }
    }


    val cardColor = if (isDragged)  getColor(ColorType.CARD_ON_DRAG)
                    else            getColor(ColorType.CARD)

    val dragModifier = if (isEditMode) Modifier
        .offset {
//            IntOffset(0, verticalTranslation)

            if (isDragged)
                IntOffset(0, offsetY.value.roundToInt())
            else
                IntOffset(0, verticalTranslation)
        }
        .zIndex(zIndex)
    else Modifier


    Box(modifier = dragModifier) {
        Card(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    //go to trip screen
                    onClick = { if (!isEditMode) onTripClick(trip) },

                    //delete trip
                    onLongClick = {
                        if (isEditMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            deleteTrip(trip)
                        }
                    }
                ),
            backgroundColor = cardColor,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeightDp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //image
                if (trip.imagePathList.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .size(98.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        elevation = 0.dp
                    ) {
                        DisplayImage(imagePath = trip.imagePathList.first())
                    }

                    MySpacerRow(width = 12.dp)
                }

                MySpacerRow(width = 4.dp)

                //text title & trip date
                Column(modifier = Modifier.weight(1f)) {
                    //trip title
                    Text(
                        text = titleText,
                        style = titleTextStyle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    MySpacerColumn(height = 8.dp)

                    //trip start date - end date
                    Text(
                        text = subTitleText,
                        style = subtitleTextStyle
                    )
                }

                MySpacerRow(width = 8.dp)

                //drag handel icon when edit mode
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = scaleIn(tween(300)),
                    exit = scaleOut(tween(400)) + fadeOut(tween(300))
                ) {
                    IconButton(
                        modifier = Modifier
//                            .dragAndDrop(
//                            trip, tripList.toMutableList(), cardHeightInt + spacerHeightInt, updateSlideState,
//                            isDraggedAfterLongPress = false,
//                            setOffsetY = { offsetY ->
////                                coroutineScope.launch {
////                                    itemOffsetY.animateTo(offsetY)
////                                }
//                            },
//                            onStartDrag = {
//                                isDragged = true
//                            },
//                            onStopDrag = { currentIndex_, destinationIndex_ ->
//                                isDragged = false
//                                isPlaced = true
//                                currentIndex = currentIndex_
//                                destinationIndex = destinationIndex_
//                            }
//                          )
                            .dragHandle(
                                isDraggedAfterLongPress = false,
                                onDragStart = { onDragStart() },
                                onDrag = onDrag,
                                onDragEnd = { onDragEnd() }
                            )
                        ,
                        enabled = false,
                        onClick = {  }
                    ) {
                        DisplayIcon(MyIcons.dragHandle)
                    }
                }
            }
        }
    }
}


//test ======================================================================
@Composable
private fun Test(){
    Column {
        TextTest("H1", MaterialTheme.typography.h1)
        TextTest("H2", MaterialTheme.typography.h2)
        TextTest("H3", MaterialTheme.typography.h3)
        TextTest("H4", MaterialTheme.typography.h4)
        TextTest("H5", MaterialTheme.typography.h5)
        TextTest("H6", MaterialTheme.typography.h6)
        TextTest("subtitle1", MaterialTheme.typography.subtitle1)
        TextTest("subtitle2", MaterialTheme.typography.subtitle2)
        TextTest("body1", MaterialTheme.typography.body1)
        TextTest("body2", MaterialTheme.typography.body2)

        Spacer(modifier = Modifier.height(10.dp))

        ColorTest(MaterialTheme.colors.primary,         "primary",          MaterialTheme.colors.onPrimary)
        ColorTest(MaterialTheme.colors.primaryVariant,  "primaryVariant",   MaterialTheme.colors.onPrimary)
        ColorTest(MaterialTheme.colors.secondary,       "secondary",        MaterialTheme.colors.onSecondary)
        ColorTest(MaterialTheme.colors.secondaryVariant,"secondaryVariant", MaterialTheme.colors.onSecondary)
        ColorTest(MaterialTheme.colors.background,      "background",       MaterialTheme.colors.onBackground)
        ColorTest(MaterialTheme.colors.surface,         "surface",          MaterialTheme.colors.onSurface)
        ColorTest(MaterialTheme.colors.error,           "error",            MaterialTheme.colors.onError)
    }
}

@Composable
private fun TextTest(
    text: String,
    textStyle: TextStyle
){
    Text(
        text = "${textStyle.fontSize}   $text",
        style = textStyle
    )
}

@Composable
private fun ColorTest(
    color: Color,
    text: String,
    textColor: Color
){
    Row {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "On Text",
                style = MaterialTheme.typography.body1.copy(color = textColor)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(text = text)
    }
}
