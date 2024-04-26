package com.newpaper.somewhere.core.ui.tripScreenUtils

import androidx.annotation.ColorInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.GraphColor
import com.newpaper.somewhere.core.designsystem.theme.whiteInt
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.R

private val DUMMY_SPACE_HEIGHT: Dp = 10.dp
private val MIN_CARD_HEIGHT: Dp = 40.dp
private val ADDITIONAL_HEIGHT: Dp = 22.dp
private val POINT_CIRCLE_SIZE: Dp = 24.dp
private val LINE_WIDTH: Dp = 7.dp

@Composable
fun GraphListItem(
    isEditMode: Boolean,
    isExpanded: Boolean,

    sideText: String,
    mainText: String?,
    expandedText: String?,
    isTextSizeLimit: Boolean,

    onMainTextChange: (mainText: String) -> Unit,

    isFirstItem: Boolean,
    isLastItem: Boolean,
    deleteEnabled: Boolean,
    dragEnabled: Boolean,

    onItemClick: () -> Unit,
    onExpandedButtonClicked: () -> Unit,

    modifier: Modifier = Modifier,  //item modifier
    dateDragModifier: Modifier = Modifier,
    spotDragModifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,

    isHighlighted: Boolean = false,

    onDeleteClick: () -> Unit = { },
    onSideTextClick: (() -> Unit)? = null,
    onPointClick: (() -> Unit)? = null,

    iconText: String? = null,
    @ColorInt iconTextColor: Int? = null,

    isShown: Boolean = true,
    sideTextPlaceHolderIcon: MyIcon? = null,

    itemColor: Color = MaterialTheme.colorScheme.surfaceBright,
    pointColor: Color = GraphColor.point,
    lineColor: Color = GraphColor.line
){
    if (!isShown)
        return


    val upperLineColor = if (isFirstItem) Color.Transparent
                            else lineColor

    val lowerLineColor = if (isLastItem) Color.Transparent
                            else lineColor

    val borderColor = if (isTextSizeLimit) CustomColor.outlineError
                        else Color.Transparent

    val clickableBoxColor = if (isHighlighted) pointColor.copy(alpha = 0.2f)
                        else Color.Transparent


    Box(modifier.background(itemColor)) {
        ClickableBox(
            containerColor = clickableBoxColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.border(1.dp, borderColor, MaterialTheme.shapes.medium),
            onClick = {
                if (!isEditMode)
                    onItemClick()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {

                //upper dummy space
                DummySpaceWithLine(lineColor = upperLineColor)

                //side text / point, line / main text / expand icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MIN_CARD_HEIGHT)
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    MySpacerRow(width = (40.dp - POINT_CIRCLE_SIZE) / 2)

                    //side text
                    SideText(
                        text = sideText,
                        isEditMode = isEditMode,
                        onClick = if (isEditMode) onSideTextClick ?: { }
                                    else onItemClick
                        ,
                        spotDragModifier = spotDragModifier,
                        sideTextPlaceHolderIcon = sideTextPlaceHolderIcon
                    )

                    //point, line
                    PointWithLine(
                        iconText = iconText,
                        onClick = if (isEditMode) onPointClick ?: { }
                                    else onItemClick,
                        iconTextColor = iconTextColor,
                        pointColor = pointColor,
                        upperLineColor = upperLineColor,
                        lowerLineColor = lowerLineColor,
                        spotDragModifier = spotDragModifier
                    )

                    MySpacerRow(width = 10.dp)

                    //main text modifier
                    val dragModifier = if (spotDragModifier != Modifier) spotDragModifier
                                        else if (dateDragModifier != Modifier) dateDragModifier
                                        else Modifier

                    Box(modifier = dragModifier.weight(1f)) {
                        //main text
                        MainText(
                            isEditMode = isEditMode,
                            mainText = mainText,
                            onValueChange = onMainTextChange
                        )
                    }

                    Icons(
                        isEditMode = isEditMode,
                        deleteEnabled = deleteEnabled,
                        dragEnabled = dragEnabled,
                        dragHandleModifier = dragHandleModifier,
                        expandedTextIsNull = expandedText == null,
                        isExpanded = isExpanded,
                        onClickDelete = onDeleteClick,
                        onClickExpanded = {
                            onExpandedButtonClicked()
                        }
                    )
                }

                //expanded text animate
                if (expandedText != null) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter =
                        expandVertically(
                            animationSpec = tween(durationMillis = 300),
                            expandFrom = Alignment.Bottom
                        ),
                        exit =
                        shrinkVertically(
                            animationSpec = tween(durationMillis = 300),
                            shrinkTowards = Alignment.Bottom
                        )
                    ) {
                        //expanded space
                        ExpandedTextWithLine(
                            expandedText = expandedText,
                            lineColor = lowerLineColor
                        )
                    }
                }

                //lower dummy space
                DummySpaceWithLine(lineColor = lowerLineColor)
            }
        }
    }
}


