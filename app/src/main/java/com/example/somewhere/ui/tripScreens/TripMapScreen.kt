package com.example.somewhere.ui.tripScreens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.tripScreenUtils.MapForTrip
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.tripScreenUtils.UserLocationButton
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.tripScreenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.tripScreenUtils.cards.SpotTypeGroupCard
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.viewModel.DateTimeFormat
import com.example.somewhere.viewModel.DateWithBoolean
import com.example.somewhere.viewModel.SpotTypeGroupWithBoolean
import com.example.somewhere.viewModel.TripMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object TripMapDestination : NavigationDestination {
    override val route = "trip map"
    override var title = ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripMapScreen(
    currentTrip: Trip,
    dateTimeFormat: DateTimeFormat,
    navigateUp: () -> Unit,
    isDarkMapTheme: Boolean,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    modifier: Modifier = Modifier
){
    //trip map viewModel
    val tripMapViewModel = viewModel<TripMapViewModel>(
        factory = object: ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TripMapViewModel(currentTrip) as T
            }
        }
    )

    val snackBarHostState = remember { SnackbarHostState() }

    //ui state
    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsState()

    val dateWithShownMarkerList = tripMapUiState.dateWithShownMarkerList
    val spotTypeGroupWithShownMarkerList = tripMapUiState.spotTypeGroupWithShownMarkerList
    val currentDateIndex = tripMapUiState.currentDateIndex
    val oneDateShown = tripMapUiState.oneDateShown
    val focusOnToSpotEnabled = tripMapUiState.focusOnToSpotEnabled


    //trip's first location
    val initialLocation = currentTrip.getFirstLocation()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation!!, 5f)
    }

    val density = LocalDensity.current.density

    var isControlExpanded by rememberSaveable{ mutableStateOf(true) }
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }
    var controlBoxHeight by rememberSaveable { mutableStateOf(0) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateListState = rememberLazyListState()


    //screen is horizontal/vertical
    val configuration = LocalConfiguration.current

    when (configuration.orientation){
        //at vertical
        Configuration.ORIENTATION_PORTRAIT -> {
            Box(modifier = Modifier.navigationBarsPadding()) {
                BottomSheetScaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier.width(500.dp)
                        )
                    },

                    //control
                    sheetContainerColor = MaterialTheme.colorScheme.background,
                    sheetPeekHeight = 150.dp,
                    sheetDragHandle = { },

                    sheetContent = {
                        Column(
                            modifier = Modifier.fillMaxHeight(0.45f)
                        ) {
                            BottomSheetHandel()

                            ControlPanel(
                                tripMapViewModel,
                                coroutineScope,
                                fusedLocationClient,
                                cameraPositionState,
                                mapSize,
                                setUserLocationEnabled,
                                focusOnToSpotEnabled,
                                oneDateShown,
                                dateTimeFormat,
                                currentDateIndex,
                                dateListState,
                                spotTypeGroupWithShownMarkerList,
                                dateWithShownMarkerList,
                                navigateUp,
                                snackBarHostState
                            )
                        }
                    }
                ) { _ ->

                    //map
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                mapSize = it
                            }
                    ) {
                        MapForTrip(
                            context = context,
                            isDarkMapTheme = isDarkMapTheme,
                            userLocationEnabled = userLocationEnabled,
                            cameraPositionState = cameraPositionState,
                            dateListWithShownMarkerList = dateWithShownMarkerList,
                            spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                            firstFocusOnToSpot = {
                                focusOnToSpot(
                                    mapSize,
                                    coroutineScope,
                                    dateWithShownMarkerList,
                                    spotTypeGroupWithShownMarkerList,
                                    cameraPositionState
                                )
                            }
                        )
                    }
                }
            }
        }

        //at horizontal
        else ->{
            Scaffold(
                snackbarHost = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier.width(500.dp)
                        )
                    }
                }
            ) { paddingValue ->
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.navigationBarsPadding()
                ) {

                    //map
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged {
                                mapSize = it
                            }
                    ) {
                        MapForTrip(
                            context = context,
                            isDarkMapTheme = isDarkMapTheme,
                            userLocationEnabled = userLocationEnabled,
                            cameraPositionState = cameraPositionState,
                            dateListWithShownMarkerList = dateWithShownMarkerList,
                            spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                            firstFocusOnToSpot = {
                                focusOnToSpot(
                                    mapSize,
                                    coroutineScope,
                                    dateWithShownMarkerList,
                                    spotTypeGroupWithShownMarkerList,
                                    cameraPositionState
                                )
                            }
                        )
                    }


                    //for different screen size - get control panel width
                    val maxWidth = 450.dp
                    val minWidth = 330.dp
                    val screenWidth = configuration.screenWidthDp.dp
                    val cardModifier =
                        if      (screenWidth > maxWidth / 45 * 100) Modifier.width(maxWidth)
                        else if (screenWidth < minWidth / 45 * 100) Modifier.width(minWidth)
                        else Modifier.fillMaxWidth(0.45f)

                    //get status bar padding value - get top padding
                    val statusBarPaddingValue = paddingValue.calculateTopPadding()
                    val topPadding = if (statusBarPaddingValue == 0.dp) 16.dp
                                    else statusBarPaddingValue

                    //control panel
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = cardModifier
                            .displayCutoutPadding()
                            .padding(0.dp, topPadding, 16.dp, 16.dp)
                            .fillMaxHeight()
                    ) {
                        Column {
                            MySpacerColumn(height = 16.dp)

                            ControlPanel(
                                tripMapViewModel,
                                coroutineScope,
                                fusedLocationClient,
                                cameraPositionState,
                                mapSize,
                                setUserLocationEnabled,
                                focusOnToSpotEnabled,
                                oneDateShown,
                                dateTimeFormat,
                                currentDateIndex,
                                dateListState,
                                spotTypeGroupWithShownMarkerList,
                                dateWithShownMarkerList,
                                navigateUp,
                                snackBarHostState
                            )
                        }

                    }

