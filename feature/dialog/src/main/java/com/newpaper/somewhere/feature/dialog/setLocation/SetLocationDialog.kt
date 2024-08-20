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
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.SaveCancelButtons
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
import com.newpaper.somewhere.core.utils.focusOnToLatLng
import com.newpaper.somewhere.feature.dialog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val SEARCH_BOX_LIST_PADDING = 14.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetLocationDialog(
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
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
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration, onActionClick: () -> Unit) -> Unit,

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


    val mapPaddingValues = if (setLocationUiState.searchLocation.searchLocationList.isNotEmpty())
        PaddingValues(16.dp, 64.dp, 12.dp,
        (searchListSize.height / density).dp + SEARCH_BOX_LIST_PADDING)
    else    PaddingValues(16.dp, 64.dp, 12.dp, 0.dp)





    MyScaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .displayCutoutPadding(),
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
            //google map
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onSizeChanged {
                        mapSize = it
                    }
            ) {
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

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(mapPaddingValues)
                        .fillMaxSize()
                ) {
                    //center marker
                    CenterMarker(
                        color = if (setLocationUiState.showOtherDateSpotMarkers) Color(dateList[dateIndex].color.color)
                                else Color(spotList[spotIndex].spotType.group.color.color),
                        onColor = if (setLocationUiState.showOtherDateSpotMarkers) Color(dateList[dateIndex].color.onColor)
                                else Color(spotList[spotIndex].spotType.group.color.onColor),
                        markerText = spotList[spotIndex].iconText.toString()
                    )
                }

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
                            if (!setLocationUiState.keyboardIsShown)
                                setLocationViewModel.setKeyboardIsShown(true)

                            setLocationViewModel.setSearchText(it)

                            if (it.isNotEmpty()) {
                                setLocationViewModel.setIsLoadingSearchPlaces(true)

                                coroutineScope.launch {
                                    setLocationViewModel.searchPlaces(it)
                                    setLocationViewModel.setIsLoadingSearchPlaces(false)
                                }
                            }

                        },
                        onClearClicked = {
                            setLocationViewModel.setSearchText("")
                            setLocationViewModel.clearSearchLocationList()
                        },
                        onSearchClicked = {
                            if (setLocationUiState.searchLocation.searchText != "")
                                coroutineScope.launch {
                                    setLocationViewModel.setIsLoading(true)
                                    focusManager.clearFocus()
                                    setLocationViewModel.setKeyboardIsShown(false)

                                    setLocationViewModel.updateAllLatLng()
                                    mapAnimateToLatLng(
                                        setLocationUiState.searchLocation.searchLocationList,
                                        cameraPositionState,
                                        mapSize,
                                        coroutineScope
                                    )
                                    setLocationViewModel.setIsLoading(false)
                                }
                        }
                    )

                    if (!setLocationUiState.keyboardIsShown)
                        Spacer(modifier = Modifier.weight(1f))
                    else
                        MySpacerColumn(height = 8.dp)


                    //date with colored circle
                    Box(modifier = Modifier.fillMaxWidth()) {
                        DateListWithCircle(
                            visible = setLocationUiState.showOtherDateSpotMarkers &&
                                    !setLocationUiState.keyboardIsShown,
                            dateTimeFormat = dateTimeFormat,
                            dateList = dateList
                        )
                    }

                    if (!setLocationUiState.keyboardIsShown
                        && setLocationUiState.searchLocation.searchLocationList.isNotEmpty()
                        && setLocationUiState.showOtherDateSpotMarkers)
                        MySpacerColumn(height = 16.dp)



                    //list of find places
                    MapSearchList(
                        visible = setLocationUiState.searchLocation.searchText.isNotEmpty()
                                && setLocationUiState.searchLocation.searchLocationList.isNotEmpty(),
                        locationAutoFillList = setLocationUiState.searchLocation.searchLocationList,
                        onClickItem = {
                            //get one location from touched item
                            focusManager.clearFocus()
                            setLocationViewModel.setKeyboardIsShown(false)

                            if (it.location != null) {
                                mapAnimateToLatLng(
                                    listOf(it),
                                    cameraPositionState,
                                    mapSize,
                                    coroutineScope
                                )
                            } else {
                                coroutineScope.launch {
                                    setLocationViewModel.updateOneLatLng(it)
                                    mapAnimateToLatLng(
                                        listOf(it),
                                        cameraPositionState,
                                        mapSize,
                                        coroutineScope
                                    )
                                }
                            }
                        },
                        onSearchListSizeChanged = {
                            searchListSize = it
                        }
                    )
                }
            }

            //buttons
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) { },
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
                        showSnackBar = showSnackBar
                    )
                }

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .width(330.dp)
                ) {
                    //cancel save buttons
                    SaveCancelButtons(
                        onClickCancel = setShowSetLocationDialogToFalse,
                        onClickSave = {
                            //set location and zoom level
                            //spotList[currentSpotId].zoomLevel = zoomLevel
                            spotList[spotIndex].setLocationAndUpdateTravelDistance(
                                showingTrip,
                                dateIndex,
                                updateTripState,
                                cameraPositionState.position.target,
                                cameraPositionState.position.zoom
                            )

                            setShowSetLocationDialogToFalse()
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
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
        .widthIn(max = 600.dp)
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
            if (searchText != "")
                IconButton(
                    onClick = {
                        onClearClicked()
                    }
                ) {
                    DisplayIcon(icon = MyIcons.clearInputText)
                }

            IconButton(
                enabled = searchText != "",
                onClick = {
                    //search
                    onSearchClicked()
                }
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
    onClickItem: (LocationInfo) -> Unit,
    onSearchListSizeChanged: (IntSize) -> Unit
){
    val itemHeight = 62.dp
    val horizontalPadding = 20.dp

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
                .widthIn(max = 600.dp)
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
                Column {
                    ClickableBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        onClick = { onClickItem(it) }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontalPadding, 0.dp)
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
                    if (locationAutoFillList.last() != it){
                        ItemDivider(horizontalPadding, horizontalPadding)
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
            modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 20.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.93f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp, 10.dp)
            ) {
                for (date in dateList) {
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

                        MySpacerRow(width = 6.dp)

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
                        MySpacerColumn(height = 4.dp)
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
    Row(
        modifier = Modifier.clickable { onCheckedChange(!checked) },
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
    coroutineScope: CoroutineScope
){
    coroutineScope.launch {
        focusOnToLatLng(
            cameraPositionState,
            mapSize,
            locationList.map { it.location },
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