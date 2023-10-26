package com.newpaper.somewhere.ui.tripScreens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.model.Trip
import com.newpaper.somewhere.ui.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.tripScreenUtils.DEFAULT_ZOOM_LEVEL
import com.newpaper.somewhere.ui.tripScreenUtils.MapChooseLocation
import com.newpaper.somewhere.ui.tripScreenUtils.MyTextField
import com.newpaper.somewhere.ui.tripScreenUtils.SaveCancelButtons
import com.newpaper.somewhere.ui.tripScreenUtils.cards.ZoomCard
import kotlinx.coroutines.launch

@Composable
fun SetLocationPage(
    showingTrip: Trip,
    spotList: List<Spot>,
    currentSpotId: Int,
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

    val firstLocation = spotList[currentSpotId].getPrevLocation(dateList, dateIndex)
    var newLocation: LatLng by rememberSaveable { mutableStateOf(firstLocation) }

    var newZoomLevel: Float by rememberSaveable { mutableFloatStateOf(spotList[currentSpotId].zoomLevel ?: DEFAULT_ZOOM_LEVEL) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstLocation, newZoomLevel)
    }

    var screenWidthDp by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current.density

    var searchText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

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
        ) {
            MapChooseLocation(
                context = LocalContext.current,
                mapPadding = PaddingValues(0.dp, 62.dp, 0.dp, 0.dp),
                isDarkMapTheme = isDarkMapTheme,
                cameraPositionState = cameraPositionState,
                dateList = dateList,
                dateIndex = dateIndex,
                spotList = spotList,
                currentDate = dateList[dateIndex],
                currentSpot = spotList[currentSpotId],
                onLocationChange = { newLocation_ ->
                    newLocation = newLocation_
                },
                onZoomChange = {newZoomLevel_ ->
                    newZoomLevel = newZoomLevel_
                }
            )

            //search box
            MapSearchBox(
                searchText = searchText,
                focusManager = focusManager,
                onTextChanged = { searchText = it },
                onSearchClicked = {
                    //search api
                }
            )

            //center marker
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(spotList[currentSpotId].spotType.group.color.color))
            ){
                Text(
                    text = spotList[currentSpotId].iconText.toString(),
                    style = getTextStyle(TextType.PROGRESS_BAR__ICON_TEXT_HIGHLIGHT)
                        .copy(color = Color(spotList[currentSpotId].spotType.group.color.onColor))
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
                            spotList[currentSpotId].setLocation(
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
                    spotList[currentSpotId].setLocationAndUpdateTravelDistance(
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
private fun MapSearchBox(
    searchText: String,
    focusManager: FocusManager,
    onTextChanged: (String) -> Unit,
    onSearchClicked: () -> Unit
){
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(78.dp)
                .widthIn(max = 500.dp)
                .padding(14.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            MySpacerRow(width = 16.dp)

            MyTextField(
                inputText = if (searchText == "") null else searchText,
                inputTextStyle = getTextStyle(TextType.MAP__SEARCH),
                placeholderText = stringResource(id = R.string.search_location),
                placeholderTextStyle = getTextStyle(TextType.MAP__SEARCH_PLACEHOLDER),
                onValueChange = onTextChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    //search
                    onSearchClicked()
                }),
                modifier = Modifier.weight(1f)
            )

            //if texting show x icon
            if (searchText != "")
                IconButton(onClick = {
                    onTextChanged("")
                }) {
                    DisplayIcon(icon = MyIcons.mapClearSearchText)
                }

            IconButton(onClick = {
                focusManager.clearFocus()
                //search
                onSearchClicked()
            }) {
                DisplayIcon(icon = MyIcons.mapSearch)
            }

            MySpacerRow(width = 0.dp)
        }
    }
}

private fun getMapLocation(
    searchText: String,
    context: Context
){

}