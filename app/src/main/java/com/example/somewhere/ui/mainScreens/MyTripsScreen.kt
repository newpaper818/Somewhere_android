package com.example.somewhere.ui.mainScreens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.somewhere.R
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.tripScreenUtils.dialogs.DeleteOrNotDialog
import com.example.somewhere.ui.tripScreenUtils.cards.DisplayImage
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.commonScreenUtils.SomewhereNavigationBar
import com.example.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.tripScreenUtils.IconFAB
import com.example.somewhere.utils.SlideState
import com.example.somewhere.utils.dragAndDrop
import com.example.somewhere.viewModel.AppViewModel
import com.example.somewhere.viewModel.DateTimeFormat
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object MyTripsDestination : NavigationDestination {
    override val route = "my_trips"
    override var title = ""
}

@Composable
fun MyTripsScreen(
    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,

    changeEditMode: (editMode: Boolean?) -> Unit,

    addAddedImages: (imageFiles: List<String>) -> Unit,
    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateTo: (NavigationDestination) -> Unit,
    navigateToMain: (NavigationDestination) -> Unit,

    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
){
    val appUiState by appViewModel.appUiState.collectAsState()
    val originalTripList = appUiState.tripList ?: listOf()
    val tempTripList = appUiState.tempTripList ?: listOf()

    val showingTripList =
        if (isEditMode) tempTripList
        else            originalTripList

    Log.d("reorder", "----------------------showingTripList: ${showingTripList.map { it.titleText }}")

    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberLazyListState()

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
                title = if (!isEditMode) stringResource(id = R.string.app_name)
                        else stringResource(id = R.string.edit_trips),

                actionIcon1 = if (!isEditMode) MyIcons.edit else null,
                actionIcon1Onclick = {
                    changeEditMode(null)
                }
            )
        },
        bottomBar = {
            if (!isEditMode)
                SomewhereNavigationBar(
                    currentDestination = MyTripsDestination,
                    navigateTo = navigateToMain,
                    scrollToTop = {
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                )
            else
                BottomSaveCancelBar(
                    onCancelClick = { onBackButtonClick() },
                    onSaveClick = {
                        coroutineScope.launch {
                            appViewModel.saveTempTripList {
                                changeEditMode(false)
                            }
                        }
                        organizeAddedDeletedImages(true)
                    }
                )
        },

        floatingActionButton = {
            IconFAB(
                visible = isEditMode,
                icon = MyIcons.add,
                onClick = {
                    if (isEditMode) {
                        coroutineScope.launch {
                            val newTrip = appViewModel.addAndGetNewTrip()

                            if (newTrip != null) {
                                navigateToTrip(true, newTrip)
                                changeEditMode(true)
                            } else
                                Log.d("debug", "New Trip Button onClick - navigate to new trip - Can't find new trip")
                        }
                    }
                    else changeEditMode(null)
                }
            )
        }
    ) { paddingValues ->

        if(showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { showExitDialog = false },
                onDeleteClick = {
                    showExitDialog = false
                    changeEditMode(false)
                    appViewModel.unSaveTempTripList()
                    organizeAddedDeletedImages(false)
                }
            )
        }

        //display trips list
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 200.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            if (showingTripList.isNotEmpty()) {

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
                                    appViewModel.deleteTripFromTempTrip(trip)
                                }
                                showDeleteDialog = false
                            }
                        )
                    }

                    //each trip item
                    key(showingTripList){
                        val slideState = slideStates[trip.id] ?: SlideState.NONE

                        TripListItem(
                            trip = trip,
                            tripList = showingTripList,
                            isEditMode = isEditMode,
                            onTripClick = { trip ->
                                navigateToTrip(false, trip)
                            },
                            deleteTrip = {
                                showDeleteDialog = true
                                addDeletedImages(it.getAllImagesPath())
                            },
                            dateTimeFormat = dateTimeFormat,
                            slideState = slideState,
                            updateSlideState = { tripId, newSlideState ->
                                slideStates[showingTripList[tripId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                //on drag end
                                coroutineScope.launch {
                                    //reorder list
                                    appViewModel.reorderTempTripList(currentIndex, destinationIndex)

                                    //all slideState to NONE
                                    slideStates.putAll(showingTripList.map { it.id }.associateWith { SlideState.NONE })
                                }
                            }
                        )
                    }
                }
            } else {
                //TODO prettier img / text
                item {
                    Text(
                        text = stringResource(id = R.string.no_trip),
                        style = getTextStyle(TextType.CARD__BODY),
                        textAlign = TextAlign.Center
                    )
                    MySpacerColumn(height = 16.dp)
                }
            }

            //TODO remove item{ } before release!
//            item {
//                MySpacerColumn(height = 60.dp)
//                Test()
//            }
        }
    }
}

