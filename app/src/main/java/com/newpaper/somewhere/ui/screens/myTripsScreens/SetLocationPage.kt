package com.newpaper.somewhere.ui.screens.myTripsScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.screenUtils.settingScreenUtils.ItemDivider
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.DEFAULT_ZOOM_LEVEL
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.MapChooseLocation
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.MyTextField
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.SaveCancelButtons
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.ZoomCard
import com.newpaper.somewhere.viewModel.LocationInfo
import com.newpaper.somewhere.viewModel.SetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val SEARCH_BOX_LIST_PADDING = 14.dp

@Composable
fun SetLocationPage(
    showingTrip: Trip,
    spotList: List<Spot>,
    currentSpotIndex: Int,
    dateList: List<Date>,
    dateIndex: Int,

    isDarkMapTheme: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    toggleIsEditLocationMode: () -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    val firstLocation = spotList[currentSpotIndex].getPrevLocation(dateList, dateIndex)
    var newLocation: LatLng by rememberSaveable { mutableStateOf(firstLocation) }
    var newZoomLevel: Float by rememberSaveable { mutableFloatStateOf(spotList[currentSpotIndex].zoomLevel ?: DEFAULT_ZOOM_LEVEL) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, 5f)
    }

    var screenWidthDp by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current.density

    var searchText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }
    var searchListSize by remember{ mutableStateOf(IntSize(0,0)) }

    val context = LocalContext.current

    val setLocationViewModel = viewModel<SetLocationViewModel>(
        factory = object: ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SetLocationViewModel(context) as T
            }
        }
    )

    var keyBoardIsShown by rememberSaveable { mutableStateOf(false) }

    val mapPaddingValues = if (setLocationViewModel.searchLocationList.isNotEmpty()) PaddingValues(0.dp, 64.dp, 0.dp,
        (searchListSize.height / density).dp + SEARCH_BOX_LIST_PADDING
    )
    else    PaddingValues(0.dp, 64.dp, 0.dp, 0.dp)

    //edit location
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
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
            MapChooseLocation(
                context = LocalContext.current,
                mapPadding = mapPaddingValues,
                isDarkMapTheme = isDarkMapTheme,
                cameraPositionState = cameraPositionState,
                dateList = dateList,
                dateIndex = dateIndex,
                spotList = spotList,
                currentDate = dateList[dateIndex],
                currentSpot = spotList[currentSpotIndex],
                showSearchLocationMarker = searchText != "",
                searchLocationMarkerList = setLocationViewModel.searchLocationList,
                onMapLoaded = {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(firstLocation, spotList[currentSpotIndex].zoomLevel ?: DEFAULT_ZOOM_LEVEL), 300
                        )
                    }
                },
                onLocationChange = { newLocation_ ->
                    newLocation = newLocation_
                },
                onZoomChange = {newZoomLevel_ ->
                    newZoomLevel = newZoomLevel_
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
                    color = Color(spotList[currentSpotIndex].spotType.group.color.color),
                    onColor = Color(spotList[currentSpotIndex].spotType.group.color.onColor),
                    markerText = spotList[currentSpotIndex].iconText.toString()
                )
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 700.dp)
                    .fillMaxHeight()
                    .padding(SEARCH_BOX_LIST_PADDING)
            ) {

                //search box
                MapSearchBox(
                    isLoading = setLocationViewModel.isLoading,
                    searchText = searchText,
                    onTextChanged = {
                        keyBoardIsShown = true
                        searchText = it
                        if (it.isNotEmpty()) {
                            setLocationViewModel.isLoadingSearchPlaces = true

                            coroutineScope.launch {
                                setLocationViewModel.searchPlaces(it)
                                setLocationViewModel.isLoadingSearchPlaces = false
                            }
                        }
                        
                    },
                    onClearClicked = {
                        searchText = ""
                        setLocationViewModel.searchLocationList.clear()
                    },
                    onSearchClicked = {
                        setLocationViewModel.isLoading = true
                        focusManager.clearFocus()
                        keyBoardIsShown = false

                        coroutineScope.launch {
                            setLocationViewModel.updateAllLatLng()
                            mapAnimateToLatLng(setLocationViewModel.searchLocationList ,cameraPositionState, mapSize, coroutineScope)
                            setLocationViewModel.isLoading = false
                        }
                    }
                )

                if (!keyBoardIsShown)
                    Spacer(modifier = Modifier.weight(1f))
                else
                    MySpacerColumn(height = 8.dp)

                //list of find places
                MapSearchList(
                    visible = searchText.isNotEmpty() && setLocationViewModel.searchLocationList.isNotEmpty(),
                    locationAutoFillList = setLocationViewModel.searchLocationList,
                    onClickItem = {
                        //get one location from touched item
                        focusManager.clearFocus()
                        keyBoardIsShown = false

                        if (it.location != null){
                            mapAnimateToLatLng(listOf(it) ,cameraPositionState, mapSize, coroutineScope)
                        }
                        else {
                            coroutineScope.launch {
                                setLocationViewModel.updateOneLatLng(it)
                                mapAnimateToLatLng(listOf(it) ,cameraPositionState, mapSize, coroutineScope)
                            }
                        }
                    },
                    onSearchListSizeChanged = {
                        searchListSize = it
                    }
                )
            }
        }