//                Column(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxHeight()
//                ) {
//
//                }
                }
            }
        }

    }














//    Scaffold(
//        snackbarHost = { androidx.compose.material3.SnackbarHost(
//            hostState = snackBarHostState,
//            modifier = Modifier
//                .padding(snackBarPaddingValues)
//                .width(500.dp)
//        ) },
//    ) { paddingValue ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//                .padding(PaddingValues(0.dp, 0.dp, 0.dp, paddingValue.calculateBottomPadding()))
//        ) {
//
//            //map
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .onSizeChanged {
//                        mapSize = it
//                    }
//            ) {
//                MapForTrip(
//                    context = context,
//                    isDarkMapTheme = isDarkMapTheme,
//                    userLocationEnabled = userLocationEnabled,
//                    cameraPositionState = cameraPositionState,
//                    dateListWithShownMarkerList = dateWithShownMarkerList,
//                    spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
//                    firstFocusOnToSpot = {
//                        focusOnToSpot(
//                            mapSize,
//                            coroutineScope,
//                            dateWithShownMarkerList,
//                            spotTypeGroupWithShownMarkerList,
//                            cameraPositionState
//                        )
//                    }
//                )
//            }
//
//
//            //control
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(getColor(ColorType.BACKGROUND))
//                    .onSizeChanged {
//                        controlBoxHeight = it.height
//                    }
//            ) {
//
//                Column(
//                    modifier = Modifier
//                ) {
//                    MySpacerColumn(height = 8.dp)
//
//                    ButtonsRow(
//                        isExpanded = isControlExpanded,
//                        mapSize = mapSize,
//                        onExpandButtonClicked = {
//                            isControlExpanded = !isControlExpanded
//                        },
//
//                        focusOnToTargetEnabled = focusOnToSpotEnabled,
//
//                        isDateShown = oneDateShown,
//                        dateTimeFormat = dateTimeFormat,
//
//                        currentDateIndex = currentDateIndex,
//
//                        onOneDateClicked = {
//                            val newDateShownList =
//                                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
//                            focusOnToSpot(
//                                mapSize,
//                                coroutineScope,
//                                newDateShownList,
//                                spotTypeGroupWithShownMarkerList,
//                                cameraPositionState
//                            )
//                            animateToDate(
//                                coroutineScope,
//                                isControlExpanded,
//                                dateListState,
//                                currentDateIndex
//                            )
//                        },
//                        onPreviousDateClicked = {
//                            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToPrevious()
//                            val newDateShownList =
//                                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
//                            focusOnToSpot(
//                                mapSize,
//                                coroutineScope,
//                                newDateShownList,
//                                spotTypeGroupWithShownMarkerList,
//                                cameraPositionState
//                            )
//                            animateToDate(
//                                coroutineScope,
//                                isControlExpanded,
//                                dateListState,
//                                newCurrentDateIndex
//                            )
//                        },
//                        onNextDateClicked = {
//                            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToNext()
//                            val newDateShownList =
//                                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
//                            focusOnToSpot(
//                                mapSize,
//                                coroutineScope,
//                                newDateShownList,
//                                spotTypeGroupWithShownMarkerList,
//                                cameraPositionState
//                            )
//                            animateToDate(
//                                coroutineScope,
//                                isControlExpanded,
//                                dateListState,
//                                newCurrentDateIndex
//                            )
//                        },
//
//                        cameraPositionState = cameraPositionState,
//                        dateListWithShownIconList = dateWithShownMarkerList,
//                        spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
//                        setUserLocationEnabled = setUserLocationEnabled,
//                        fusedLocationClient = fusedLocationClient,
//                        onBackButtonClicked = navigateUp,
//                        showSnackBar = { text_, actionLabel_ ->
//                            coroutineScope.launch {
//                                snackBarHostState.showSnackbar(text_, actionLabel_)
//                            }
//                        }
//                    )
//
//                    SpotTypeList(
//                        spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
//                        onSpotTypeItemClicked = { spotTypeGroup ->
//                            tripMapViewModel.toggleSpotTypeGroupWithShownMarkerList(spotTypeGroup)
//                        }
//                    )
//
//                    MySpacerColumn(height = 8.dp)
//
//                    AnimatedVisibility(
//                        visible = isControlExpanded,
//                        enter =
//                        expandVertically(
//                            animationSpec = tween(durationMillis = 400),
//                            expandFrom = Alignment.Top
//                        ),
//                        exit =
//                        shrinkVertically(
//                            animationSpec = tween(durationMillis = 400),
//                            shrinkTowards = Alignment.Top
//                        )
//                    )
//                    {
//                        DateList(
//                            dateTimeFormat = dateTimeFormat,
//                            dateListState = dateListState,
//                            dateListWithShownIconList = dateWithShownMarkerList,
//                            onDateItemClicked = { date ->
//                                tripMapViewModel.toggleOneDateShown(date)
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
}

