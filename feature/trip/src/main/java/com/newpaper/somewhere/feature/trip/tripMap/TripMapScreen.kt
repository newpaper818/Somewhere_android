package com.newpaper.somewhere.feature.trip.tripMap

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.newpaper.somewhere.feature.trip.tripMap.component.BottomSheetHandel
import com.newpaper.somewhere.feature.trip.tripMap.component.ControlPanel
import com.newpaper.somewhere.feature.trip.tripMap.component.MapButtons
import kotlinx.coroutines.launch

private val SHEET_PEEK_HEIGHT = 150.dp
private const val CONTROL_PANEL_MAX_WIDTH = 420 //use at horizontal layout-
private const val CONTROL_PANEL_MIN_WIDTH = 366

@Composable
fun TripMapRoute(
    currentTrip: Trip,
    useVerticalLayout: Boolean,
    dateTimeFormat: DateTimeFormat,
    navigateUp: () -> Unit,
    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    userLocationEnabled: Boolean,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    modifier: Modifier = Modifier,
    tripMapViewModel: TripMapViewModel = hiltViewModel()
){

    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsState()

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
        modifier
    )
}


@Composable
private fun TripMapScreen(
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

    modifier: Modifier = Modifier,
){
    //screen is horizontal/vertical
    val configuration = LocalConfiguration.current

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
            modifier
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
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

    modifier: Modifier = Modifier
){
    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsState()
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
                        .padding(horizontal = 4.dp),
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                )
            }
        }
    ) { paddingValue ->
        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            //control
            sheetSwipeEnabled = true,
            sheetContainerColor = MaterialTheme.colorScheme.background,
            sheetPeekHeight = SHEET_PEEK_HEIGHT,
            sheetDragHandle = { },
            sheetContent = {
                Column(
                    modifier = Modifier
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
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        mapSize = getMapSizeWithOutPadding(it, mapPadding, density)
                    }
            ) {
                MapForTripMap(
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
                    }
                )

                //map buttons
                MapButtons(
                    paddingValues = PaddingValues(end = 16.dp, bottom = bottomPadding.dp + 16.dp),
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
                                when (this){
                                    SnackbarResult.Dismissed -> { }
                                    SnackbarResult.ActionPerformed -> onActionClick()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

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

    modifier: Modifier = Modifier
){
    val tripMapUiState by tripMapViewModel.tripMapUiState.collectAsState()
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
                    modifier = Modifier.width(300.dp),
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            shape = MaterialTheme.shapes.medium
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


            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .statusBarsPadding()
                    .padding(0.dp, 0.dp, 16.dp, 16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
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
                                when (this){
                                    SnackbarResult.Dismissed -> { }
                                    SnackbarResult.ActionPerformed -> onActionClick()
                                }
                            }
                        }
                    }
                )

                //control panel
                MyCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .width(cardWidth.dp)
                        .fillMaxHeight()
                ) {
                    Column {
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