//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(320.dp),
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier.fillMaxWidth()
//        ){
//            item {
//                //zoom card
////                Box(modifier = Modifier.width(335.dp)) {
//                    ZoomCard(
//                        zoomLevel = newZoomLevel,
//                        mapZoomTo = { newZoomLevel_ ->
//                            newZoomLevel = newZoomLevel_
//                            coroutineScope.launch {
//                                cameraPositionState.animate(
//                                    CameraUpdateFactory.zoomTo(newZoomLevel), 300
//                                )
//                            }
//                        },
//                        fusedLocationClient = fusedLocationClient,
//                        cameraPositionState = cameraPositionState,
//                        setUserLocationEnabled = setUserLocationEnabled,
//                        showSnackBar = showSnackBar
//                    )
////                }
//            }
//            item {
//                //cancel save buttons
//                SaveCancelButtons(
//                    onCancelClick = toggleIsEditLocationMode,
//                    onSaveClick = {
//                        //set location and zoom level
//                        //spotList[currentSpotId].zoomLevel = zoomLevel
//                        spotList[currentSpotId].setLocation(
//                            showingTrip,
//                            dateId,
//                            updateTripState,
//                            newLocation,
//                            newZoomLevel
//                        )
//                        toggleIsEditLocationMode()
//                    },
//                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
//                    positiveText = stringResource(id = R.string.dialog_button_ok)
//                )
//            }
//        }


        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.weight(1f)
            ) {
                ZoomCard(
                    zoomLevel = newZoomLevel,
                    mapZoomTo = { newZoomLevel1 ->
                        newZoomLevel = newZoomLevel1
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

            if (screenWidthDp >= 670){
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.weight(1f)
                ) {
                    //cancel save buttons
                    SaveCancelButtons(
                        onCancelClick = toggleIsEditLocationMode,
                        onSaveClick = {
                            //set location and zoom level
                            //spotList[currentSpotId].zoomLevel = zoomLevel
                            spotList[currentSpotIndex].setLocation(
                                showingTrip,
                                dateIndex,
                                updateTripState,
                                newLocation,
                                newZoomLevel
                            )

                            toggleIsEditLocationMode()
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        positiveText = stringResource(id = R.string.dialog_button_ok)
                    )
                }
            }
        }
        if (screenWidthDp < 670) {

            MySpacerColumn(height = 8.dp)

            //cancel save buttons
            SaveCancelButtons(
                onCancelClick = toggleIsEditLocationMode,
                onSaveClick = {
                    spotList[currentSpotIndex].setLocationAndUpdateTravelDistance(
                        showingTrip, dateIndex, updateTripState, newLocation, newZoomLevel
                    )

                    toggleIsEditLocationMode()
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                positiveText = stringResource(id = R.string.dialog_button_ok)
            )
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
            style = getTextStyle(TextType.PROGRESS_BAR__ICON_TEXT_HIGHLIGHT)
                .copy(color = onColor)
        )
    }
}

@Composable
private fun MapSearchBox(
    isLoading: Boolean,
    searchText: String,
    onTextChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onSearchClicked: () -> Unit
){
    val boxModifier = Modifier
        .height(50.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (isLoading) boxModifier.searchBoxShimmerEffect()
                    else boxModifier
    ) {
        MySpacerRow(width = 20.dp)

        MyTextField(
            inputText = if (searchText == "") null else searchText,
            inputTextStyle = getTextStyle(TextType.MAP__SEARCH),
            placeholderText = stringResource(id = R.string.search_location),
            placeholderTextStyle = getTextStyle(TextType.MAP__SEARCH_PLACEHOLDER),
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
                DisplayIcon(icon = MyIcons.mapClearSearchText)
            }

        IconButton(
            enabled = searchText != "",
            onClick = {
                //search
                onSearchClicked()
            }
        ) {
            DisplayIcon(icon = MyIcons.mapSearch)
        }

        MySpacerRow(width = 0.dp)
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
            ),
        exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300),
                shrinkTowards = Alignment.Top
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .heightIn(max = 200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                .onSizeChanged {
                    onSearchListSizeChanged(it)
                }
        ){
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
                                style = getTextStyle(TextType.MAP__SEARCH_LIST_TITLE),
                                maxLines = 1
                            )

                            if (it.address != "") {
                                MySpacerColumn(height = 1.dp)

                                Text(
                                    text = it.address,
                                    style = getTextStyle(TextType.MAP__SEARCH_LIST_SUBTITLE),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    if (locationAutoFillList.last() != it){
                        ItemDivider(horizontalPadding)
                    }
                }
            }
        }
    }
}

private fun mapAnimateToLatLng(
    locationList: List<LocationInfo>,
    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    coroutineScope: CoroutineScope
){
    coroutineScope.launch {
        com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.focusOnToLatLng(
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
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.surfaceVariant,
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}