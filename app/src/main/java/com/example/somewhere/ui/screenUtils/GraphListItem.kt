package com.example.somewhere.ui.screenUtils

import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.whiteInt

private val dummySpaceHeight:   Dp = 10.dp
private val minCardHeight:      Dp = 40.dp
private val additionalHeight:   Dp = 40.dp
private val pointCircleSize:    Dp = 22.dp
private val lineWidth:          Dp = 7.dp

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun GraphListItem(
    isEditMode: Boolean,
    isExpanded: Boolean,
    itemId: Int,

    sideText: String,
    mainText: String?,
    expandedText: String?,

    onTitleTextChange: (itemId: Int, titleText: String) -> Unit,

    isFirstItem: Boolean,
    isLastItem: Boolean,
    availableDelete: Boolean,

    onItemClick: (Int) -> Unit,/**return [itemId] (Date's id / Spot's id)*/
    onExpandedButtonClicked: (Int) -> Unit,

    modifier: Modifier = Modifier,
    onDeleteClick: (itemId: Int) -> Unit = { },

    iconText: String? = null,
    @ColorInt iconTextColor: Int? = null,

    isShown: Boolean = true,

    itemColor: Color = MaterialTheme.colors.surface,
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

    val mainText1 = if(mainText == null || mainText == "") "No Title"
                    else mainText

    val mainTextStyle1 = if(mainText == null || mainText == "") mainNullTextStyle
                            else mainTextStyle


    Card(
        backgroundColor = itemColor,
        modifier = modifier,
        elevation = 0.dp,
        onClick = {
            if (!isEditMode)
                onItemClick(itemId)
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


                //side text
                Row(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = sideText,
                        style = sideTextStyle
                    )
                }


                //point, line
                Box(
                    modifier = Modifier
                        .width(pointCircleSize)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
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
                        modifier = Modifier
                            .size(pointCircleSize)
                            .clip(CircleShape)
                            .background(pointColor)
                    ){
                        if (iconText != null){
                            val textStyle = getTextStyle(TextType.GRAPH_LIST_ITEM__ICON).copy(color = Color(iconTextColor ?: whiteInt))
                            Text(
                                text = iconText,
                                style = textStyle
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(modifier = Modifier.weight(1f)) {
                    //main text
                    if (!isEditMode) {
                        Text(
                            text = mainText1,
                            style = mainTextStyle1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    else{
                        val focusManager = LocalFocusManager.current
                        val context = LocalContext.current

                        MyTextField(
                            inputText = mainText,
                            inputTextStyle = mainTextStyle,
                            placeholderText = "Edit Title",
                            placeholderTextStyle = mainNullTextStyle,
                            onValueChange = {
                                var text = it
                                if(it.length > 25) {
                                    Toast.makeText(context, "over 25", Toast.LENGTH_SHORT).show()
                                    text = text.substring(0, 25)
                                }

                                onTitleTextChange(itemId, text)
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
                    visible = isEditMode && availableDelete,
                    enter = scaleIn(tween(300)),
                    exit = scaleOut(tween(400)) + fadeOut(tween(300))
                ) {
                    IconButton(onClick = {
                        onDeleteClick(itemId)
                    }) {
                        DisplayIcon(MyIcons.delete)
                    }
                }

                //expand / collapse icon
                if (expandedText != null)
                    IconButton(onClick = {
                        onExpandedButtonClicked(itemId)
                    }) {
                        if (!isExpanded)
                            DisplayIcon(icon = MyIcons.expand)
                        else
                            DisplayIcon(icon = MyIcons.collapse)
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
            //dummy date
            Spacer(modifier = Modifier.width(80.dp))

            //dummy (point - line) / 2
            Spacer(modifier = Modifier.width(dummyPointHalfWidth))

            //line
            Box(
                modifier = Modifier
                    .width(lineWidth)
                    .fillMaxHeight()
                    .background(lineColor)
            )

            //dummy (point - line) / 2
            Spacer(modifier = Modifier.width(dummyPointHalfWidth))

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = expandedText,
                    style = expandedTextStyle
                )
            }
        }
    }
}


@Composable
fun DummySpaceWithLine(
    height: Dp = dummySpaceHeight,
    lineColor: Color = getColor(ColorType.GRAPH__LINE)
){
    val dummyPointHalfWidth = (pointCircleSize - lineWidth)/2

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ){
        //dummy date
        Spacer(modifier = Modifier.width(80.dp))

        //dummy (point - line) / 2
        Spacer(modifier = Modifier.width(dummyPointHalfWidth))

        //line
        Box(
            modifier = Modifier
                .width(lineWidth)
                .fillMaxHeight()
                .background(lineColor)
        )

        //dummy (point - line) / 2
        Spacer(modifier = Modifier.width(dummyPointHalfWidth))
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
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
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
            DummySpaceWithLine()
    }
}