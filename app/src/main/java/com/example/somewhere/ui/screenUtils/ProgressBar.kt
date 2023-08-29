package com.example.somewhere.ui.screenUtils

import androidx.annotation.ColorInt
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import kotlinx.coroutines.launch

private enum class Shape{
    DEFAULT_POINT,
    HIGHLIGHT_POINT,
    DEFAULT_LINE,
    HIGHLIGHT_LINE
}

private fun getSize(shape: Shape): Dp {
    return when(shape){
        Shape.DEFAULT_POINT ->      10.dp
        Shape.HIGHLIGHT_POINT ->    16.dp
        Shape.DEFAULT_LINE ->       6.dp
        Shape.HIGHLIGHT_LINE ->     8.dp
    }
}

@Composable
fun DateListProgressBar(
    dateList: List<Date>,
    currentDateIdx: Int,

    onClickDate: (dateId: Int) -> Unit
){
    val lazyRowState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()


    coroutineScope.launch {
        lazyRowState.animateScrollToItem(currentDateIdx)
    }

    LazyRow(
        state = lazyRowState,
        modifier = Modifier.fillMaxWidth()
    ){
        item{ MySpacerRow(width = 10.dp) }

        items(dateList){

            OneProgressBar(
                upperText = it.getDateText(includeYear = false),
                titleText = it.titleText,
                isHighlight = it == dateList[currentDateIdx],
                isLeftHighlight = false,
                isRightHighlight = false,
                isFirst = it == dateList.first(),
                isLast = it == dateList.last(),
                onClickItem = {
                    onClickDate(it.id)
                },
                pointColor = it.iconColor
            )
        }

        item{ MySpacerRow(width = 10.dp) }

    }
}

