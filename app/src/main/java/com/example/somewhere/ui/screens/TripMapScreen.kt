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
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.typeUtils.SpotTypeGroup
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MapForTrip
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.UserLocationButton
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screenUtils.cards.SpotTypeCard
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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
    navigateUp: () -> Unit,
    userLocationEnabled: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    setUserLocationEnabled: (userLocationEnabled: Boolean) -> Unit,

    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    val initialLocation = currentTrip.getFirstLocation()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation!!, 5f)
    }

    var isControlExpanded by rememberSaveable{ mutableStateOf(true) }
    var mapSize by remember{ mutableStateOf(IntSize(0,0)) }

    //List<Pair<Date, Boolean>>
    var dateListWithShownMarkerList by rememberSaveable{ mutableStateOf( getDateListWithShownMarkerList(currentTrip) ) }
    //List<Pair<SpotTypeGroup, Boolean>>
    var spotTypeGroupWithShownMarkerList by rememberSaveable{ mutableStateOf( getSpotTypeGroupWithShownMarkerList() ) }

    var focusOnToSpotEnabled by rememberSaveable{ mutableStateOf(true) }

    var currentDateIndex by rememberSaveable{ mutableStateOf(0) }
    var oneDateShown by rememberSaveable{ mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    oneDateShown = checkOneDateShown(currentDateIndex, dateListWithShownMarkerList)

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
                dateListWithShownMarkerList = dateListWithShownMarkerList,
                spotTypeGroupWithShownMarkerList = spotTypeGroupWithShownMarkerList,
                firstFocusOnToSpot = {
                    focusOnToSpot(mapSize, coroutineScope, dateListWithShownMarkerList, spotTypeGroupWithShownMarkerList, cameraPositionState)
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
                    focusOnToSpotEnabledChanged = {
                        focusOnToSpotEnabled = it
                    },

                    isDateShown = oneDateShown,

                    currentDateIndex = currentDateIndex,

                    onDateClicked = {
                        val newList = dateListWithShownMarkerList.map {
                            if (dateListWithShownMarkerList.indexOf(it) == currentDateIndex) Pair(
                                it.first,
                                true
                            )
                            else Pair(it.first, false)
                        }

                        dateListWithShownMarkerList = newList

                        focusOnToSpot(mapSize, coroutineScope, dateListWithShownMarkerList, spotTypeGroupWithShownMarkerList, cameraPositionState)

                        if (isControlExpanded)
                            coroutineScope.launch {
                                dateListState.animateScrollToItem(currentDateIndex)
                            }
                    },
                    onPreviousDateClicked = {
                        currentDateIndex = when (currentDateIndex) {
                            0 -> dateListWithShownMarkerList.indexOf(dateListWithShownMarkerList.last())
                            else -> currentDateIndex - 1
                        }

                        val newList = dateListWithShownMarkerList.map {
                            if (dateListWithShownMarkerList.indexOf(it) == currentDateIndex) Pair(
                                it.first,
                                true
                            )
                            else Pair(it.first, false)
                        }

                        dateListWithShownMarkerList = newList

                        focusOnToSpot(mapSize, coroutineScope, dateListWithShownMarkerList, spotTypeGroupWithShownMarkerList, cameraPositionState)

                        if (isControlExpanded)
                            coroutineScope.launch {
                                dateListState.animateScrollToItem(currentDateIndex)
                            }
                    },
                    onNextDateClicked = {
                        currentDateIndex = when (currentDateIndex) {
                            dateListWithShownMarkerList.indexOf(dateListWithShownMarkerList.last()) -> 0
                            else -> currentDateIndex + 1
                        }

                        val newList = dateListWithShownMarkerList.map {
                            if (dateListWithShownMarkerList.indexOf(it) == currentDateIndex) Pair(
                                it.first,
                                true
                            )
                            else Pair(it.first, false)
                        }

                        dateListWithShownMarkerList = newList

                        focusOnToSpot(mapSize, coroutineScope, dateListWithShownMarkerList, spotTypeGroupWithShownMarkerList, cameraPositionState)

                        if (isControlExpanded)
                            coroutineScope.launch {
                                dateListState.animateScrollToItem(currentDateIndex)
                            }
                    },

                    cameraPositionState = cameraPositionState,
                    dateListWithShownIconList = dateListWithShownMarkerList,
                    spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
                    setUserLocationEnabled = setUserLocationEnabled,
                    fusedLocationClient = fusedLocationClient,
                    onBackButtonClicked = navigateUp
                )

                SpotTypeList(
                    spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
                    onSpotTypeItemClicked = { spotType ->
                        val newList = spotTypeGroupWithShownMarkerList.map {
                            if (it.first == spotType) Pair(it.first, !it.second)
                            else it
                        }

                        spotTypeGroupWithShownMarkerList = newList
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
                        dateListState = dateListState,
                        dateListWithShownIconList = dateListWithShownMarkerList,
                        onDateItemClicked = { date ->

                            val newList = dateListWithShownMarkerList.map {
                                if (it.first == date) Pair(it.first, !it.second)
                                else it
                            }

                            dateListWithShownMarkerList = newList
                        }
                    )
                }
            }
        }
    }
}