@Composable
private fun BottomSheetHandel(

){
    MySpacerColumn(height = 14.dp)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline)
        )
    }

    MySpacerColumn(height = 14.dp)
}

/*
have to Column()
*/
@Composable
private fun ControlPanel(
    tripMapViewModel: TripMapViewModel,
    coroutineScope: CoroutineScope,

    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    focusOnToSpotEnabled: Boolean,
    oneDateShown: Boolean,

    dateTimeFormat: DateTimeFormat,
    currentDateIndex: Int,

    dateListState: LazyListState,
    spotTypeGroupWithShownMarkerList:List<SpotTypeGroupWithBoolean>,
    dateWithShownMarkerList: List<DateWithBoolean>,

    navigateUp: () -> Unit,
    snackBarHostState: SnackbarHostState
){
    ButtonsRow(
        isExpanded = false,
        mapSize = mapSize,
        onExpandButtonClicked = { },

        focusOnToTargetEnabled = focusOnToSpotEnabled,

        isDateShown = oneDateShown,
        dateTimeFormat = dateTimeFormat,

        currentDateIndex = currentDateIndex,

        onOneDateClicked = {
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            focusOnToSpot(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            animateToDate(
                coroutineScope,
                true,
                dateListState,
                currentDateIndex
            )
        },
        onPreviousDateClicked = {
            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToPrevious()
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            focusOnToSpot(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            animateToDate(
                coroutineScope,
                true,
                dateListState,
                newCurrentDateIndex
            )
        },
        onNextDateClicked = {
            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToNext()
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            focusOnToSpot(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            animateToDate(
                coroutineScope,
                true,
                dateListState,
                newCurrentDateIndex
            )
        },

        cameraPositionState = cameraPositionState,
        dateListWithShownIconList = dateWithShownMarkerList,
        spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
        setUserLocationEnabled = setUserLocationEnabled,
        fusedLocationClient = fusedLocationClient,
        onBackButtonClicked = navigateUp,
        showSnackBar = { text_, actionLabel_, duration_ ->
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = text_,
                    actionLabel = actionLabel_,
                    duration = duration_
                )
            }
        }
    )

    SpotTypeList(
        spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
        onSpotTypeItemClicked = { spotTypeGroup ->
            tripMapViewModel.toggleSpotTypeGroupWithShownMarkerList(spotTypeGroup)
        }
    )

    MySpacerColumn(height = 8.dp)

    AnimatedVisibility(
        visible = true,
        enter =
        expandVertically(
            animationSpec = tween(durationMillis = 400),
            expandFrom = Alignment.Top
        ),
        exit =
        shrinkVertically(
            animationSpec = tween(durationMillis = 400),
            shrinkTowards = Alignment.Top
        )
    )
    {
        DateList(
            dateTimeFormat = dateTimeFormat,
            dateListState = dateListState,
            dateListWithShownIconList = dateWithShownMarkerList,
            onDateItemClicked = { date ->
                tripMapViewModel.toggleOneDateShown(date)
            }
        )
    }
}

private fun animateToDate(
    coroutineScope: CoroutineScope,
    isControlExpanded: Boolean,
    dateListState: LazyListState,
    currentDateIndex: Int
){
    if (isControlExpanded)
        coroutineScope.launch {
            dateListState.animateScrollToItem(currentDateIndex)
        }
}

@Composable
private fun ButtonsRow(
    isExpanded: Boolean,
    mapSize: IntSize,
    onExpandButtonClicked: () -> Unit,

    focusOnToTargetEnabled: Boolean,

    isDateShown: Boolean,
    dateTimeFormat: DateTimeFormat,
    currentDateIndex: Int,
    onOneDateClicked: () -> Unit,
    onPreviousDateClicked: () -> Unit,
    onNextDateClicked: () -> Unit,

    cameraPositionState: CameraPositionState,

    dateListWithShownIconList:List<DateWithBoolean>,
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,

    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,
    fusedLocationClient: FusedLocationProviderClient,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit,

    onBackButtonClicked: () -> Unit,
){
    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = Modifier.padding(4.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //back button
        IconButton(onClick = onBackButtonClicked) {
            DisplayIcon(icon = MyIcons.back)
        }

        Spacer(modifier = Modifier.weight(1f))

        // my location & focus on target buttons
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            //my location button
            UserLocationButton(fusedLocationClient , cameraPositionState, setUserLocationEnabled, showSnackBar)

            //focus on to target button
            FocusOnToSpotButton(
                mapSize = mapSize,
                focusOnToTargetEnabled = focusOnToTargetEnabled,
                cameraPositionState = cameraPositionState,
                dateListWithShownIconList = dateListWithShownIconList,
                spotTypeGroupWithShownIconList = spotTypeGroupWithShownIconList,
                showSnackBar = showSnackBar
            )
        }

        MySpacerRow(width = 16.dp)

        //date button  <  3.28  >
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onPreviousDateClicked
            ) {
                DisplayIcon(icon = MyIcons.leftArrow)
            }

            val dateTextStyle = if (isDateShown)    getTextStyle(TextType.BUTTON)
            else                getTextStyle(TextType.BUTTON_NULL)

            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(40.dp)
                    .clip(CircleShape)
                    .clickable { onOneDateClicked() },
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = dateListWithShownIconList[currentDateIndex].date.getDateText(locale, dateTimeFormat.copy(includeDayOfWeek = false), false),
                    style = dateTextStyle
                )
            }


            IconButton(
                onClick = onNextDateClicked
            ) {
                DisplayIcon(icon = MyIcons.rightArrow)
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        //expand collapse button
//        IconButton(onClick = onExpandButtonClicked) {
//            DisplayIcon(icon = if (isExpanded)  MyIcons.collapseControlBox
//                                else            MyIcons.expandControlBox)
//        }
    }
}



