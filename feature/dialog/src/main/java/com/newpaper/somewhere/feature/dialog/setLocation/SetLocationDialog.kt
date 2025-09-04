package com.newpaper.somewhere.feature.dialog.setLocation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.NegativePositiveButtons
import com.newpaper.somewhere.core.designsystem.component.map.MapForSetLocation
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.LocationInfo
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.ZoomButtonsForSetLocation
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.utils.DEFAULT_ZOOM_LEVEL
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getPrevLocation
import com.newpaper.somewhere.core.utils.convert.setLocationAndUpdateTravelDistance
import com.newpaper.somewhere.core.utils.focusOnToLatLngForSetLocation
import com.newpaper.somewhere.feature.dialog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val SEARCH_BOX_LIST_PADDING = 14.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetLocationDialog(
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    use2Panes: Boolean,
    showingTrip: Trip,
    dateList: List<Date>,
    spotList: List<Spot>,
    dateIndex: Int,
    spotIndex: Int,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    onClickCloseButton: () -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    setShowSetLocationDialogToFalse: () -> Unit,

    setLocationViewModel: SetLocationViewModel = hiltViewModel()
){
    LaunchedEffect(Unit) {
        setLocationViewModel.init()
    }

    val coroutineScope = rememberCoroutineScope()

    val setLocationUiState by setLocationViewModel.setLocationUiState.collectAsState()

    val firstLocation = spotList[spotIndex].getPrevLocation(dateList, dateIndex)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, 5f)
    }

    var screenWidthDp by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current.density

    val focusManager = LocalFocusManager.current

    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }
    var searchListSize by remember{ mutableStateOf(IntSize(0,0)) }

    val snackBarHostState = remember { SnackbarHostState() }


    val mapPaddingValues = if (setLocationUiState.userTexting
        || setLocationUiState.searchLocation.searchText.isEmpty())
        PaddingValues(16.dp, 64.dp, 16.dp, 0.dp)
    else PaddingValues(16.dp, 64.dp, 16.dp,
        (searchListSize.height / density).dp + SEARCH_BOX_LIST_PADDING)






    MyScaffold(
        modifier = Modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .width(500.dp)
                    .padding(
                        bottom = if (LocalConfiguration.current.screenWidthDp > 670) 90.dp
                        else 150.dp
                    )
                    .imePadding()
                    .navigationBarsPadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        },
        //top bar
        topBar = {
            SomewhereTopAppBar(
                title = stringResource(id = R.string.set_location),
                internetEnabled = internetEnabled,

                //back button
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = onClickCloseButton,

                actionIcon1 = TopAppBarIcon.more,
                actionIcon1Onclick = {
                    //open dropdown menu
                    setLocationViewModel.setShowDropdownMenu(true)
                },
                dropdownMenuContent = {
                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(
                            extraSmall = RoundedCornerShape(16.dp)
                        )
                    ){
                        DropdownMenu(
                            expanded = setLocationUiState.showDropdownMenu,
                            onDismissRequest = { setLocationViewModel.setShowDropdownMenu(false) }
                        ) {
                            ShowOtherDateSpotMarkersCheckBox(
                                checked = setLocationUiState.showOtherDateSpotMarkers,
                                onCheckedChange = setLocationViewModel::setShowOtherDateSpotMarkers
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        //edit location
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged {
                    screenWidthDp = (it.width.toFloat() / density).toInt()
                }
        ) {
            //map, search box ... ==================================================================
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onSizeChanged {
                        mapSize = it
                    }
            ) {

                //google map ---------------------
                MapForSetLocation(
                    mapPadding = mapPaddingValues,
                    isDarkMapTheme = isDarkMapTheme,
                    cameraPositionState = cameraPositionState,
                    dateList = dateList,
                    dateIndex = dateIndex,
                    currentSpot = spotList[spotIndex],
                    showOtherDateSpotMarkers = setLocationUiState.showOtherDateSpotMarkers,
                    showSearchLocationMarker = setLocationUiState.searchLocation.searchText != "",
                    searchLocationMarkerList = setLocationUiState.searchLocation.searchLocationList,
                    onMapLoaded = {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    firstLocation,
                                    spotList[spotIndex].zoomLevel ?: DEFAULT_ZOOM_LEVEL
                                ), 300
                            )
                        }
                    }
                )

                //center marker ---------------------
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(mapPaddingValues)
                        .fillMaxSize()
                        .clearAndSetSemantics { }
                ) {
                    CenterMarker(
                        color = if (setLocationUiState.showOtherDateSpotMarkers) Color(dateList[dateIndex].color.color)
                                else Color(spotList[spotIndex].spotType.group.color.color),
                        onColor = if (setLocationUiState.showOtherDateSpotMarkers) Color(dateList[dateIndex].color.onColor)
                                else Color(spotList[spotIndex].spotType.group.color.onColor),
                        markerText = spotList[spotIndex].iconText.toString()
                    )
                }

                //search box, search list, date list ----------------
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SEARCH_BOX_LIST_PADDING)
                ) {
                    //search box
                    MapSearchBox(
                        visible = internetEnabled,
                        isLoading = setLocationUiState.searchLocation.isLoading,
                        searchText = setLocationUiState.searchLocation.searchText,
                        onTextChanged = {
                            if (!setLocationUiState.userTexting)
                                setLocationViewModel.setUserTexting(true)

                            setLocationViewModel.setSearchText(it)

                            if (it.isNotEmpty()) {
                                setLocationViewModel.setIsLoadingSearchPlaces(true)

                                coroutineScope.launch {
                                    setLocationViewModel.searchPlaces(it)
                                    setLocationViewModel.setIsLoadingSearchPlaces(false)
                                }
                            }
                            setLocationViewModel.setGoogleMapsPlacesId(null)
                        },
                        onClearClicked = {
                            setLocationViewModel.setSearchText("")
                            setLocationViewModel.clearSearchLocationList()
                            setLocationViewModel.setGoogleMapsPlacesId(null)
                        },
                        onSearchClicked = {
                            if (setLocationUiState.searchLocation.searchText != "") {
                                coroutineScope.launch {
                                    setLocationViewModel.setIsLoading(true)
                                    focusManager.clearFocus()
                                    setLocationViewModel.setUserTexting(false)

                                    setLocationViewModel.updateAllLatLng(
                                        onDone = { newSearchLocationList ->
                                            mapAnimateToLatLng(
                                                newSearchLocationList,
                                                cameraPositionState,
                                                mapSize,
                                                coroutineScope
                                            )
                                            setLocationViewModel.setIsLoading(false)
                                        }
                                    )
                                }
                            } else {
                                focusManager.clearFocus()
                                setLocationViewModel.setUserTexting(false)
                                setLocationViewModel.clearSearchLocationList()
                            }
                            setLocationViewModel.setGoogleMapsPlacesId(null)
                        }
                    )



                    //spacer
                    if (!setLocationUiState.userTexting)
                        Spacer(modifier = Modifier.weight(1f))

                    MySpacerColumn(height = 8.dp)





                    //list of find places
                    MapSearchList(
                        visible = setLocationUiState.searchLocation.searchText.isNotEmpty()
                                && setLocationUiState.searchLocation.searchLocationList.isNotEmpty(),
                        locationAutoFillList = setLocationUiState.searchLocation.searchLocationList,
                        selectedPlaceId = setLocationUiState.googleMapsPlacesId,
                        onClickItem = { clickedLocationInfo ->
                            //get one location from touched item
                            focusManager.clearFocus()
                            setLocationViewModel.setUserTexting(false)

                            if (clickedLocationInfo.location != null) {
                                mapAnimateToLatLng(
                                    listOf(clickedLocationInfo),
                                    cameraPositionState,
                                    mapSize,
                                    coroutineScope
                                )
                            } else {
                                coroutineScope.launch {
                                    setLocationViewModel.updateOneLatLng(
                                        locationInfo = clickedLocationInfo,
                                        onDone = { newLocationInfo ->
                                            mapAnimateToLatLng(
                                                listOf(newLocationInfo),
                                                cameraPositionState,
                                                mapSize,
                                                coroutineScope
                                            )
                                        }
                                    )
                                    setLocationViewModel.updateAllLatLng(
                                        onDone = {}
                                    )
                                }
                            }
                            setLocationViewModel.setGoogleMapsPlacesId(clickedLocationInfo.placeId)
                        },
                        onSearchListSizeChanged = {
                            searchListSize = it
                        }
                    )
                }

                //date list with circle --------------
                val padding =
                    if (!use2Panes)
                        PaddingValues(
                            mapPaddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                            mapPaddingValues.calculateTopPadding() + 8.dp,
                            mapPaddingValues.calculateRightPadding(LayoutDirection.Ltr),
                            mapPaddingValues.calculateBottomPadding() + 34.dp,
                        )
                    else PaddingValues(
                        SEARCH_BOX_LIST_PADDING,
                        SEARCH_BOX_LIST_PADDING,
                        SEARCH_BOX_LIST_PADDING,
                        if (setLocationUiState.searchLocation.searchLocationList.isEmpty()) SEARCH_BOX_LIST_PADDING + 20.dp
                        else SEARCH_BOX_LIST_PADDING
                    )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.BottomStart
                ) {
                    DateListWithCircle(
                        visible = setLocationUiState.showOtherDateSpotMarkers &&
                                !setLocationUiState.userTexting,
                        dateTimeFormat = dateTimeFormat,
                        dateList = dateList
                    )
                }
            }

            //buttons ==============================================================================
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
//                    .clickable(enabled = false) { }
                ,
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.width(330.dp)
                ) {
                    ZoomButtonsForSetLocation(
                        zoomLevel = cameraPositionState.position.zoom,
                        mapZoomTo = { newZoomLevel ->
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.zoomTo(newZoomLevel), 300
                                )
                            }
                        },
                        fusedLocationClient = fusedLocationClient,
                        cameraPositionState = cameraPositionState,
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

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .width(330.dp)
                ) {
                    //cancel save buttons
                    NegativePositiveButtons(
                        positiveButtonText = stringResource(id = R.string.save),
                        onClickCancel = setShowSetLocationDialogToFalse,
                        onClickPositive = {
                            //set location and zoom level
                            //spotList[currentSpotId].zoomLevel = zoomLevel
                            spotList[spotIndex].setLocationAndUpdateTravelDistance(
                                showingTrip,
                                dateIndex,
                                updateTripState,
                                cameraPositionState.position.target,
                                cameraPositionState.position.zoom,
                                setLocationUiState.googleMapsPlacesId
                            )

                            setShowSetLocationDialogToFalse()
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(10.dp, 2.dp, 10.dp, 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterMarker(
    color: Color,
    onColor: Color,
    markerText: String
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(
            text = markerText,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                .copy(color = onColor)
        )
    }
}

@Composable
private fun MapSearchBox(
    visible: Boolean,
    isLoading: Boolean,
    searchText: String,
    onTextChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onSearchClicked: () -> Unit
){
    val boxModifier = Modifier
        .height(50.dp)
        .widthIn(max = 450.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceBright)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + scaleIn(tween(400)),
        exit = fadeOut(tween(400)) + scaleOut(tween(400))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (isLoading) boxModifier.searchBoxShimmerEffect()
            else boxModifier
        ) {
            MySpacerRow(width = 20.dp)

            MyTextField(
                inputText = if (searchText == "") null else searchText,
                inputTextStyle = MaterialTheme.typography.bodyLarge,
                placeholderText = stringResource(id = R.string.search_location),
                placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                onValueChange = onTextChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    //search
                    onSearchClicked()
                }),
                modifier = Modifier.weight(1f)
            )

            //if texting show x icon
            if (searchText != "") {
                IconButton(
                    onClick = { onClearClicked() }
                ) {
                    DisplayIcon(icon = MyIcons.clearInputText)
                }
            }

            IconButton(
                enabled = searchText != "",
                onClick = { onSearchClicked() }
            ) {
                DisplayIcon(icon = MyIcons.searchLocation)
            }

            MySpacerRow(width = 0.dp)
        }
    }
}

