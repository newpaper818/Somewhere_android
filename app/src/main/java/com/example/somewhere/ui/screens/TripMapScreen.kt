package com.example.somewhere.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
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
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MapForTrip
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.UserLocationButton
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screenUtils.cards.SpotTypeGroupCard
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

@Composable
fun TripMapScreen(
    currentTrip: Trip,
    dateTimeFormat: DateTimeFormat,
    navigateUp: () -> Unit,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    modifier: Modifier = Modifier,
//    tripMapViewModel: TripMapViewModel = TripMapViewModel(currentTrip)
//        viewModel(factory = SomewhereViewModelProvider.Factory)
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

    var isControlExpanded by rememberSaveable{ mutableStateOf(true) }
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }


    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateListState = rememberLazyListState()


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        //map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onSizeChanged {
                    mapSize = it
                }
        ) {
            MapForTrip(
                context = context,
                userLocationEnabled = userLocationEnabled,
                cameraPositionState = cameraPositionState,
                dateListWithShownMarkerList = dateWithShownMarkerList,
                spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                firstFocusOnToSpot = {
                    focusOnToSpot(mapSize, coroutineScope, dateWithShownMarkerList, spotTypeGroupWithShownMarkerList, cameraPositionState)
                }
            )
        }

        //control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(getColor(ColorType.BACKGROUND))
        ) {

            Column(
                modifier = Modifier
            ) {
                MySpacerColumn(height = 8.dp)

                ButtonsRow(
                    isExpanded = isControlExpanded,
                    mapSize = mapSize,
                    onExpandButtonClicked = {
                        isControlExpanded = !isControlExpanded
                    },

                    focusOnToTargetEnabled = focusOnToSpotEnabled,

                    isDateShown = oneDateShown,
                    dateTimeFormat = dateTimeFormat,

                    currentDateIndex = currentDateIndex,

                    onOneDateClicked = {
                        val newDateShownList = tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
                        focusOnToSpot(mapSize, coroutineScope, newDateShownList, spotTypeGroupWithShownMarkerList, cameraPositionState)
                        animateToDate(coroutineScope, isControlExpanded, dateListState, currentDateIndex)
                    },
                    onPreviousDateClicked = {
                        val newCurrentDateIndex = tripMapViewModel.currentDateIndexToPrevious()
                        val newDateShownList = tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
                        focusOnToSpot(mapSize, coroutineScope, newDateShownList, spotTypeGroupWithShownMarkerList, cameraPositionState)
                        animateToDate(coroutineScope, isControlExpanded, dateListState, newCurrentDateIndex)
                    },
                    onNextDateClicked = {
                        val newCurrentDateIndex = tripMapViewModel.currentDateIndexToNext()
                        val newDateShownList = tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
                        focusOnToSpot(mapSize, coroutineScope, newDateShownList, spotTypeGroupWithShownMarkerList, cameraPositionState)
                        animateToDate(coroutineScope, isControlExpanded, dateListState, newCurrentDateIndex)
                    },

                    cameraPositionState = cameraPositionState,
                    dateListWithShownIconList = dateWithShownMarkerList,
                    spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
                    setUserLocationEnabled = setUserLocationEnabled,
                    fusedLocationClient = fusedLocationClient,
                    onBackButtonClicked = navigateUp
                )

                SpotTypeList(
                    spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
                    onSpotTypeItemClicked = { spotTypeGroup ->
                        tripMapViewModel.toggleSpotTypeGroupWithShownMarkerList(spotTypeGroup)
                    }
                )

                MySpacerColumn(height = 8.dp)

                AnimatedVisibility(
                    visible = isControlExpanded,
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
        }
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

@OptIn(ExperimentalMaterialApi::class)
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

    onBackButtonClicked: () -> Unit,
){
    Row(
        modifier = Modifier.padding(4.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //back button
        Card(
            elevation = 0.dp,
            backgroundColor = getColor(ColorType.BACKGROUND)
        ) {
            IconButton(onClick = onBackButtonClicked) {
                DisplayIcon(icon = MyIcons.back)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // my location & focus on target buttons
        Card(
            modifier = Modifier.clip(RoundedCornerShape(30.dp)),
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //my location button
                UserLocationButton(fusedLocationClient , cameraPositionState, setUserLocationEnabled)

                //focus on to target button
                FocusOnToSpotButton(
                    mapSize = mapSize,
                    focusOnToTargetEnabled = focusOnToTargetEnabled,
                    cameraPositionState = cameraPositionState,
                    dateListWithShownIconList = dateListWithShownIconList,
                    spotTypeGroupWithShownIconList = spotTypeGroupWithShownIconList
                )
            }
        }

        MySpacerRow(width = 16.dp)

        //date button  <  3.28  >
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp)),
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onPreviousDateClicked
                ) {
                    DisplayIcon(icon = MyIcons.leftArrow)
                }

                val dateTextStyle = if (isDateShown)    getTextStyle(TextType.BUTTON)
                else                getTextStyle(TextType.BUTTON_NULL)

                Card(
                    onClick = onOneDateClicked,
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    modifier = Modifier
                        .width(60.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    Box(contentAlignment = Alignment.Center){
                        Text(
                            text = dateListWithShownIconList[currentDateIndex].date.getDateText(dateTimeFormat.copy(includeDayOfWeek = false), false),
                            style = dateTextStyle
                        )
                    }
                }

                IconButton(
                    onClick = onNextDateClicked
                ) {
                    DisplayIcon(icon = MyIcons.rightArrow)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //expand collapse button
        Card(
            elevation = 0.dp,
            backgroundColor = getColor(ColorType.BACKGROUND)
        ){
            IconButton(onClick = onExpandButtonClicked) {
                DisplayIcon(icon = if (isExpanded)  MyIcons.collapseControlBox
                                    else            MyIcons.expandControlBox)
            }
        }
    }
}



@Composable
private fun FocusOnToSpotButton(
    mapSize: IntSize,
    focusOnToTargetEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    dateListWithShownIconList:List<DateWithBoolean>,
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val spotTypeShownList = mutableListOf<SpotTypeGroup>()
    val toastText = stringResource(id = R.string.toast_no_location_to_show)

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
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
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

    coroutineScope.launch {
        com.example.somewhere.ui.screenUtils.focusOnToSpot(
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
    ){
        items(spotTypeGroupWithShownIconList){

            Row {
                SpotTypeGroupCard(
                    spotTypeGroup = it.spotTypeGroup,
                    defaultTextStyle = textStyle,
                    isShown = it.isShown,
                    isNotShownColor = isNotShownColor,
                    onCardClicked = {spotTypeGroup ->
                        onSpotTypeItemClicked(spotTypeGroup)
                    },
                    shownColor = isShownColor,
                )

                MySpacerRow(width = 12.dp)
            }
        }
    }
}

@Composable
private fun DateList(
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    dateListWithShownIconList: List<DateWithBoolean>,
    onDateItemClicked: (Date) -> Unit,
){
    LazyColumn(
        state = dateListState,
        contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp),
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth()
            .height(320.dp)
    ){
        item {
            StartEndDummySpaceWithRoundedCorner(
                isFirst = true,
                isLast = false
            )
        }
        items(dateListWithShownIconList){
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

            MySpacerColumn(height = 160.dp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
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

    Card(
        backgroundColor = getColor(ColorType.CARD),
        elevation = 0.dp,
        onClick = { onItemClicked(date) },
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
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
                    .width(50.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = date.getDateText(dateTimeFormat.copy(includeDayOfWeek = false), false),
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