@Composable
private fun FocusOnToSpotButton(
    mapSize: IntSize,
    focusOnToTargetEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    dateListWithShownIconList:List<DateWithBoolean>,
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val spotTypeShownList = mutableListOf<SpotTypeGroup>()
    val snackBarText = stringResource(id = R.string.snack_bar_no_location_to_show)

    for (spotTypeWithBoolean in spotTypeGroupWithShownIconList){
        if (spotTypeWithBoolean.isShown)
            spotTypeShownList.add(spotTypeWithBoolean.spotTypeGroup)
    }

    if (focusOnToTargetEnabled){
        IconButton(
            onClick = {
                focusOnToSpot(
                    mapSize = mapSize,
                    coroutineScope = coroutineScope,
                    dateListWithShownMarkerList = dateListWithShownIconList,
                    spotTypeWithShownMarkerList = spotTypeGroupWithShownIconList,
                    cameraPositionState = cameraPositionState
                )
            }
        ) {
            DisplayIcon(icon = MyIcons.focusOnToTarget)
        }
    }
    else{
        IconButton(
            onClick = {
                showSnackBar(snackBarText, null, SnackbarDuration.Short)
            }
        ) {
            DisplayIcon(icon = MyIcons.disabledFocusOnToTarget)
        }
    }


}

