package com.newpaper.somewhere.feature.trip.tripMap

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.core.designsystem.component.map.MapForTripMap
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getFirstLocation
import com.newpaper.somewhere.core.utils.enterVertically
import com.newpaper.somewhere.core.utils.exitVertically
import com.newpaper.somewhere.core.utils.fitBoundsToMarkersForTripMap
import com.newpaper.somewhere.feature.trip.tripMap.component.BottomSheetHandel
import com.newpaper.somewhere.feature.trip.tripMap.component.ControlPanel
import com.newpaper.somewhere.feature.trip.tripMap.component.MapButtons
import com.newpaper.somewhere.feature.trip.trips.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.GlanceSpots
import com.newpaper.somewhere.feature.trip.trips.component.GlanceSpotGroup
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private val SHEET_PEEK_HEIGHT = 136.dp
private const val CONTROL_PANEL_MAX_WIDTH = 420 //use at horizontal layout-
private const val CONTROL_PANEL_MIN_WIDTH = 366

@Composable
fun TripMapRoute(
    currentTrip: Trip,
    useVerticalLayout: Boolean,
    useBlurEffect: Boolean,
    dateTimeFormat: DateTimeFormat,
    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,
    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    modifier: Modifier = Modifier,
    tripMapViewModel: TripMapViewModel = hiltViewModel()
){

    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (tripMapUiState.dateWithShownMarkerList.isEmpty())
            tripMapViewModel.initDateListWithShownMarkerList(currentTrip.dateList)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    //trip's first location
    val initialLocation = currentTrip.getFirstLocation()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation!!, 5f)
    }

    val dateListState = rememberLazyListState()

    TripMapScreen(
        useBlurEffect,
        tripMapViewModel,
        snackBarHostState,
        dateTimeFormat,
        dateListState,
        currentTrip,
        useVerticalLayout,
        isDarkMapTheme,
        cameraPositionState,
        userLocationEnabled,
        fusedLocationClient,
        setUserLocationEnabled,
        navigateUp,
        navigateToSpot,
        modifier
    )
}