/**
 * TODO
 *
 * @param updateSlideState update tripIdx item to slideState
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TripListItem(
    trip: Trip,
    tripList: List<Trip>,
    isEditMode: Boolean,
    onTripClick: (Trip) -> Unit,
    deleteTrip: (Trip) -> Unit,

    dateTimeFormat: DateTimeFormat,

    slideState: SlideState,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE),
    titleNullTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE_NULL),
    subtitleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__SUBTITLE)
){
    val locale = LocalConfiguration.current.locales[0]
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val titleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                    else trip.titleText
    val titleTextStyle1 = if (trip.titleText == null || trip.titleText == "") titleNullTextStyle
                            else titleTextStyle

    val subTitleText = trip.getStartEndDateText(locale, dateTimeFormat.copy(includeDayOfWeek = false), true) ?: stringResource(id = R.string.no_date)

    val haptic = LocalHapticFeedback.current


    var cardHeightInt: Int
    val cardHeightDp = 120.dp
    var spacerHeightInt: Int
    val spacerHeightDp = 16.dp

    with(LocalDensity.current){
        cardHeightInt = cardHeightDp.toPx().toInt()
        spacerHeightInt = spacerHeightDp.toPx().toInt()
    }

    Log.d("test3", "   [trip list item] title = ${trip.titleText} | tripList: ${tripList.map { it.titleText }}")

    var isDragged by remember { mutableStateOf(false) }

    val zIndex = if (isDragged) 1.0f else 0.0f

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -(cardHeightInt + spacerHeightInt)
            SlideState.DOWN -> cardHeightInt + spacerHeightInt
            else -> 0
        },
        label = "vertical translation"
    )

    //========================================================================
    //card y offset
    val itemOffsetY = remember { Animatable(0f) }

    val cardColor = if (isDragged)  getColor(ColorType.CARD_ON_DRAG)
                    else            getColor(ColorType.CARD)

    val dragModifier = if (isEditMode) Modifier
        .offset {
            if (isDragged) IntOffset(0, itemOffsetY.value.roundToInt())
            else IntOffset(0, verticalTranslation)
        }
        .zIndex(zIndex)
    else Modifier


    Box(modifier = dragModifier) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
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
                )
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
                            .dragAndDrop(
                                trip, tripList, cardHeightInt + spacerHeightInt,
                                updateSlideState = updateSlideState,
                                offsetY = itemOffsetY,
                                onStartDrag = {
                                    isDragged = true
                                },
                                onStopDrag = { currentIndex, destinationIndex ->

                                    if (currentIndex != destinationIndex){
                                        updateItemPosition(currentIndex, destinationIndex)
                                    }

                                    isDragged = false
                                }
                            )
                        ,
                        //disable touch ripple effect
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
        TextTest("displayLarge", MaterialTheme.typography.displayLarge)
        TextTest("displayMedium", MaterialTheme.typography.displayMedium)
        TextTest("displaySmall", MaterialTheme.typography.displaySmall)
        Spacer(modifier = Modifier.height(16.dp))

        TextTest("headlineLarge", MaterialTheme.typography.headlineLarge)
        TextTest("headlineMedium", MaterialTheme.typography.headlineMedium)
        TextTest("headlineSmall", MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        TextTest("titleLarge", MaterialTheme.typography.titleLarge)
        TextTest("titleMedium", MaterialTheme.typography.titleMedium)
        TextTest("titleSmall", MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(16.dp))

        TextTest("bodyLarge", MaterialTheme.typography.bodyLarge)
        TextTest("bodyMedium", MaterialTheme.typography.bodyMedium)
        TextTest("bodySmall", MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        TextTest("labelLarge", MaterialTheme.typography.labelLarge)
        TextTest("labelMedium", MaterialTheme.typography.labelMedium)
        TextTest("labelSmall", MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.height(20.dp))

        ColorTest(MaterialTheme.colorScheme.primary,           "primary",            MaterialTheme.colorScheme.onPrimary)
        ColorTest(MaterialTheme.colorScheme.primaryContainer,  "primaryContainer",   MaterialTheme.colorScheme.onPrimaryContainer)
        ColorTest(MaterialTheme.colorScheme.inversePrimary,    "inversePrimary",     )
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.secondary,          "secondary",         MaterialTheme.colorScheme.onSecondary)
        ColorTest(MaterialTheme.colorScheme.primaryContainer,   "primaryContainer",  MaterialTheme.colorScheme.onSecondaryContainer)
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.tertiary,           "tertiary",          MaterialTheme.colorScheme.onTertiary)
        ColorTest(MaterialTheme.colorScheme.tertiaryContainer,  "tertiaryContainer", MaterialTheme.colorScheme.onTertiaryContainer)
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.error,              "error",             MaterialTheme.colorScheme.onError)
        ColorTest(MaterialTheme.colorScheme.errorContainer,     "errorContainer",    MaterialTheme.colorScheme.onErrorContainer)
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.background,         "background",        MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.surface,            "surface",           MaterialTheme.colorScheme.onSurface)
        ColorTest(MaterialTheme.colorScheme.inverseSurface,     "inverseSurface",    MaterialTheme.colorScheme.inverseOnSurface)
        ColorTest(MaterialTheme.colorScheme.surfaceVariant,     "surfaceVariant",    MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        ColorTest(MaterialTheme.colorScheme.outline,            "outline",           )
        ColorTest(MaterialTheme.colorScheme.outlineVariant,     "outlineVariant",    )

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
    textColor: Color? = null
){
    Row {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ){
            if (textColor != null)
                Text(
                    text = "On Text",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(text = text)
    }
}