private fun focusOnToSpot(
    mapSize: IntSize,
    coroutineScope: CoroutineScope,
    dateListWithShownMarkerList: List<DateWithBoolean>,
    spotTypeWithShownMarkerList: List<SpotTypeGroupWithBoolean>,
    cameraPositionState: CameraPositionState,
){
    val spotList: MutableList<Spot> = mutableListOf()

    for (dateWithBoolean in dateListWithShownMarkerList) {
        if (dateWithBoolean.isShown) {
            for (spot in dateWithBoolean.date.spotList) {
                if (SpotTypeGroupWithBoolean(spot.spotType.group, true or false) in spotTypeWithShownMarkerList &&
                    spot.location != null
                ) {
                    spotList.add(spot)
                }
            }
        }
    }

    //add ghost spot - to avoid hide behind the control panel.


    coroutineScope.launch {
        com.example.somewhere.ui.tripScreenUtils.focusOnToSpot(
            cameraPositionState, mapSize, spotList.toList()
        )
    }
}

@Composable
private fun SpotTypeList(
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,
    onSpotTypeItemClicked: (SpotTypeGroup) -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.CARD__SPOT_TYPE),
    isShownColor: Color = getColor(ColorType.BUTTON),
    isNotShownColor: Color = getColor(ColorType.CARD)
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(16.dp, 8.dp, 4.dp, 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(spotTypeGroupWithShownIconList) {

            Row {
                SpotTypeGroupCard(
                    spotTypeGroup = it.spotTypeGroup,
                    defaultTextStyle = textStyle,
                    isShown = it.isShown,
                    isNotShownColor = isNotShownColor,
                    onCardClicked = { spotTypeGroup ->
                        onSpotTypeItemClicked(spotTypeGroup)
                    },
                    shownColor = isShownColor,
                )

                MySpacerRow(width = 12.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DateList(
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    dateListWithShownIconList: List<DateWithBoolean>,
    onDateItemClicked: (Date) -> Unit,
){
    val pullRefreshState = rememberPullRefreshState(true, {  })

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        PullRefreshIndicator(refreshing = false, state = pullRefreshState)
        //here!!!!!!!!!!!!!!!

        LazyColumn(
            state = dateListState,
            contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp),
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth()
//            .height(260.dp)
        ) {
            item {
                StartEndDummySpaceWithRoundedCorner(
                    isFirst = true,
                    isLast = false
                )
            }
            items(dateListWithShownIconList) {
                DateItem(
                    date = it.date,
                    dateTimeFormat = dateTimeFormat,
                    isShown = it.isShown,
                    onItemClicked = { date ->
                        onDateItemClicked(date)
                    }
                )
            }
            item {
                StartEndDummySpaceWithRoundedCorner(
                    isFirst = false,
                    isLast = true
                )

                MySpacerColumn(height = 60.dp)
            }
        }
    }
}
@Composable
private fun DateItem(
    date: Date,
    dateTimeFormat: DateTimeFormat,
    isShown: Boolean,
    onItemClicked: (Date) -> Unit,
    dateSpotTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__SIDE),
    titleTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__MAIN),
    titleNullTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__MAIN_NULL)
){
    val pointColor = if (isShown) Color(date.color.color)
                    else Color.Transparent

    val locale = LocalConfiguration.current.locales[0]


    ClickableBox(
        shape = RectangleShape,
        containerColor = getColor(ColorType.CARD),
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),
        onClick = { onItemClicked(date) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //circle
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(pointColor)
            )

            MySpacerRow(width = 12.dp)

            //date text
            Box(
                modifier = Modifier
                    .width(58.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = date.getDateText(locale, dateTimeFormat.copy(includeDayOfWeek = false), false),
                    style = dateSpotTextStyle
                )
            }

            MySpacerRow(width = 12.dp)

            //title text
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = date.titleText ?: stringResource(id = R.string.no_title),
                    style = if (isShown && date.titleText != null)  titleTextStyle
                            else                                    titleNullTextStyle
                )
            }

            //spot count
            Text(
                text = "${date.spotList.size - date.getSpotTypeGroupCount(SpotTypeGroup.MOVE)} Spot",
                style = dateSpotTextStyle
            )
        }
    }
}