@Composable
fun SpotListProgressBar(
    progressBarState: LazyListState,
    isEditMode: Boolean,

    dateList: List<Date>,
    dateId: Int,
    spotList: List<Spot>,
    currentSpotIdx: Int,

    addNewSpot: () -> Unit,

    onClickSpot: (spotId: Int) -> Unit
){
    val spot = spotList.getOrNull(currentSpotIdx) ?: return

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        val toIdx = if (
                        currentSpotIdx >= 1 &&
                        spotList[currentSpotIdx - 1].spotType.isMove()
                        && spotList[currentSpotIdx].spotType.isNotMove()
                    )
                        currentSpotIdx -1
                    else    currentSpotIdx

        progressBarState.animateScrollToItem(toIdx)
    }

    val moveIdx =
        if (spot.spotType.isMove()){
            currentSpotIdx - 1
        }
        else    null

    LazyRow(
        state = progressBarState,
        modifier = Modifier.fillMaxWidth()
    ){
        item{
            Column() {
                MySpacerColumn(height = 28.dp)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(10.dp)
                        .height(25.dp)
                ) {
                    Line(
                        isShown = spotList[0].spotType.isMove() ||
                                spotList[0].getPrevSpot(dateList, spotList, dateId)?.spotType?.isMove() ?: false,
                        isHighlight = currentSpotIdx == 0 && spotList[0].spotType.isMove(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }

        items(spotList){

            if (it.spotType.isNotMove()) {

                OneProgressBar(
                    upperText = it.getStartTimeText() ?: "",
                    titleText = it.titleText,
                    isHighlight = it == spot ||
                            moveIdx != null && (it.id == moveIdx || it.id == moveIdx + 2),
                    isLeftHighlight = moveIdx != null && it.id == moveIdx + 2,
                    isRightHighlight = moveIdx != null && it.id == moveIdx,
                    isFirst = it == spotList.first() && it.getPrevSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,
                    isLast = it == spotList.last() && it.getNextSpot(dateList, spotList, dateId)?.spotType?.isNotMove() ?: true,
                    onClickItem = {
                        onClickSpot(it.id)
                    }
                )
            }
        }

        item{
            //MySpacerRow(width = 10.dp)
            val lastSpotIdx = spotList.lastIndexOf(spotList.last())

            Column() {
                MySpacerColumn(height = 28.dp)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(10.dp)
                        .height(25.dp)
                ) {
                    Line(
                        isShown = spotList[lastSpotIdx].spotType.isMove() ||
                                spotList[lastSpotIdx].getNextSpot(dateList, spotList, dateId)?.spotType?.isMove() ?: false,
                        isHighlight = currentSpotIdx == lastSpotIdx && spotList[lastSpotIdx].spotType.isMove(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if(isEditMode){
            item{
                Column() {
                    MySpacerColumn(height = 22.dp)

                    NewItemButton(text = "New Spot", onClick = {
                        addNewSpot()
//                        coroutineScope.launch {
//                            lazyRowState.animateScrollToItem(spotList.indexOf(spotList.last()) + 1)
//                        }
                    })
                }

                MySpacerRow(width = 160.dp)
            }
        }

    }
}

@Composable
fun OneProgressBar(
    upperText: String?,
    titleText: String?,

    isHighlight: Boolean,
    isLeftHighlight: Boolean,
    isRightHighlight: Boolean,

    isFirst: Boolean,
    isLast: Boolean,
    onClickItem: () -> Unit,

    pointColor: Int? = null,
    upperHighlightTextStyle: TextStyle = getTextStyle(TextType.PROGRESS_BAR__UPPER_HIGHLIGHT),
    upperDefaultTextStyle: TextStyle = getTextStyle(TextType.PROGRESS_BAR__UPPER),
    lowerHighlightTextStyle: TextStyle = getTextStyle(TextType.PROGRESS_BAR__LOWER_HIGHLIGHT),
    lowerHighlightNullTextStyle: TextStyle = getTextStyle(TextType.PROGRESS_BAR__LOWER_HIGHLIGHT_NULL),
    lowerDefaultTextStyle: TextStyle = getTextStyle(TextType.PROGRESS_BAR__LOWER),
){
    val upperTextStyle =    if (isHighlight)    upperHighlightTextStyle
                            else                upperDefaultTextStyle

    val titleTextStyle =    if (isHighlight && titleText != null)   lowerHighlightTextStyle
                            else if (isHighlight)                   lowerHighlightNullTextStyle
                            else                                    lowerDefaultTextStyle

    val upperText1 =    upperText ?: ""

    val titleText1 =    if (isHighlight)  titleText ?: stringResource(id = R.string.no_title)
                        else            titleText ?: ""


    var itemWidth by rememberSaveable {
        mutableStateOf(
            if (isHighlight) 150
            else             100
        )
    }

    //width animate
    val width by animateDpAsState(
        targetValue = itemWidth.dp,
        tween(800)
    )

    itemWidth =  if (isHighlight) 150
                else            110

    Column(
        modifier = Modifier
            .width(width)
            .clickable {
                onClickItem()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.height(28.dp),
            contentAlignment = Alignment.BottomCenter
        ){
            Text(
                text = upperText1,
                style = upperTextStyle
            )
        }

        Box(
            modifier = Modifier.height(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Line(
                    isShown = !isFirst,
                    isHighlight = isLeftHighlight,
                    modifier = Modifier.weight(1f)
                )
                Line(
                    isShown = !isLast,
                    isHighlight = isRightHighlight,
                    modifier = Modifier.weight(1f)
                )
            }

            //point line
            Point(isHighlightPoint = isHighlight, color = pointColor)
        }

        Text(
            text = titleText1,
            style = titleTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .height(30.dp)
        )
    }
}

@Composable
private fun Point(
    isHighlightPoint: Boolean,
    @ColorInt color: Int? = null
){
    val pointSize = if (isHighlightPoint)   getSize(Shape.HIGHLIGHT_POINT)
                    else                    getSize(Shape.DEFAULT_POINT)

    val pointColor =    if(color != null)           Color(color)
                        else if (isHighlightPoint)  getColor(ColorType.PROGRESS_BAR__POINT_HIGHLIGHT)
                        else                        getColor(ColorType.PROGRESS_BAR__POINT_DEFAULT)

    Box(
        modifier = Modifier
            .size(pointSize)
            .clip(CircleShape)
            .background(pointColor)
    )
}


@Composable
private fun Line(
    isShown: Boolean,
    isHighlight: Boolean,

    modifier: Modifier = Modifier
){
    if (isShown) {
        if (isHighlight)
            Box(
                modifier = modifier
                    .height(getSize(Shape.HIGHLIGHT_LINE))
                    .background(getColor(ColorType.PROGRESS_BAR__LINE_HIGHLIGHT))
            )
        else
            Box(
                modifier = modifier
                    .height(getSize(Shape.DEFAULT_LINE))
                    .background(getColor(ColorType.PROGRESS_BAR__LINE_DEFAULT))
            )

    }
    else{
        Box(
            modifier = modifier
                .height(getSize(Shape.DEFAULT_LINE))
                .background(Color.Transparent)
        )
    }
}
