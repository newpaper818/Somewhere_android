package com.example.somewhere.ui.tripScreenUtils

import androidx.annotation.ColorInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.whiteInt
import com.example.somewhere.ui.tripScreenUtils.cards.MAX_TITLE_LENGTH
import com.example.somewhere.utils.dragAndDrop

val dummySpaceHeight:   Dp = 10.dp
val minCardHeight:      Dp = 40.dp
val additionalHeight:   Dp = 22.dp
val pointCircleSize:    Dp = 22.dp
val lineWidth:          Dp = 7.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GraphListItem(
    isEditMode: Boolean,
    isExpanded: Boolean,

    sideText: String,
    mainText: String?,
    expandedText: String?,

    onTitleTextChange: (titleText: String) -> Unit,

    isFirstItem: Boolean,
    isLastItem: Boolean,
    deleteEnabled: Boolean,
    dragEnabled: Boolean,

    onItemClick: () -> Unit,
    onExpandedButtonClicked: () -> Unit,


    isLongText: (Boolean) -> Unit,

    modifier: Modifier = Modifier,  //item modifier
    dateDragModifier: Modifier = Modifier,
    spotDragModifier: Modifier = Modifier,
    dragHandleModifier: Modifier = Modifier,

    onDeleteClick: () -> Unit = { },
    onSideTextClick: () -> Unit = { },
    onPointClick: () -> Unit = { },

    iconText: String? = null,
    @ColorInt iconTextColor: Int? = null,

    isShown: Boolean = true,

    itemColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    pointColor: Color = getColor(ColorType.GRAPH__POINT),
    lineColor: Color = getColor(ColorType.GRAPH__LINE),

    mainTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__MAIN),
    mainNullTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__MAIN_NULL),
    sideTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__SIDE),
    expandedTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__EXPAND)
) {

    if (!isShown)
        return

    var upperLineColor = lineColor
    var lowerLineColor = lineColor

    if (isFirstItem)
        upperLineColor = Color.Transparent

    if (isLastItem)
        lowerLineColor = Color.Transparent

    val mainText1 = if(mainText == null || mainText == "") stringResource(id = R.string.no_title)
                    else mainText

    val mainTextStyle1 = if(mainText == null || mainText == "") mainNullTextStyle
                            else mainTextStyle

    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    val borderColor = if (isTextSizeLimit) getColor(ColorType.ERROR_BORDER)
                        else Color.Transparent

    LaunchedEffect(isEditMode){
        isTextSizeLimit = (mainText ?: "").length > MAX_TITLE_LENGTH
    }


    Box(modifier.background(itemColor)) {
        ClickableBox(
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
                        .height(minCardHeight)
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    MySpacerRow(width = (40.dp - pointCircleSize) / 2)
                    
                    //side text
                    ClickableBox(
                        modifier = spotDragModifier,
                        shape = MaterialTheme.shapes.small,
                        enabled = isEditMode,
                        onClick = onSideTextClick
                    ) {
                        Row(
                            modifier = Modifier
                                .width(70.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = sideText,
                                style = sideTextStyle
                            )
                        }
                    }

                    //point, line
                    ClickableBox(
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                        enabled = isEditMode,
                        onClick = onPointClick
                    ) {

                        Column {
                            //upper line
                            Box(
                                modifier = Modifier
                                    .width(lineWidth)
                                    .height(minCardHeight / 2)
                                    .background(upperLineColor)
                            )

                            //lower line
                            Box(
                                modifier = Modifier
                                    .width(lineWidth)
                                    .height(minCardHeight / 2)
                                    .background(lowerLineColor)
                            )
                        }

                        //point
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = spotDragModifier
                                .size(pointCircleSize)
                                .clip(CircleShape)
                                .background(pointColor)
                        ) {
                            if (iconText != null) {
                                val textStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__ICON).copy(
                                    color = Color(iconTextColor ?: whiteInt)
                                )
                                Text(
                                    text = iconText,
                                    style = textStyle
                                )
                            }
                        }
                    }

                    MySpacerRow(width = 10.dp)

                    //main text modifier

                    val dragModifier = if (spotDragModifier != Modifier) spotDragModifier
                                        else if (dateDragModifier != Modifier) dateDragModifier
                                        else Modifier

                    Box(modifier = dragModifier.weight(1f)) {
                        //main text
                        if (!isEditMode) {
                            Text(
                                text = mainText1,
                                style = mainTextStyle1,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            val focusManager = LocalFocusManager.current

                            MyTextField(
                                inputText = mainText,
                                inputTextStyle = mainTextStyle,
                                placeholderText = stringResource(id = R.string.title_card_body_add_a_title),
                                placeholderTextStyle = mainNullTextStyle,
                                onValueChange = {
                                    if (it.length > MAX_TITLE_LENGTH && !isTextSizeLimit) {
                                        isTextSizeLimit = true
                                        isLongText(true)
                                    } else if (it.length <= MAX_TITLE_LENGTH && isTextSizeLimit) {
                                        isTextSizeLimit = false
                                        isLongText(false)
                                    }

                                    onTitleTextChange(it)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                })
                            )
                        }
                    }

                    //delete icon
                    AnimatedVisibility(
                        visible = isEditMode && deleteEnabled,
                        enter = scaleIn(tween(300)),
                        exit = scaleOut(tween(400)) + fadeOut(tween(300))
                    ) {
                        IconButton(onClick = {
                            if (isTextSizeLimit) {
                                isTextSizeLimit = false
                                isLongText(false)
                            }
                            onDeleteClick()
                        }) {
                            DisplayIcon(MyIcons.delete)
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
                        if (expandedText != null)
                            IconButton(onClick = {
                                onExpandedButtonClicked()
                            }) {
                                if (!isExpanded)
                                    DisplayIcon(icon = MyIcons.expand)
                                else
                                    DisplayIcon(icon = MyIcons.collapse)
                            }
                    }
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

/**
 * when user click expand button, this will show
 *
 */
@Composable
private fun ExpandedTextWithLine(
    expandedText: String,

    expandedTextStyle: TextStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__EXPAND),
    lineColor: Color = getColor(ColorType.GRAPH__LINE)
){
    val dummyPointHalfWidth = (pointCircleSize - lineWidth)/2

    Column{
        Row(
            modifier = Modifier
                .height(additionalHeight)
                .fillMaxWidth()
        ) {
            //dummy date/time
            MySpacerRow(width = (40.dp - pointCircleSize) / 2 + 70.dp)

            //dummy (40.dp - line) / 2
            MySpacerRow(width = (40.dp - lineWidth) / 2)

            //line
            Box(
                modifier = Modifier
                    .width(lineWidth)
                    .fillMaxHeight()
                    .background(lineColor)
            )

            //dummy (40.dp - line) / 2
            MySpacerRow(width = (40.dp - lineWidth) / 2)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = expandedText,
                    style = expandedTextStyle
                )
            }
        }
    }
}

/**
 * at GraphListItem's upper and lower
 *
 */
@Composable
fun DummySpaceWithLine(
    height: Dp = dummySpaceHeight,
    lineColor: Color = getColor(ColorType.GRAPH__LINE)
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ){
        //dummy date/time
        MySpacerRow(width = (40.dp - pointCircleSize) / 2 + 70.dp)

        //dummy (40.dp - line) / 2
        MySpacerRow(width = (40.dp - lineWidth) / 2)

        //line
        Box(
            modifier = Modifier
                .width(lineWidth)
                .fillMaxHeight()
                .background(lineColor)
        )

        //dummy (40.dp - line) / 2
        MySpacerRow(width = (40.dp - lineWidth) / 2)
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

    Card(
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