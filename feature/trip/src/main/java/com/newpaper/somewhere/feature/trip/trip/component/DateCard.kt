package com.newpaper.somewhere.feature.trip.trip.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.core.model.enums.SpotType
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.card.trip.TitleLayout
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.enterVertically
import com.newpaper.somewhere.core.utils.exitVertically
import com.newpaper.somewhere.feature.trip.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

@Composable
internal fun DateCard(
    visible: Boolean,
    trip: Trip,
    dateIndex: Int,

    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,
    focusManager: FocusManager,

    slideState: SlideState,
    upperItemHeight: Float,
    lowerItemHeight: Float,
    onItemHeightChanged: (dateId: Int, itemHeight: Float) -> Unit,
//    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
//    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    onDateTitleTextChange: (title: String) -> Unit,
    isLongText: (Boolean) -> Unit,

    onClickDateMoveUp: () -> Unit,
    onClickDateMoveDown: () -> Unit,
    onClickSetDateColor: () -> Unit,
    onSpotTitleTextChange: (spot: Spot, title: String) -> Unit,
    onClickSpotItem: (spot: Spot) -> Unit,
    onClickDeleteSpot: (spot: Spot) -> Unit,
    onClickSpotSideText: (spot: Spot) -> Unit,
    onClickSpotPoint: (spot: Spot) -> Unit,
    reorderSpotList: (dateIndex: Int, currentSpotIndex: Int, destinationSpotIndex: Int) -> Unit,

    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val currentDate = trip.dateList[dateIndex]
    val spotList = trip.dateList[dateIndex].spotList


    //spot slideStates
    val spotSlideStates = remember { mutableStateMapOf(
        *trip.dateList[dateIndex].spotList.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    //date
    //get item height(px), use at drag reorder
    var itemHeight by rememberSaveable { mutableFloatStateOf(0f) }

    val verticalTranslation by animateFloatAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -upperItemHeight
            SlideState.DOWN -> lowerItemHeight
            else -> 0f
        },
        animationSpec = tween(400)
    )


    //item modifier
    val dragModifier =
        //set y offset while dragging or drag end
        if (isEditMode) Modifier
            .offset { IntOffset(0, verticalTranslation.toInt()) }
        else Modifier


    Column(
        modifier = dragModifier.onSizeChanged{
            itemHeight = it.height.toFloat() + with(density){ 24.dp.toPx() }
            onItemHeightChanged(trip.dateList[dateIndex].id, itemHeight)
        }
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(tween(500)),
            exit = shrinkVertically(tween(500))
        ) {
            Column {
                //date
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentDate.getDateText(
                            dateTimeFormat = dateTimeFormat,
                            includeYear = false
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = enterVertically,
                        exit = exitVertically
                    ) {
                        MoveDateButtonsWithText(
                            onClickUp = onClickDateMoveUp,
                            onClickDown = onClickDateMoveDown
                        )
                    }
                }

                MySpacerColumn(6.dp)
            }
        }


        MyCard(
            modifier = modifier,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = expandVertically(tween(500)),
                exit = shrinkVertically(tween(500))
            ) {
                //set color / date title
                Column {
                    Row(
                        modifier = Modifier.padding(18.dp, 14.dp, 16.dp, 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SetDateColorButton(
                            isEditMode = isEditMode,
                            color = currentDate.color,
                            onClick = onClickSetDateColor
                        )

                        MySpacerRow(width = 16.dp)

                        TitleLayout(
                            isEditMode = isEditMode,
                            upperTitleText = stringResource(R.string.date_title),
                            titleText = currentDate.titleText,
                            onTitleChange = onDateTitleTextChange,
                            focusManager = focusManager,
                            isLongText = isLongText,
                            useUpperTitleAnimation = true
                        )
                    }
                    ItemDivider()
                }
            }

            Column {
                spotList.forEach { spot ->
                    val slideState = spotSlideStates[spot.id] ?: SlideState.NONE

                    AnimatedVisibility(
                        visible = visible,
                        enter = expandVertically(tween(500)),
                        exit = shrinkVertically(tween(500))
                    ) {
                        SpotListItem(
                            trip = trip,
                            dateIndex = dateIndex,
                            spot = spot,
                            isEditMode = isEditMode,
                            timeFormat = dateTimeFormat.timeFormat,

                            slideState = slideState,
                            updateSlideState = { dateId, newSlideState ->
                                spotSlideStates[spotList[dateId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                //on drag end
                                coroutineScope.launch {

                                    //reorder list & update travel distance
                                    reorderSpotList(dateIndex, currentIndex, destinationIndex)

                                    //all slideState to NONE
                                    spotSlideStates.putAll(spotList.map { it.id }
                                        .associateWith { SlideState.NONE })
                                }
                            },
                            onTitleTextChange = { spotTitleText ->
                                onSpotTitleTextChange(spot, spotTitleText)
                            },
                            isLongText = isLongText,
                            onClickItem = { onClickSpotItem(spot) },
                            onClickDelete = { onClickDeleteSpot(spot) },
                            onClickSideText =
                                if (isEditMode) {
                                    { onClickSpotSideText(spot) }
                                } else null,
                            onClickPoint = if (isEditMode) {
                                { onClickSpotPoint(spot) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoveDateButtonsWithText(
    onClickUp: () -> Unit,
    onClickDown: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.move_date),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MySpacerRow(8.dp)

        MoveDateButton(
            icon = MyIcons.moveUp,
            onClick = onClickUp
        )

        MySpacerRow(6.dp)

        MoveDateButton(
            icon = MyIcons.moveDown,
            onClick = onClickDown
        )
    }
}

@Composable
private fun MoveDateButton(
    icon: MyIcon,
    onClick: () -> Unit
){
    ClickableBox(
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        modifier = Modifier
            .clip(CircleShape)
            .size(40.dp),
        contentAlignment = Alignment.Center,
        onClick = onClick
    ) {
        DisplayIcon(icon = icon)
    }
}


@Composable
private fun SetDateColorButton(
    isEditMode: Boolean,
    color: MyColor,
    onClick: () -> Unit,
){
    val scale by animateFloatAsState(
        targetValue = if (isEditMode) 50f else 16f,
        animationSpec = if (isEditMode) tween(450)
                        else tween(350, 160)
    )

    ClickableBox(
        containerColor = Color(color.color),
        modifier = Modifier
            .clip(CircleShape)
            .size(scale.dp),
        contentAlignment = Alignment.Center,
        onClick = onClick
    ) {
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(tween(300, 200)),
            exit = fadeOut(tween(300))
        ) {
            DisplayIcon(icon = MyIcons.setColor, color = Color(color.onColor))
        }
    }
}

















@Composable
@PreviewLightDark
private fun DateCardPreview(){
    SomewhereTheme {
        DateCard(
            visible = true,
            trip = Trip(
                titleText = "this is trip title",
                dateList = listOf(
                    Date(
                        titleText = "this is date title",
                        date = LocalDate.of(2025,10,5),
                        spotList = listOf(
                            Spot(
                                id = 0,
                                index = 0,
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 10"
                            ),
                            Spot(
                                id = 1,
                                index = 1,
                                iconText = 1,
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 2",
                                spotType = SpotType.FOOD
                            ),
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 3",
                                startTime = LocalTime.of(10,0)
                            ),
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 4",
                                spotType = SpotType.ETC
                            )
                        )
                    )
                )
            ),
            dateIndex = 0,
            isEditMode = false,
            dateTimeFormat = DateTimeFormat(),
            focusManager = LocalFocusManager.current,
            slideState = SlideState.NONE,
            upperItemHeight = 0f,
            lowerItemHeight = 0f,
            onItemHeightChanged = {_,_->},
            onDateTitleTextChange = {},
            isLongText = {},
            onClickDateMoveUp = {},
            onClickDateMoveDown = {},
            onClickSetDateColor = {},
            onSpotTitleTextChange = {_,_->},
            onClickSpotItem = {},
            onClickDeleteSpot = {},
            onClickSpotSideText = {},
            onClickSpotPoint = { },
            reorderSpotList = {_,_,_->}
        )
    }
}

@Composable
@PreviewLightDark
private fun DateCardEditPreview(){
    SomewhereTheme {
        DateCard(
            visible = true,
            trip = Trip(
                titleText = "this is trip title",
                dateList = listOf(
                    Date(
                        titleText = "this is date title",
                        date = LocalDate.of(2025,10,5),
                        spotList = listOf(
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 1"
                            ),
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 2",
                                spotType = SpotType.FOOD
                            ),
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 3",
                                startTime = LocalTime.of(10,0)
                            ),
                            Spot(
                                date = LocalDate.of(2025,10,5),
                                titleText = "spot 4",
                                spotType = SpotType.ETC
                            )
                        )
                    )
                )
            ),
            dateIndex = 0,
            isEditMode = false,
            dateTimeFormat = DateTimeFormat(),
            focusManager = LocalFocusManager.current,
            slideState = SlideState.NONE,
            upperItemHeight = 0f,
            lowerItemHeight = 0f,
            onItemHeightChanged = {_,_->},
            onDateTitleTextChange = {},
            isLongText = {},
            onClickDateMoveUp = {},
            onClickDateMoveDown = {},
            onClickSetDateColor = {},
            onSpotTitleTextChange = {_,_->},
            onClickSpotItem = {},
            onClickDeleteSpot = {},
            onClickSpotSideText = {},
            onClickSpotPoint = { },
            reorderSpotList = {_,_,_->}
        )
    }
}