@Composable
private fun MapSearchList(
    visible: Boolean,
    locationAutoFillList: List<LocationInfo>,
    selectedPlaceId: String?,
    onClickItem: (LocationInfo) -> Unit,
    onSearchListSizeChanged: (IntSize) -> Unit
){
    val itemHeight = 62.dp
    val horizontalPadding = 16.dp

    val selected = stringResource(id = R.string.selected)
    val notSelected = stringResource(id = R.string.not_selected)
    val select = stringResource(id = R.string.select)

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
                animationSpec = tween(durationMillis = 300),
                expandFrom = Alignment.Top
            ) + fadeIn(tween(300)),
        exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300),
                shrinkTowards = Alignment.Top
            ) + fadeOut(tween(300))
    ) {
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .heightIn(max = 200.dp)
                .widthIn(max = 450.dp)
                .background(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.93f))
                .onSizeChanged {
                    onSearchListSizeChanged(it)
                }
        ){
            if (locationAutoFillList.isEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                    )
                }
            }

            items(locationAutoFillList) {
                val itemColor = if (selectedPlaceId == it.placeId) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent

                Column {
                    ClickableBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .semantics {
                                stateDescription =
                                    if (selectedPlaceId == it.placeId) selected else notSelected
                                onClick(
                                    label = select,
                                    action = {
                                        onClickItem(it)
                                        true
                                    }
                                )
                            },
                        containerColor = itemColor,
                        shape = RoundedCornerShape(25.dp),
                        onClick = { onClickItem(it) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontalPadding, 0.dp)
                        ) {
                            //
                            if (it.mapMarkerIndex != null) {

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFbbbbbb.toInt()))
                                ) {
                                    Text(
                                        text = it.mapMarkerIndex!!,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF000000.toInt())
                                    )
                                }

                                MySpacerRow(8.dp)
                            }

                            //
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                            ) {
                                Text(
                                    text = it.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )

                                if (it.address != "") {
                                    MySpacerColumn(height = 1.dp)

                                    Text(
                                        text = it.address,
                                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    if (locationAutoFillList.last() != it){
                        ItemDivider(20.dp, 20.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateListWithCircle(
    visible: Boolean,
    dateTimeFormat: DateTimeFormat,
    dateList: List<Date>
){
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + scaleIn(tween(400)),
        exit = fadeOut(tween(400)) + scaleOut(tween(400))
    ) {
        MyCard(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.93f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(min = 90.dp)
                    .heightIn(max = 200.dp),
                contentPadding = PaddingValues(12.dp, 10.dp)
            ) {
                for (date in dateList) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //colored circle
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(date.color.color))
                            )

                            MySpacerRow(width = 8.dp)

                            //date
                            Text(
                                text = date.getDateText(
                                    dateTimeFormat.copy(includeDayOfWeek = false),
                                    false
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (dateList.last() != date)
                            MySpacerColumn(height = 6.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowOtherDateSpotMarkersCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
){
    val on = stringResource(id = R.string.on)
    val off = stringResource(id = R.string.off)
    val toggle = stringResource(id = R.string.toggle)

    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .semantics(mergeDescendants = true) {
                stateDescription = if (checked) on else off
                onClick(
                    label = toggle,
                    action = null
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = stringResource(id = R.string.show_other_date_spot_marker),
            modifier = Modifier.padding(0.dp, 8.dp, 16.dp, 8.dp),
            maxLines = 2
        )
    }
}






private fun mapAnimateToLatLng(
    locationList: List<LocationInfo>,
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    coroutineScope: CoroutineScope,
){
    coroutineScope.launch {
        focusOnToLatLngForSetLocation(
            cameraPositionState,
            mapSize,
            locationList.map { it.location },
            cameraPositionState.position.zoom
        )
    }
}

fun Modifier.searchBoxShimmerEffect(): Modifier = composed {
    var size by remember{
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "infinite_transition")
    val startOffsetX by transition.animateFloat(
        initialValue = -1 * size.width.toFloat(),
        targetValue = 1 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(900)
        ),
        label = "shimmer_effect"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceBright,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.surfaceBright,
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

