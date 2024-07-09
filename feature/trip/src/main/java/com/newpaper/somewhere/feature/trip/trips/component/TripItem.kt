package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getStartEndDateText
import com.newpaper.somewhere.core.utils.dragAndDropVertical
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.tripCardHeightDp
import kotlin.math.roundToInt

@Composable
internal fun TripItem(
    trip: Trip,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier,

    showDragIcon: Boolean = false,
    firstLaunch: Boolean = false,
    trips: List<Trip> = listOf(),

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,

    slideState: SlideState = SlideState.NONE,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit = {_,_ ->},
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit = {_,_ ->},
){
    val titleIsNull = trip.titleText == null || trip.titleText == ""
    val titleText: String = if (titleIsNull) stringResource(id = R.string.no_title)
                            else trip.titleText!!

    val dateText = trip.getStartEndDateText(dateTimeFormat.copy(includeDayOfWeek = false))
                    ?: stringResource(id = R.string.no_date)

    val haptic = LocalHapticFeedback.current


    //drag =========================================================================================
    var cardHeightPx: Int
    var spacerHeightPx: Int
    val spacerHeightDp = 16.dp //between trip card

    with(LocalDensity.current){
        cardHeightPx = tripCardHeightDp.toPx().toInt()
        spacerHeightPx = spacerHeightDp.toPx().toInt()
    }

    var isDragging by remember { mutableStateOf(false) }

    val zIndex = if (isDragging) 1.0f else 0.0f //dragging trip card to front

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        label = "scale"
    )

    //for other trip card that effect from dragging card
    val verticalOffset by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -(cardHeightPx + spacerHeightPx)
            SlideState.DOWN -> cardHeightPx + spacerHeightPx
            else -> 0
        },
        label = "vertical offset"
    )

    //card y offset
    val itemOffsetY = remember { Animatable(0f) }

    val offset =
        if (isDragging) IntOffset(0, itemOffsetY.value.roundToInt())
        else IntOffset(0, verticalOffset)

    val dragModifier =
        if (showDragIcon) modifier
            .offset { offset }
            .scale(scale)
            .zIndex(zIndex)
        else modifier

    //==============================================================================================




    var appear by rememberSaveable { mutableStateOf(!firstLaunch) }
    val alpha: Float by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha",
    )

    LaunchedEffect(Unit){
        if (!appear)
            appear = true
    }





    //==============================================================================================
    TripItemUi(
        alpha = alpha,
        internetEnabled = internetEnabled,
        imagePath = trip.imagePathList.firstOrNull(),
        tripManagerId = trip.managerId,
        showDragIcon = showDragIcon,
        title = titleText,
        titleIsNull = titleIsNull,
        dateText = dateText,
        onClick = onClick,
        onLongClick = if (onLongClick != null) {
            {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick()
            }
        } else null,
        downloadImage = downloadImage,
        modifier = dragModifier,
        dragHandleModifier = Modifier
            .dragAndDropVertical(
                item = trip,
                items = trips,
                itemHeight = cardHeightPx + spacerHeightPx,
                updateSlideState = updateSlideState,
                offsetY = itemOffsetY,
                onStartDrag = {
                    isDragging = true
                },
                onStopDrag = { currentIndex, destinationIndex ->
                    isDragging = false

                    if (currentIndex != destinationIndex){
                        updateItemPosition(currentIndex, destinationIndex)
                    }
                }
            )
    )
}










@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TripItemUi(
    alpha: Float,
    internetEnabled: Boolean,

    imagePath: String?,
    tripManagerId: String,
    showDragIcon: Boolean,

    title: String,
    titleIsNull: Boolean,
    dateText: String,

    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,


    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier
){
    var cardModifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .alpha(alpha)

    if (onClick != null && onLongClick != null)
        cardModifier = cardModifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    else if (onClick == null && onLongClick != null)
        cardModifier = cardModifier.combinedClickable(
            onClick = {},
            onLongClick = onLongClick
        )
    else if (onClick != null)
        cardModifier = cardModifier.combinedClickable(
            onClick = onClick,
            onLongClick = {}
        )

    Box(
        modifier = modifier
    ) {
        MyCard(
            modifier = cardModifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tripCardHeightDp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //image
                if (imagePath != null) {
                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .clip(MaterialTheme.shapes.small),
                    ) {
                        ImageFromFile(
                            internetEnabled = internetEnabled,
                            imageUserId = tripManagerId,
                            imagePath = imagePath,
                            contentDescription = stringResource(id = R.string.image),
                            downloadImage = downloadImage,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    MySpacerRow(width = 12.dp)
                }

                MySpacerRow(width = 4.dp)


                //text title & trip date
                Column(modifier = Modifier.weight(1f)) {

                    //trip title
                    Text(
                        text = title,
                        style = if (titleIsNull) MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                else MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    MySpacerColumn(height = 8.dp)

                    //trip start date - end date
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                MySpacerRow(width = 8.dp)


                //drag handel icon when edit mode
                AnimatedVisibility(
                    visible = showDragIcon,
                    enter = scaleIn(tween(300)),
                    exit = scaleOut(tween(400)) + fadeOut(tween(300))
                ) {
                    IconButton(
                        modifier = dragHandleModifier,
                        //disable touch ripple effect
                        enabled = false,
                        onClick = {  }
                    ) {
                        DisplayIcon(MyIcons.dragHandle)
                    }
                }
            }
        }
    }
}


































@PreviewLightDark
@Composable
private fun TripItemPreview(){
    SomewhereTheme {
        TripItemUi(
            showDragIcon = false,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "title",
            titleIsNull = false,
            dateText = "3.14 - 3.16",
            onClick = { },
            onLongClick = { },
            downloadImage = {_,_,_ ->}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripItemPreview2(){
    SomewhereTheme {
        TripItemUi(
            showDragIcon = true,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "title",
            titleIsNull = false,
            dateText = "3.14 - 3.16",
            onClick = { },
            onLongClick = { },
            downloadImage = {_,_,_ ->}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripItemPreview3(){
    SomewhereTheme {
        TripItemUi(
            showDragIcon = false,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "No title",
            titleIsNull = true,
            dateText = "No date",
            onClick = { },
            onLongClick = { },
            downloadImage = {_,_,_ ->}
        )
    }
}