@Composable
private fun SideText(
    text: String,
    isEditMode: Boolean,
    onClick: () -> Unit,
    spotDragModifier: Modifier,
    sideTextPlaceHolderIcon: MyIcon?
){
    ClickableBox(
        modifier = spotDragModifier,
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .width(70.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (text != "") {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            else if (sideTextPlaceHolderIcon != null && isEditMode) {
                DisplayIcon(icon = sideTextPlaceHolderIcon)
            }
        }
    }
}

@Composable
private fun PointWithLine(
    iconText: String?,
    onClick: () -> Unit,
    @ColorInt iconTextColor: Int?,
    pointColor: Color,
    upperLineColor: Color,
    lowerLineColor: Color,
    spotDragModifier: Modifier,
){
    ClickableBox(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .width(40.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
        onClick = onClick
    ) {

        Column {
            //upper line
            Box(
                modifier = Modifier
                    .width(LINE_WIDTH)
                    .height(MIN_CARD_HEIGHT / 2)
                    .background(upperLineColor)
            )

            //lower line
            Box(
                modifier = Modifier
                    .width(LINE_WIDTH)
                    .height(MIN_CARD_HEIGHT / 2)
                    .background(lowerLineColor)
            )
        }

        //point
        Box(
            contentAlignment = Alignment.Center,
            modifier = spotDragModifier
                .size(if (iconText != null) POINT_CIRCLE_SIZE else POINT_CIRCLE_SIZE - 6.dp)
                .clip(CircleShape)
                .background(pointColor)
        ) {
            if (iconText != null) {
                val textStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold).copy(
                    color = Color(iconTextColor ?: whiteInt)
                )
                Text(
                    text = iconText,
                    style = textStyle
                )
            }
        }
    }
}

@Composable
private fun MainText(
    isEditMode: Boolean,
    mainText: String?,
    onValueChange: (String) -> Unit
){

    val mainText1 = if(mainText == null || mainText == "") stringResource(id = R.string.no_title)
                    else mainText

    val mainTextStyle = if(mainText == null || mainText == "") MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        else MaterialTheme.typography.bodyLarge

    if (!isEditMode) {
        Text(
            text = mainText1,
            style = mainTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    } else {
        val focusManager = LocalFocusManager.current

        MyTextField(
            inputText = mainText,
            inputTextStyle = MaterialTheme.typography.bodyLarge,
            placeholderText = stringResource(id = R.string.add_a_title),
            placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
    }
}

@Composable
private fun Icons(
    isEditMode: Boolean,
    deleteEnabled: Boolean,
    dragEnabled: Boolean,
    dragHandleModifier: Modifier,
    expandedTextIsNull: Boolean,
    isExpanded: Boolean,
    onClickDelete: () -> Unit,
    onClickExpanded: () -> Unit
){
    //delete icon
    AnimatedVisibility(
        visible = isEditMode && deleteEnabled,
        enter = scaleIn(tween(300)),
        exit = scaleOut(tween(400)) + fadeOut(tween(300))
    ) {
        IconButton(onClick = onClickDelete) {
            DisplayIcon(
                icon = MyIcons.delete,
                descriptionTextId = R.string.delete_spot
            )
        }
    }

    //drag handle
    AnimatedVisibility(
        visible = isEditMode && dragEnabled,
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

    //expand / collapse icon
    AnimatedVisibility(
        visible = !isEditMode,
        enter = expandHorizontally(tween(400)),
        exit = shrinkHorizontally(tween(300))
    ) {
        if (!expandedTextIsNull)
            IconButton(onClick = onClickExpanded) {
                if (!isExpanded)
                    DisplayIcon(icon = MyIcons.expand)
                else
                    DisplayIcon(icon = MyIcons.collapse)
            }
    }
}

/**
 * when user click expand button, this will show
 */
@Composable
private fun ExpandedTextWithLine(
    expandedText: String,
    lineColor: Color
){
    Row(
        modifier = Modifier
            .height(ADDITIONAL_HEIGHT)
            .fillMaxWidth()
    ) {
        //dummy date/time
        MySpacerRow((40.dp - POINT_CIRCLE_SIZE) / 2)
        MySpacerRow(70.dp)

        //line
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(LINE_WIDTH)
                    .fillMaxHeight()
                    .background(lineColor)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = expandedText,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

/**
 * at GraphListItem's upper and lower
 */
@Composable
fun DummySpaceWithLine(
    height: Dp = DUMMY_SPACE_HEIGHT,
    lineColor: Color = GraphColor.line
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ){
        //dummy date/time
        MySpacerRow((40.dp - POINT_CIRCLE_SIZE) / 2)
        MySpacerRow(70.dp)

        //line
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(LINE_WIDTH)
                    .fillMaxHeight()
                    .background(lineColor)
            )
        }

        //dummy (40.dp - line) / 2
        MySpacerRow(width = (40.dp - LINE_WIDTH) / 2)
    }
}

@Composable
fun StartEndDummySpaceWithRoundedCorner(
    isFirst: Boolean,
    isLast: Boolean,

    modifier: Modifier = Modifier,
    showLine: Boolean = false
){
    val upperRoundedCornerValue =
        if (isFirst)    16.dp
        else            0.dp

    val lowerRoundedCornerValue =
        if (isLast)     16.dp
        else            0.dp

    MyCard(
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(
                RoundedCornerShape(
                    topStart = upperRoundedCornerValue,
                    topEnd = upperRoundedCornerValue,
                    bottomStart = lowerRoundedCornerValue,
                    bottomEnd = lowerRoundedCornerValue
                )
            )
    ){
        if(showLine)
            DummySpaceWithLine(height = 16.dp)
    }
}