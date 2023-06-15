package com.example.somewhere.ui.screenUtils

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
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun GraphListItem(
    isEditMode: Boolean,
    isExpanded: Boolean,
    itemId: Int,

    sideText: String,
    mainText: String?,
    expandedText: String,

    isFirstItem: Boolean,
    isLastItem: Boolean,

    onItemClick: (Int) -> Unit, /**return [itemId] (Date's id / Spot's id)*/
    onExpandedButtonClicked: (Int) -> Unit,

    modifier: Modifier = Modifier,
    isShown: Boolean = true,

    dummySpaceHeight: Dp = 10.dp,
    minCardHeight: Dp = 40.dp,
    additionalHeight: Dp = 40.dp,
    pointCircleSize: Dp = 22.dp,
    lineWidth: Dp = 7.dp,

    pointColor: Color = MaterialTheme.colors.primaryVariant,
    lineColor: Color = MaterialTheme.colors.secondaryVariant,

    mainTextStyle: TextStyle = MaterialTheme.typography.h4,
    sideTextStyle: TextStyle = MaterialTheme.typography.h5,
    expandedTextStyle: TextStyle = MaterialTheme.typography.h6
) {

    if (!isShown)
        return

    var upperLineColor = lineColor
    var lowerLineColor = lineColor

    if (isFirstItem)
        upperLineColor = Color.Transparent

    if (isLastItem)
        lowerLineColor = Color.Transparent


    Card(
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier,
        elevation = 0.dp,
        onClick = { onItemClick(itemId) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {

            //upper dummy space
            DummySpaceWithLine(
                dummySpaceHeight = dummySpaceHeight,
                pointCircleSize = pointCircleSize,
                lineWidth = lineWidth,
                lineColor = upperLineColor,
            )

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
                    verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier
                            .size(pointCircleSize)
                            .clip(CircleShape)
                            .background(pointColor)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                //main text
                Text(
                    text = mainText ?: "No Title",
                    style = mainTextStyle
                )

                Spacer(modifier = Modifier.weight(1f))

                //delete icon
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = scaleIn(tween(300)),
                    exit = scaleOut(tween(400)) + fadeOut(tween(300))
                ) {
                    IconButton(onClick = { /*TODO delete trip? dialog and delete*/ }) {
                        DisplayIcon(MyIcons.delete)
                    }
                }

                //expand / collapse icon
                IconButton(onClick = {
                    onExpandedButtonClicked(itemId)
                }) {
                    if (!isExpanded)
                        DisplayIcon(icon = MyIcons.expand)
                    else
                        DisplayIcon(icon = MyIcons.collapse)
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter =
                expandVertically(
                    animationSpec = tween(durationMillis = 300),
                    expandFrom = Alignment.Bottom
                )
                ,
                exit =
                shrinkVertically(
                    animationSpec = tween(durationMillis = 300),
                    shrinkTowards = Alignment.Bottom
                )
            ) {
                //expanded space
                ExpandedTextWithLine(
                    additionalHeight = additionalHeight,
                    expandedText = expandedText,
                    expandedTextStyle = expandedTextStyle,
                    pointCircleSize = pointCircleSize,
                    lineWidth = lineWidth,
                    lineColor = lowerLineColor
                )
            }

            //lower dummy space
            DummySpaceWithLine(
                dummySpaceHeight = dummySpaceHeight,
                pointCircleSize = pointCircleSize,
                lineWidth = lineWidth,
                lineColor = lowerLineColor,
            )
        }
    }
}

@Composable
private fun ExpandedTextWithLine(
    additionalHeight: Dp,

    expandedText: String,
    expandedTextStyle: TextStyle,

    pointCircleSize: Dp,
    lineWidth: Dp,
    lineColor: Color
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
private fun DummySpaceWithLine(
    dummySpaceHeight: Dp,
    pointCircleSize: Dp,
    lineWidth: Dp,

    lineColor: Color
){
    val dummyPointHalfWidth = (pointCircleSize - lineWidth)/2

    Row (
        modifier = Modifier
            .height(dummySpaceHeight)
            .fillMaxWidth()
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
    dummySpaceHeight: Dp = 16.dp
){
    val upperRoundedCornerValue =
        if (isFirst) 16.dp
        else    0.dp

    val lowerRoundedCornerValue =
        if (isLast) 16.dp
        else 0.dp


    Card(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(dummySpaceHeight)
            .clip(
                RoundedCornerShape(
                    topStart = upperRoundedCornerValue,
                    topEnd = upperRoundedCornerValue,
                    bottomStart = lowerRoundedCornerValue,
                    bottomEnd = lowerRoundedCornerValue
                )
            )
    ){

    }
}