private fun checkOneDateShown(
    currentDateIndex: Int,
    dateListWithShownIconList: List<Pair<Date, Boolean>>
): Boolean{
    for (datePair in dateListWithShownIconList){
        if (dateListWithShownIconList.indexOf(datePair) == currentDateIndex && !datePair.second)
            return false
        else if (dateListWithShownIconList.indexOf(datePair) != currentDateIndex && datePair.second)
            return false
    }
    return true
}

private fun getDateListWithShownMarkerList(
    trip: Trip
): List<Pair<Date, Boolean>>{
    val list: MutableList<Pair<Date, Boolean>> = mutableListOf()

    for (date in trip.dateList){
        val pair = Pair(date, true)
        list.add(pair)
    }

    return list
}

private fun getSpotTypeGroupWithShownMarkerList(

): List<Pair<SpotTypeGroup, Boolean>>{
    val list: MutableList<Pair<SpotTypeGroup, Boolean>> = mutableListOf()

    val spotTypeGroupList = enumValues<SpotTypeGroup>()

    for (spotTypeGroup in spotTypeGroupList){
        if (spotTypeGroup != SpotTypeGroup.MOVE)    //not include MOVE
            list.add(Pair(spotTypeGroup, true))
    }

    return list
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
private fun ButtonsRow(
    isExpanded: Boolean,
    mapSize: IntSize,
    onExpandButtonClicked: () -> Unit,

    focusOnToTargetEnabled: Boolean,
    focusOnToSpotEnabledChanged: (Boolean) -> Unit,

    isDateShown: Boolean,
    currentDateIndex: Int,
    onDateClicked: () -> Unit,
    onPreviousDateClicked: () -> Unit,
    onNextDateClicked: () -> Unit,

    cameraPositionState: CameraPositionState,

    dateListWithShownIconList:List<Pair<Date, Boolean>>,
    spotTypeGroupWithShownIconList: List<Pair<SpotTypeGroup, Boolean>>,

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
                    focusOnToSpotEnabledChanged = focusOnToSpotEnabledChanged,
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
                    onClick = onDateClicked,
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,
                    modifier = Modifier
                        .width(60.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    Box(contentAlignment = Alignment.Center){
                        Text(
                            text = dateListWithShownIconList[currentDateIndex].first.getDateText(false),
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
    focusOnToSpotEnabledChanged: (Boolean) -> Unit,
    cameraPositionState: CameraPositionState,
    dateListWithShownIconList:List<Pair<Date, Boolean>>,
    spotTypeGroupWithShownIconList: List<Pair<SpotTypeGroup, Boolean>>
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val spotTypeShownList = mutableListOf<SpotTypeGroup>()
    val toastText = stringResource(id = R.string.toast_no_location_to_show)

    for (spotType in spotTypeGroupWithShownIconList){
        if (spotType.second)
            spotTypeShownList.add(spotType.first)
    }

    focusOnToSpotEnabledChanged(
        checkFocusOnToSpotEnabled(dateListWithShownIconList,spotTypeGroupWithShownIconList)
    )

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

private fun checkFocusOnToSpotEnabled(
    dateListWithShownIconList: List<Pair<Date, Boolean>>,
    spotTypeWithShownIconList: List<Pair<SpotTypeGroup, Boolean>>,
): Boolean {
    for (datePair in dateListWithShownIconList){
        if (datePair.second) {
            for (spot in datePair.first.spotList) {
                if (Pair(spot.spotType.group, true or false) in spotTypeWithShownIconList &&
                    spot.location != null){

                    return true
                }
            }
        }
    }
    return false
}

private fun focusOnToSpot(
    mapSize: IntSize,
    coroutineScope: CoroutineScope,
    dateListWithShownMarkerList: List<Pair<Date, Boolean>>,
    spotTypeWithShownMarkerList: List<Pair<SpotTypeGroup, Boolean>>,
    cameraPositionState: CameraPositionState,
){
    val spotList: MutableList<Spot> = mutableListOf()

    for (date in dateListWithShownMarkerList) {
        if (date.second) {
            for (spot in date.first.spotList) {
                if (Pair(spot.spotType.group, true or false) in spotTypeWithShownMarkerList &&
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
    spotTypeGroupWithShownIconList: List<Pair<SpotTypeGroup, Boolean>>,
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
                SpotTypeCard(
                    spotTypeGroup = it.first,
                    textStyle = textStyle,
                    isShown = it.second,
                    isShownColor = isShownColor,
                    isNotShownColor = isNotShownColor,
                    onCardClicked = {spotTypeGroup ->
                        onSpotTypeItemClicked(spotTypeGroup)
                    }
                )

                MySpacerRow(width = 12.dp)
            }
        }
    }
}

@Composable
private fun DateList(
    dateListState: LazyListState,
    dateListWithShownIconList: List<Pair<Date, Boolean>>,
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
                date = it.first,
                isShown = it.second,
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
                    text = date.getDateText(false),
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
                    style = if(isShown) titleTextStyle
                            else        titleNullTextStyle
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
