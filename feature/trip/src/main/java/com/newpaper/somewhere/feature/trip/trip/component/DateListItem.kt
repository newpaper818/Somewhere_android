package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.tripScreenUtils.ADDITIONAL_HEIGHT
import com.newpaper.somewhere.core.ui.tripScreenUtils.DUMMY_SPACE_HEIGHT
import com.newpaper.somewhere.core.ui.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.core.ui.tripScreenUtils.MIN_CARD_HEIGHT
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getExpandedText
import com.newpaper.somewhere.core.utils.convert.setTitleText
import com.newpaper.somewhere.core.utils.dragAndDropVertical
import kotlin.math.roundToInt

@Composable
internal fun DateListItem(
    trip: Trip,
    date: Date,

    isEditMode: Boolean,
    isHighlighted: Boolean,

    dateTimeFormat: DateTimeFormat,

    slideState: SlideState,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,
    isTextSizeLimit: Boolean,

    onItemClick: () -> Unit,
    onSideTextClick: (() -> Unit)? = null,
    onPointClick: (() -> Unit)? = null
){
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    //get item height(px), use at drag reorder
    var itemHeight: Int
    val unExpandedItemHeight = MIN_CARD_HEIGHT + DUMMY_SPACE_HEIGHT * 2
    val expandedItemHeight = MIN_CARD_HEIGHT + DUMMY_SPACE_HEIGHT * 2 + ADDITIONAL_HEIGHT

    with(LocalDensity.current){
        itemHeight = if (isExpanded) expandedItemHeight.toPx().toInt()
                     else            unExpandedItemHeight.toPx().toInt()
    }

    //is dragged
    var isDragged by remember { mutableStateOf(false) }
    val zIndex = if (isDragged) 1.0f else 0.0f

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        },
        label = "vertical translation"
    )

    //item y offset
    val itemOffsetY = remember { Animatable(0f) }

    //item modifier
    val dragModifier =
        //set y offset while dragging or drag end
        if (isEditMode) Modifier
            .offset {
                if (isDragged) IntOffset(0, itemOffsetY.value.roundToInt())
                else IntOffset(0, verticalTranslation)
            }
            .zIndex(zIndex)
        else Modifier

    //item ui
    GraphListItem(
        modifier = dragModifier,
        dragHandleModifier = Modifier
            .dragAndDropVertical(
                item = date,
                items = trip.dateList,
                itemHeight = itemHeight,
                updateSlideState = updateSlideState,
                offsetY = itemOffsetY,
                onStartDrag = {
                    isDragged = true
                },
                onStopDrag = { currentIndex, destinationIndex ->

                    if (currentIndex != destinationIndex){
                        updateItemPosition(currentIndex, destinationIndex)
                    }

                    isDragged = false
                }
            ),

        pointColor = Color(date.color.color),
        isEditMode = isEditMode,
        isExpanded = isExpanded,
        isHighlighted = isHighlighted,

        sideText = date.getDateText(dateTimeFormat.copy(includeDayOfWeek = false), false),
        mainText = date.titleText,
        expandedText = date.getExpandedText(trip, isEditMode),
        isTextSizeLimit = isTextSizeLimit,

        onMainTextChange = { mainText ->
            trip.dateList[date.index].setTitleText(trip, updateTripState, mainText)
        },

        isFirstItem = date == trip.dateList.first(),
        isLastItem = date == trip.dateList.last() || !(trip.dateList.getOrNull(date.index + 1)?.enabled ?: true),
        deleteEnabled = false,
        dragEnabled = true,

        onItemClick = onItemClick,
        onExpandedButtonClicked = {
            isExpanded = !isExpanded
        },
        onSideTextClick = onSideTextClick,
        onPointClick = onPointClick
    )
}