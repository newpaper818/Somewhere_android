package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getStartEndDateText
import com.newpaper.somewhere.core.utils.enterHorizontally
import com.newpaper.somewhere.core.utils.exitHorizontally
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.tripCardHeightDp

@Composable
internal fun TripItem(
    trip: Trip,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier,

    showDeleteIcon: Boolean = false,
    firstLaunch: Boolean = false,

    onClick: (() -> Unit)? = null,
    onClickDelete: () -> Unit = {}
){
    val titleIsNull = trip.titleText == null || trip.titleText == ""
    val titleText: String = if (titleIsNull) stringResource(id = R.string.no_title)
                            else trip.titleText!!

    val dateText = trip.getStartEndDateText(dateTimeFormat.copy(includeDayOfWeek = false))
                    ?: stringResource(id = R.string.no_date)

    val haptic = LocalHapticFeedback.current


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
        showDeleteIcon = showDeleteIcon,
        title = titleText,
        titleIsNull = titleIsNull,
        dateText = dateText,
        onClick = onClick,
        onClickDelete = onClickDelete,
        downloadImage = downloadImage,
        modifier = modifier,
    )
}










@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TripItemUi(
    alpha: Float,
    internetEnabled: Boolean,

    imagePath: String?,
    tripManagerId: String,
    showDeleteIcon: Boolean,

    title: String,
    titleIsNull: Boolean,
    dateText: String,

    onClick: (() -> Unit)?,
    onClickDelete: () -> Unit,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier,
){
    var cardModifier = Modifier
        .clip(MaterialTheme.shapes.large)
        .alpha(alpha)

    //onClick
    cardModifier = if (onClick != null){
            cardModifier.combinedClickable(
                onClick = onClick
            )
        }
        else cardModifier

    val deleteText = stringResource(R.string.delete)

    //talkback
    cardModifier = if (onClick == null) {
        cardModifier.semantics{
            onClick(
                label = deleteText,
                action = {
                    onClickDelete()
                    true
                }
            )
        }
    }
    else cardModifier


    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) { }
    ) {
        MyCard(
            modifier = cardModifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tripCardHeightDp)
                    .padding(12.dp, 12.dp, 0.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //image
                if (imagePath != null) {
                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .clip(RoundedCornerShape(11.dp)),
                    ) {
                        ImageFromFile(
                            internetEnabled = internetEnabled,
                            imageUserId = tripManagerId,
                            imagePath = imagePath,
                            contentDescription = stringResource(id = R.string.image),
                            downloadImage = downloadImage,
                            modifier = Modifier
                                .fillMaxSize()
                                .clearAndSetSemantics { },
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    MySpacerColumn(height = 8.dp)

                    //trip start date - end date
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                MySpacerRow(width = 4.dp)

                AnimatedVisibility(
                    visible = !showDeleteIcon,
                    enter = enterHorizontally,
                    exit = exitHorizontally
                ) {
                    MySpacerRow(width = 8.dp)
                }

                //delete icon when edit mode
                AnimatedVisibility(
                    visible = showDeleteIcon,
                    enter = enterHorizontally,
                    exit = exitHorizontally
                ) {
                    Row(
                        modifier = Modifier.clearAndSetSemantics { }
                    ) {
                        IconButton(
                            onClick = onClickDelete
                        ) {
                            DisplayIcon(MyIcons.delete)
                        }

                        MySpacerRow(width = 4.dp)
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
            showDeleteIcon = false,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "title",
            titleIsNull = false,
            dateText = "3.14 - 3.16",
            onClick = { },
            onClickDelete = { },
            downloadImage = {_,_,_ ->}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripItemPreview2(){
    SomewhereTheme {
        TripItemUi(
            showDeleteIcon = true,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "title",
            titleIsNull = false,
            dateText = "3.14 - 3.16",
            onClick = { },
            onClickDelete = { },
            downloadImage = {_,_,_ ->}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripItemPreview3(){
    SomewhereTheme {
        TripItemUi(
            showDeleteIcon = false,
            alpha = 1f,
            internetEnabled = true,
            imagePath = null,
            tripManagerId = "",
            title = "No title",
            titleIsNull = true,
            dateText = "No date",
            onClick = { },
            onClickDelete = { },
            downloadImage = {_,_,_ ->}
        )
    }
}