@Composable
private fun TripMapScreen(
    useBlurEffect: Boolean,

    tripMapViewModel: TripMapViewModel,
    snackBarHostState: SnackbarHostState,
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    currentTrip: Trip,

    useVerticalLayout: Boolean,
    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,

    modifier: Modifier = Modifier,
){
    //screen is horizontal/vertical
    val configuration = LocalConfiguration.current

    val hazeState = if (useBlurEffect) rememberHazeState() else null


    //at vertical
    if (useVerticalLayout) {
        TripMapScreenVertical(
            tripMapViewModel,
            snackBarHostState,
            dateTimeFormat,
            dateListState,
            currentTrip,
            isDarkMapTheme,
            cameraPositionState,
            userLocationEnabled,
            fusedLocationClient,
            setUserLocationEnabled,
            navigateUp,
            navigateToSpot,
            hazeState,
            modifier
        )
    }

    //at horizontal
    else {
        TripMapScreenHorizontal(
            configuration,
            tripMapViewModel,
            snackBarHostState,
            dateTimeFormat,
            dateListState,
            currentTrip,
            isDarkMapTheme,
            cameraPositionState,
            userLocationEnabled,
            fusedLocationClient,
            setUserLocationEnabled,
            navigateUp,
            navigateToSpot,
            hazeState,
            modifier
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeApi::class)
@Composable
private fun TripMapScreenVertical(
    tripMapViewModel: TripMapViewModel,
    snackBarHostState: SnackbarHostState,
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    currentTrip: Trip,

    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,

    hazeState: HazeState?,

    modifier: Modifier = Modifier
){
    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsStateWithLifecycle()
    val dateWithShownMarkerList = tripMapUiState.dateWithShownMarkerList
    val spotTypeGroupWithShownMarkerList = tripMapUiState.spotTypeGroupWithShownMarkerList
    val currentDateIndex = tripMapUiState.currentDateIndex
    val oneDateShown = tripMapUiState.oneDateShown
    val fitBoundsToMarkersEnabled = tripMapUiState.fitBoundsToMarkersEnabled

    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    )

    var sheetHeightDp by rememberSaveable { mutableIntStateOf(0) }

    val density = LocalDensity.current.density
    val coroutineScope = rememberCoroutineScope()
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }


    //get map bottom padding
    val bottomPadding: Float by animateFloatAsState(
        if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded)
            sheetHeightDp.toFloat()
        else
            SHEET_PEEK_HEIGHT.value, label = ""
    )

    Scaffold(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding(),
        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier
                        .width(300.dp)
                        .padding(bottom = bottomPadding.dp)
                        .padding(horizontal = 4.dp)
                        .navigationBarsPadding(),
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            shape = MaterialTheme.shapes.small
                        )
                    }
                )
            }
        }
    ) { paddingValue ->

        val sheetContainerColor = if (hazeState == null) MaterialTheme.colorScheme.background
                                else Color.Transparent

        val hazeTintColor = MaterialTheme.colorScheme.background

        val sheetModifier = if (hazeState == null) Modifier
                                    else Modifier.hazeEffect(state = hazeState) {
                                        blurRadius = 16.dp
                                        tints = listOf(HazeTint(hazeTintColor.copy(alpha = 0.8f)))
                                        inputScale = HazeInputScale.Fixed(0.5f)
                                    }

        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            //control
            sheetSwipeEnabled = true,
            sheetContainerColor = sheetContainerColor,
            sheetPeekHeight = SHEET_PEEK_HEIGHT,
            sheetDragHandle = { },
            sheetContent = {
                Column(
                    modifier = sheetModifier
                        .fillMaxHeight(0.5f)
                        .onSizeChanged {
                            sheetHeightDp = (it.height / density).toInt()
                        }
                ) {
                    BottomSheetHandel()

                    ControlPanel(
                        tripMapViewModel,
                        coroutineScope,
                        cameraPositionState,
                        mapSize,
                        fitBoundsToMarkersEnabled,
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
        ){ _ ->

            val mapPadding = PaddingValues(0.dp, paddingValue.calculateTopPadding(), 0.dp, bottomPadding.dp)

            //map
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        mapSize = getMapSizeWithOutPadding(it, mapPadding, density)
                    }
            ) {
                MapForTripMap(
                    modifier = if (hazeState != null) Modifier.hazeSource(state = hazeState)
                                else Modifier,
                    mapPadding = mapPadding,
                    isDarkMapTheme = isDarkMapTheme,
                    userLocationEnabled = userLocationEnabled,
                    cameraPositionState = cameraPositionState,
                    dateList = currentTrip.dateList,
                    dateListWithShownMarkerList = dateWithShownMarkerList,
                    spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                    firstFitBoundsToMarkers = {
                        fitBoundsToMarkers(
                            mapSize,
                            coroutineScope,
                            dateWithShownMarkerList,
                            spotTypeGroupWithShownMarkerList,
                            cameraPositionState
                        )
                    },
                    onClickMarker = { date, spot ->
                        if (tripMapUiState.glanceSpot != spot || (tripMapUiState.glanceSpot == spot && !tripMapUiState.showGlanceSpot)) {
                            tripMapViewModel.setGlanceTripSpot(currentTrip, date, spot)
                            tripMapViewModel.setShowGlanceSpot(true)

                            coroutineScope.launch {
                                fitBoundsToMarkersForTripMap(
                                    mapSize = mapSize,
                                    cameraPositionState = cameraPositionState,
                                    spotList = listOf(spot)
                                )
                            }
                        }
                        else
                            tripMapViewModel.setShowGlanceSpot(false)
                    },
                    onClickMap = {
                        tripMapViewModel.setShowGlanceSpot(false)
                    }
                )

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = bottomPadding.dp + 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //map buttons
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        MapButtons(
                            paddingValues = PaddingValues(end = 16.dp),
                            cameraPositionState = cameraPositionState,
                            fusedLocationClient = fusedLocationClient,
                            setUserLocationEnabled = setUserLocationEnabled,
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
                            hazeState = hazeState
                        )
                    }

                    //glance spot
                    GlanceSpotGroup(
                        showTopTripTitleWithDate = false,
                        modifier = Modifier.padding(top = 16.dp),
                        uesLongWidth = true,
                        visible = tripMapUiState.showGlanceSpot,
                        dateTimeFormat = dateTimeFormat,
                        glanceSpots = GlanceSpots(
                            visible = true,
                            spots = listOf(
                                GlanceSpot(tripMapUiState.glanceTrip,
                                    tripMapUiState.glanceDate,
                                    tripMapUiState.glanceSpot)
                            )
                        ),
                        onClickGlanceSpot = {
                            navigateToSpot(tripMapUiState.glanceDate.index, tripMapUiState.glanceSpot.index)
                        },
                        hazeState = hazeState,
                        enterAnimation = enterVertically,
                        exitAnimation = exitVertically
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun TripMapScreenHorizontal(
    configuration: Configuration,
    tripMapViewModel: TripMapViewModel,
    snackBarHostState: SnackbarHostState,
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    currentTrip: Trip,

    isDarkMapTheme: Boolean,
    cameraPositionState: CameraPositionState,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    navigateUp: () -> Unit,
    navigateToSpot: (dateIndex: Int, spotIndex: Int) -> Unit,

    hazeState: HazeState?,

    modifier: Modifier = Modifier
){
    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsStateWithLifecycle()
    val dateWithShownMarkerList = tripMapUiState.dateWithShownMarkerList
    val spotTypeGroupWithShownMarkerList = tripMapUiState.spotTypeGroupWithShownMarkerList
    val currentDateIndex = tripMapUiState.currentDateIndex
    val oneDateShown = tripMapUiState.oneDateShown
    val fitBoundsToMarkersEnabled = tripMapUiState.fitBoundsToMarkersEnabled

    val density = LocalDensity.current.density
    val coroutineScope = rememberCoroutineScope()
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }

    var cardWidth by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier
            .imePadding()
            .fillMaxSize(),

        snackbarHost = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.width(300.dp).navigationBarsPadding(),
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            shape = MaterialTheme.shapes.small
                        )
                    }
                )
            }
        }
    ) { paddingValues ->

        val mapPadding = PaddingValues(0.dp, paddingValues.calculateTopPadding(), cardWidth.dp, 0.dp)

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {

            //map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        mapSize = getMapSizeWithOutPadding(it, mapPadding, density)
                    }
            ) {
                MapForTripMap(
                    modifier = if (hazeState != null) Modifier.hazeSource(state = hazeState)
                                else Modifier,
                    mapPadding = mapPadding,
                    isDarkMapTheme = isDarkMapTheme,
                    userLocationEnabled = userLocationEnabled,
                    cameraPositionState = cameraPositionState,
                    dateList = currentTrip.dateList,
                    dateListWithShownMarkerList = dateWithShownMarkerList,
                    spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                    firstFitBoundsToMarkers = {
                        fitBoundsToMarkers(
                            mapSize,
                            coroutineScope,
                            dateWithShownMarkerList,
                            spotTypeGroupWithShownMarkerList,
                            cameraPositionState
                        )
                    },
                    onClickMarker = { date, spot ->
                        if (tripMapUiState.glanceSpot != spot || (tripMapUiState.glanceSpot == spot && !tripMapUiState.showGlanceSpot)) {
                            tripMapViewModel.setGlanceTripSpot(currentTrip, date, spot)
                            tripMapViewModel.setShowGlanceSpot(true)

                            coroutineScope.launch {
                                fitBoundsToMarkersForTripMap(
                                    mapSize = mapSize,
                                    cameraPositionState = cameraPositionState,
                                    spotList = listOf(spot)
                                )
                            }
                        }
                        else
                            tripMapViewModel.setShowGlanceSpot(false)
                    },
                    onClickMap = {
                        tripMapViewModel.setShowGlanceSpot(false)
                    }
                )
            }


            //for different screen size - get control panel width
            val screenWidth = configuration.screenWidthDp

            cardWidth =
                if (screenWidth > (CONTROL_PANEL_MAX_WIDTH / 45 * 100)) {
                    CONTROL_PANEL_MAX_WIDTH
                }
                else if (screenWidth < (CONTROL_PANEL_MIN_WIDTH / 45 * 100)) {
                    CONTROL_PANEL_MIN_WIDTH
                }
                else {
                    (screenWidth * 0.45f).toInt()
                }

            //***** RTL *****
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .statusBarsPadding()
                        .padding(16.dp, 0.dp, 0.dp, 16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

                        val panelContainerColor = if (hazeState == null) MaterialTheme.colorScheme.background
                                                    else Color.Transparent

                        val hazeTintColor = MaterialTheme.colorScheme.background

                        val panelModifier = if (hazeState == null) Modifier
                                            else Modifier.hazeEffect(state = hazeState) {
                                                blurRadius = 16.dp
                                                tints = listOf(HazeTint(hazeTintColor.copy(alpha = 0.8f)))
                                                inputScale = HazeInputScale.Fixed(0.5f)
                                            }

                        //control panel
                        MyCard(
                            colors = CardDefaults.cardColors(containerColor = panelContainerColor),
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier
                                .width(cardWidth.dp)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = panelModifier.fillMaxHeight()
                            ) {
                                MySpacerColumn(height = 16.dp)

                                ControlPanel(
                                    tripMapViewModel,
                                    coroutineScope,
                                    cameraPositionState,
                                    mapSize,
                                    fitBoundsToMarkersEnabled,
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
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        //map buttons
                        MapButtons(
                            paddingValues = PaddingValues(end = 16.dp),
                            cameraPositionState = cameraPositionState,
                            fusedLocationClient = fusedLocationClient,
                            setUserLocationEnabled = setUserLocationEnabled,
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
                            hazeState = hazeState
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        //glance spot
                        GlanceSpotGroup(
                            showTopTripTitleWithDate = false,
                            uesLongWidth = true,
                            visible = tripMapUiState.showGlanceSpot,
                            dateTimeFormat = dateTimeFormat,
                            glanceSpots = GlanceSpots(
                                visible = true,
                                spots = listOf(
                                    GlanceSpot(tripMapUiState.glanceTrip,
                                        tripMapUiState.glanceDate,
                                        tripMapUiState.glanceSpot)
                                )
                            ),
                            onClickGlanceSpot = {
                                navigateToSpot(tripMapUiState.glanceDate.index, tripMapUiState.glanceSpot.index)
                            },
                            hazeState = hazeState
                        )
                    }
                }
            }
        }
    }
}

















private fun getMapSizeWithOutPadding(
    mapSizeWithPadding: IntSize,
    padding: PaddingValues,
    density: Float
): IntSize{
    val topPadding = padding.calculateTopPadding().value * density
    val bottomPadding = padding.calculateBottomPadding().value * density
    val leftPadding = padding.calculateLeftPadding(LayoutDirection.Rtl).value * density
    val rightPadding = padding.calculateRightPadding(LayoutDirection.Rtl).value * density

    return IntSize((mapSizeWithPadding.height - topPadding - bottomPadding).toInt(),
        (mapSizeWithPadding.width - leftPadding - rightPadding).toInt())
}