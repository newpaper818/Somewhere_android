package com.example.somewhere.ui.screenUtils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MiniProgressBar(
    pointCount: Int,
    currentIndex: Int,
    isMove: Boolean,

    defaultPointSize: Dp = 8.dp,
    currentPointSize: Dp = 14.dp,
    defaultLineWidth: Dp = 4.dp,
    currentLineWidth: Dp = 4.dp,

    defaultPointColor: Color = MaterialTheme.colors.secondaryVariant,
    currentPointColor: Color = MaterialTheme.colors.primaryVariant,
    defaultLineColor: Color = MaterialTheme.colors.secondaryVariant,
    currentLineColor: Color = MaterialTheme.colors.primaryVariant
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
    ) {
        for (i in 0 until pointCount) {
            PointWithLine(
                isFirstPoint = i == 0,
                isLastPoint = i == pointCount - 1,
                isCurrent = i == currentIndex || isMove && i - 1 == currentIndex,
                isMoveLeft = isMove && i == currentIndex + 1,
                isMoveRight = isMove && i == currentIndex,
                defaultPointSize = defaultPointSize,
                currentPointSize = currentPointSize,
                defaultLineWidth = defaultLineWidth,
                currentLineWidth = currentLineWidth,
                defaultPointColor = defaultPointColor,
                currentPointColor = currentPointColor,
                defaultLineColor = defaultLineColor,
                currentLineColor = currentLineColor
            )

            if (i != pointCount - 1) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Line(
                        isFirstOrLast = false,
                        isMove = isMove && i == currentIndex,
                        defaultLineWidth = defaultLineWidth,
                        currentLineWidth = currentLineWidth,
                        defaultLineColor = defaultLineColor,
                        currentLineColor = currentLineColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PointWithLine(
    isFirstPoint: Boolean,
    isLastPoint: Boolean,

    isCurrent: Boolean,
    isMoveLeft: Boolean,
    isMoveRight: Boolean,

    defaultPointSize: Dp,
    currentPointSize: Dp,

    defaultLineWidth: Dp,
    currentLineWidth: Dp,

    defaultPointColor: Color,
    currentPointColor: Color,

    defaultLineColor: Color,
    currentLineColor: Color
){
    val pointSize = if (isCurrent) currentPointSize
    else defaultPointSize

    val pointColor = if (isCurrent) currentPointColor
    else defaultPointColor

    Box(
        modifier = Modifier.size(currentPointSize),
        contentAlignment = Alignment.Center
    ) {

        //line
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            //left line
            Box(
                modifier = Modifier
                    .weight(1f)
            ){
                Line(
                    isFirstOrLast = isFirstPoint,
                    isMove = isMoveLeft,
                    defaultLineWidth = defaultLineWidth,
                    currentLineWidth = currentLineWidth,
                    defaultLineColor = defaultLineColor,
                    currentLineColor = currentLineColor
                )
            }

            //right line
            Box(
                modifier = Modifier
                    .weight(1f)
            ){
                Line(
                    isFirstOrLast = isLastPoint,
                    isMove = isMoveRight,
                    defaultLineWidth = defaultLineWidth,
                    currentLineWidth = currentLineWidth,
                    defaultLineColor = defaultLineColor,
                    currentLineColor = currentLineColor
                )
            }
        }

        //point
        Box(
            modifier = Modifier
                .size(pointSize)
                .clip(CircleShape)
                .background(pointColor)
        )
    }
}

@Composable
private fun Line(
    isFirstOrLast: Boolean,
    isMove: Boolean,
    defaultLineWidth: Dp,
    currentLineWidth: Dp,
    defaultLineColor: Color,
    currentLineColor: Color
){
    if (!isFirstOrLast) {
        if (isMove) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentLineWidth)
                    .background(currentLineColor)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultLineWidth)
                    .background(defaultLineColor)
            )
        }
    }
}