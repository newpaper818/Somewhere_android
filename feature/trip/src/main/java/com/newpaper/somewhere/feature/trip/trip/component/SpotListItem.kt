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
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.enums.TimeFormat
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.tripScreenUtils.ADDITIONAL_HEIGHT
import com.newpaper.somewhere.core.ui.tripScreenUtils.DUMMY_SPACE_HEIGHT
import com.newpaper.somewhere.core.ui.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.core.ui.tripScreenUtils.MIN_CARD_HEIGHT
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getExpandedText
import com.newpaper.somewhere.core.utils.convert.getStartTimeText
import com.newpaper.somewhere.core.utils.convert.isFirstSpot
import com.newpaper.somewhere.core.utils.convert.isLastSpot
import com.newpaper.somewhere.core.utils.dragAndDropVertical
import kotlin.math.roundToInt

@Composable
internal fun SpotListItem(
    trip: Trip,
    dateIndex: Int,
    spot: Spot,

    isEditMode: Boolean,

    timeFormat: TimeFormat,

    slideState: SlideState,
    updateSlideState: (spotIndex: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    onTitleTextChange: (title: String) -> Unit,
    isLongText: (Boolean) -> Unit,

    onClickItem: (() -> Unit)?,
    onClickDelete: () -> Unit,
    onClickSetStartTime: (() -> Unit)?,
    onClickSetSpotType: (() -> Unit)?,

    modifier: Modifier = Modifier
){
    val dateList = trip.dateList
    val spotList = trip.dateList[dateIndex].spotList

    //set point color
    val pointColor =
        if (spot.spotType.isMove())
            Color.Transparent
        else
            Color(spot.spotType.group.color.color)

    //is expanded
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
        targetValue = when (slideState) {
            SlideState.UP -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        }
    )

    //item y offset
    val itemOffsetY = remember { Animatable(0f) }

    //item modifier
    val dragModifier =
        //set y offset while dragging or drag end
        if (isEditMode) modifier
            .offset {
                if (isDragged) IntOffset(0, itemOffsetY.value.roundToInt())
                else IntOffset(0, verticalTranslation)
            }
            .zIndex(zIndex)
        else modifier

    //item ui
    GraphListItem(
        modifier = dragModifier,
        dragHandleModifier = Modifier
            .dragAndDropVertical(
                spot, spotList,
                itemHeight = itemHeight,
                updateSlideState = updateSlideState,
                offsetY = itemOffsetY,
                onStartDrag = { isDragged = true },
                onStopDrag = { currentIndex, destinationIndex ->

                    if (currentIndex != destinationIndex){
                        updateItemPosition(currentIndex, destinationIndex)
                    }

                    isDragged = false
                }
            ),

        pointColor = pointColor,
        isEditMode = isEditMode,
        isExpanded = isExpanded,

        iconText = if (spot.spotType.isNotMove()) spot.iconText.toString()
        else null,
        iconTextColor = spot.spotType.group.color.onColor,

        sideTextPlaceHolderIcon = MyIcons.setTime,
        sideText = spot.getStartTimeText(timeFormat) ?: "",
        mainText = spot.titleText,
        expandedText = spot.getExpandedText(trip, isEditMode),

        onMainTextChange = onTitleTextChange,

        isFirstItem = spot.isFirstSpot(
            dateIndex = dateIndex,
            dateList = dateList,
            spotList = spotList
        ),

        isLastItem = spot.isLastSpot(
            dateIndex = dateIndex,
            dateList = dateList,
            spotList = spotList
        ),

        deleteEnabled = true,
        dragEnabled = true,

        onClickItem = onClickItem,
        onClickExpandedButton = {
            isExpanded = !isExpanded
        },
        onClickDelete = onClickDelete,
        onClickSideText = onClickSetStartTime,
        onClickPoint = onClickSetSpotType,

        isLongText = isLongText
    )
}