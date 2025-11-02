package com.newpaper.somewhere.feature.trip.tripMap.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.button.FilterChipButton
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MapButtonIcon
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.DateWithBoolean
import com.newpaper.somewhere.core.model.data.SpotTypeGroupWithBoolean
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getSpotTypeGroupCount
import com.newpaper.somewhere.core.utils.getTalkbackDateText
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripMap.TripMapViewModel
import com.newpaper.somewhere.feature.trip.tripMap.fitBoundsToMarkers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun BottomSheetHandel(

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

/**
have to Column()
*/
@Composable
internal fun ControlPanel(
    tripMapViewModel: TripMapViewModel,
    coroutineScope: CoroutineScope,

    cameraPositionState: CameraPositionState,
    mapSize: IntSize,
    fitBoundsToMarkersEnabled: Boolean,
    oneDateShown: Boolean,

    dateTimeFormat: DateTimeFormat,
    currentDateIndex: Int,

    dateListState: LazyListState,
    spotTypeGroupWithShownMarkerList:List<SpotTypeGroupWithBoolean>,
    dateWithShownMarkerList: List<DateWithBoolean>,

    navigateUp: () -> Unit,
    snackBarHostState: SnackbarHostState,
){
    ControlButtonsRow(
        mapSize = mapSize,
        fitBoundsToMarkersEnabled = fitBoundsToMarkersEnabled,
        isDateShown = oneDateShown,
        dateTimeFormat = dateTimeFormat,
        currentDateIndex = currentDateIndex,
        onClickOneDate = {
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            fitBoundsToMarkers(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            scrollToDate(
                coroutineScope,
                dateListState,
                currentDateIndex
            )
        },
        onClickPreviousDate = {
            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToPrevious()
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            fitBoundsToMarkers(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            scrollToDate(
                coroutineScope,
                dateListState,
                newCurrentDateIndex
            )
        },
        onClickNextDate = {
            val newCurrentDateIndex = tripMapViewModel.currentDateIndexToNext()
            val newDateShownList =
                tripMapViewModel.updateDateWithShownMarkerListToCurrentDate()
            fitBoundsToMarkers(
                mapSize,
                coroutineScope,
                newDateShownList,
                spotTypeGroupWithShownMarkerList,
                cameraPositionState
            )
            scrollToDate(
                coroutineScope,
                dateListState,
                newCurrentDateIndex
            )
        },

        cameraPositionState = cameraPositionState,
        dateListWithShownIconList = dateWithShownMarkerList,
        spotTypeGroupWithShownIconList = spotTypeGroupWithShownMarkerList,
        onBackButtonClicked = navigateUp,
        showSnackBar = { text, actionLabel, duration ->
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = text,
                    actionLabel = actionLabel,
                    duration = duration
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

    AnimatedVisibility(
        visible = true,
        enter = expandVertically(
                animationSpec = tween(durationMillis = 400),
                expandFrom = Alignment.Top
            ),
        exit = shrinkVertically(
                animationSpec = tween(durationMillis = 400),
                shrinkTowards = Alignment.Top
            )
    ) {
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

@Composable
internal fun ControlButtonsRow(
    mapSize: IntSize,

    fitBoundsToMarkersEnabled: Boolean,

    isDateShown: Boolean,
    dateTimeFormat: DateTimeFormat,
    currentDateIndex: Int,
    onClickOneDate: () -> Unit,
    onClickPreviousDate: () -> Unit,
    onClickNextDate: () -> Unit,

    cameraPositionState: CameraPositionState,

    dateListWithShownIconList:List<DateWithBoolean>,
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,

    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit,

    onBackButtonClicked: () -> Unit,
){
    Row(
        modifier = Modifier.padding(4.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //back button
        MyPlainTooltipBox(tooltipText = stringResource(id = TopAppBarIcon.back.descriptionTextId!!)) {
            IconButton(onClick = onBackButtonClicked) {
                DisplayIcon(icon = TopAppBarIcon.back)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.width(278.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // my location & focus on target buttons
            Row(
                modifier = Modifier
                    .clip(SmoothRoundedCornerShape(999.dp, 1f))
                    .background(MaterialTheme.colorScheme.surfaceBright),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                //fit bounds to markers button
                FitBoundsToMarkersButton(
                    mapSize = mapSize,
                    fitBoundsToMarkersEnabled = fitBoundsToMarkersEnabled,
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
                    .clip(SmoothRoundedCornerShape(999.dp, 1f))
                    .background(MaterialTheme.colorScheme.surfaceBright),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.prevDate.descriptionTextId!!)) {
                    IconButton(
                        onClick = onClickPreviousDate
                    ) {
                        DisplayIcon(icon = MapButtonIcon.prevDate)
                    }
                }

                val dateTextStyle = if (isDateShown) MaterialTheme.typography.labelLarge
                else MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(40.dp)
                        .clip(SmoothRoundedCornerShape(999.dp, 1f))
                        .clickable { onClickOneDate() },
                    contentAlignment = Alignment.Center
                ) {
                    if (dateListWithShownIconList.isNotEmpty()) {
                        Text(
                            text = dateListWithShownIconList[currentDateIndex].date.getDateText(
                                dateTimeFormat.copy(includeDayOfWeek = false),
                                false
                            ),
                            style = dateTextStyle,
                            modifier = Modifier.semantics{
                                contentDescription = getTalkbackDateText(dateListWithShownIconList[currentDateIndex].date.date, dateTimeFormat.copy(includeDayOfWeek = false),
                                    false)
                            }
                        )
                    }
                }

                MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.nextDate.descriptionTextId!!)) {
                    IconButton(
                        onClick = onClickNextDate
                    ) {
                        DisplayIcon(icon = MapButtonIcon.nextDate)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(40.dp))
    }
}



@Composable
internal fun FitBoundsToMarkersButton(
    mapSize: IntSize,
    fitBoundsToMarkersEnabled: Boolean,
    cameraPositionState: CameraPositionState,
    dateListWithShownIconList:List<DateWithBoolean>,
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit
){
    val coroutineScope = rememberCoroutineScope()
    val spotTypeShownList = mutableListOf<SpotTypeGroup>()
    val snackBarText = stringResource(id = R.string.snackbar_no_location_to_show)

    for (spotTypeWithBoolean in spotTypeGroupWithShownIconList){
        if (spotTypeWithBoolean.isShown)
            spotTypeShownList.add(spotTypeWithBoolean.spotTypeGroup)
    }

    if (fitBoundsToMarkersEnabled){
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.fitBoundsToMarkers.descriptionTextId!!)) {
            IconButton(
                onClick = {
                    fitBoundsToMarkers(
                        mapSize = mapSize,
                        coroutineScope = coroutineScope,
                        dateListWithShownMarkerList = dateListWithShownIconList,
                        spotTypeWithShownMarkerList = spotTypeGroupWithShownIconList,
                        cameraPositionState = cameraPositionState
                    )
                }
            ) {
                DisplayIcon(icon = MapButtonIcon.fitBoundsToMarkers)
            }
        }
    }
    else{
        MyPlainTooltipBox(tooltipText = stringResource(id = MapButtonIcon.disabledFitBoundsToMarkers.descriptionTextId!!)) {
            IconButton(
                onClick = {
                    showSnackBar(snackBarText, null, SnackbarDuration.Short)
                }
            ) {
                DisplayIcon(icon = MapButtonIcon.disabledFitBoundsToMarkers)
            }
        }
    }
}


@Composable
internal fun SpotTypeList(
    spotTypeGroupWithShownIconList: List<SpotTypeGroupWithBoolean>,
    onSpotTypeItemClicked: (SpotTypeGroup) -> Unit,
    isShownColor: Color = MaterialTheme.colorScheme.primary
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(16.dp, 12.dp, 4.dp, 12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(spotTypeGroupWithShownIconList) {

            Row {
                FilterChipButton(
                    text = stringResource(it.spotTypeGroup.textId),
                    selected = it.isShown,
                    onClick = { onSpotTypeItemClicked(it.spotTypeGroup) },

                    selectedButtonColor = isShownColor,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    notSelectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

                MySpacerRow(width = 12.dp)
            }
        }
    }
}

@Composable
internal fun DateList(
    dateTimeFormat: DateTimeFormat,
    dateListState: LazyListState,
    dateListWithShownIconList: List<DateWithBoolean>,
    onDateItemClicked: (Date) -> Unit,
){
    LazyColumn(
        state = dateListState,
        modifier = Modifier
            .padding(16.dp, 0.dp, 16.dp, 16.dp)
            .fillMaxWidth()
            .clip(SmoothRoundedCornerShape(16.dp))
    ) {
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
    }
}

@Composable
internal fun DateItem(
    date: Date,
    dateTimeFormat: DateTimeFormat,
    isShown: Boolean,
    onItemClicked: (Date) -> Unit
){
    val shown = stringResource(id = R.string.shown)
    val hidden = stringResource(id = R.string.hidden)
    val toggle = stringResource(id = R.string.toggle)

    ClickableBox(
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .semantics {
                stateDescription = if (isShown) shown else hidden
                onClick(
                    label = toggle,
                    action = {
                        onItemClicked(date)
                        true
                    }
                )
            },
        onClick = { onItemClicked(date) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //circle
            Row(modifier = Modifier.size(15.dp)) {
                AnimatedVisibility(
                    visible = isShown,
                    enter = scaleIn(tween(200)),
                    exit = scaleOut(tween(200))
                ) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(Color(date.color.color))
                    )
                }
            }


            MySpacerRow(width = 12.dp)

            //date text
            Box(
                modifier = Modifier
                    .width(58.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = date.getDateText(dateTimeFormat.copy(includeDayOfWeek = false), false),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.semantics{
                        contentDescription = getTalkbackDateText(date.date, dateTimeFormat.copy(includeDayOfWeek = false), false)
                    }
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
                    style = if (isShown && date.titleText != null)  MaterialTheme.typography.bodyLarge
                    else                                    MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MySpacerRow(width = 2.dp)

            //spot count
            Text(
                text = "${date.spotList.size - date.getSpotTypeGroupCount(SpotTypeGroup.MOVE)} " + stringResource(id = R.string.spots),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}




























private fun scrollToDate(
    coroutineScope: CoroutineScope,
    dateListState: LazyListState,
    currentDateIndex: Int
){
    coroutineScope.launch {
        dateListState.animateScrollToItem(currentDateIndex)
    }
}