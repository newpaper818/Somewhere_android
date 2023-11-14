package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils

import androidx.annotation.ColorInt
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.model.Spot
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.theme.whiteInt
import com.newpaper.somewhere.viewModel.DateTimeFormat

val PROGRESS_BAR_HEIGHT = 85.dp

private enum class Shape{
    DEFAULT_POINT_NUM,
    HIGHLIGHT_POINT_NUM,
    DEFAULT_POINT,
    HIGHLIGHT_POINT,

    DEFAULT_LINE,
    HIGHLIGHT_LINE
}

private fun getSize(shape: Shape): Dp {
    return when(shape){
        Shape.DEFAULT_POINT_NUM     -> 22.dp
        Shape.HIGHLIGHT_POINT_NUM   -> 25.dp
        Shape.DEFAULT_POINT         -> 13.dp
        Shape.HIGHLIGHT_POINT       -> 18.dp

        Shape.DEFAULT_LINE          -> 6.dp
        Shape.HIGHLIGHT_LINE        -> 8.dp
    }
}

@Composable
fun DateListProgressBar(
    startSpacerValue: Dp,
    endSpacerValue: Dp,
    progressBarState: LazyListState,
    dateList: List<Date>,
    currentDateIdx: Int,
    dateTimeFormat: DateTimeFormat,

    onClickDate: (dateIndex: Int) -> Unit
){
    LazyRow(
        state = progressBarState,
        modifier = Modifier.fillMaxWidth()
    ){
        item{ MySpacerRow(width = startSpacerValue) }

        items(dateList){

            OneProgressBar(
                useLargeItemWidth = true,
                includeIconNum = false,
                upperText = it.getDateText(dateTimeFormat, includeYear = false),
                titleText = it.titleText,
                isHighlight = it == dateList[currentDateIdx],
                isLeftHighlight = false,
                isRightHighlight = false,
                isFirst = it == dateList.first(),
                isLast = it == dateList.last(),
                onClickItem = {
                    onClickDate(it.index)
                },
                pointColor = it.color.color,
                lineColor = getColor(ColorType.PROGRESS_BAR__LINE_DEFAULT_MOVE)
            )
        }

        item{ MySpacerRow(width = endSpacerValue) }
    }
}

@Composable
fun SpotListProgressBar(
    useLargeItemWidth: Boolean,
    spacerValue: Dp,
    initialIdx: Int,
    progressBarState: LazyListState,
    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,
    dateList: List<Date>,
    dateId: Int,
    spotList: List<Spot>,
    currentSpotIdx: Int,

    addNewSpot: () -> Unit,
    onClickSpot: (spotId: Int) -> Unit,
    onPrevDateClick: (dateId: Int) -> Unit,
    onNextDateClick: (dateId: Int) -> Unit,
){
    val spot = spotList.getOrNull(currentSpotIdx)

    val moveIdx =
        if (spot?.spotType?.isMove() == true){
            currentSpotIdx - 1
        }
        else    null

    LazyRow(
        state = progressBarState,
        modifier = Modifier.fillMaxWidth()
    ){

        //to prev date button
        item {
            if (!isEditMode && dateList.first() != dateList[dateId]) {
                MySpacerRow(width = spacerValue)

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    ToPrevDateButton(
                        text = dateList[dateId - 1].getDateText(dateTimeFormat, false),
                        onClick = { onPrevDateClick(dateId - 1) }
                    )
                }
            }
        }

        //if prev date's last spot is move or this date first spot is move : show line
        item{
            if (spotList.isNotEmpty()) {
                val currentSpotSpotTypeIsMove = spotList[0].spotType.isMove()
                val prevSpotSpotTypeIsMove =
                    spotList[0].getPrevSpot(dateList, dateId)?.spotType?.isMove() ?: false

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(spacerValue)
                            .height(25.dp)
                    ) {
                        Line(
                            isShown = currentSpotSpotTypeIsMove || prevSpotSpotTypeIsMove,
                            isHighlight = currentSpotIdx == 0 && spotList[0].spotType.isMove(),
                            modifier = Modifier.fillMaxWidth(),
                            useMoveColor = currentSpotSpotTypeIsMove || prevSpotSpotTypeIsMove
                        )
                    }
                }
            }
        }

        //each spot
        items(spotList){

//            if (it.spotType.isNotMove()) {
                OneProgressBar(
                    useLargeItemWidth = useLargeItemWidth,
                    includeIconNum = it.spotType.isNotMove(),
                    pointColor = it.spotType.group.color.color,
                    showPoint = it.spotType.isNotMove(),
                    iconText = it.iconText.toString(),
                    iconTextColor = it.spotType.group.color.onColor,
                    upperText = it.getStartTimeText(dateTimeFormat.timeFormat) ?: "",
                    titleText = it.titleText,
                    isHighlight = it == spot ||
                            moveIdx != null && (it.index == moveIdx || it.index == moveIdx + 2),
                    isLeftHighlight = moveIdx != null && it.index == moveIdx + 2
                            || it.spotType.isMove() && it.index == currentSpotIdx,
                    isRightHighlight = moveIdx != null && it.index == moveIdx
                            || it.spotType.isMove() && it.index == currentSpotIdx,
                    isFirst = it == spotList.first() && it.getPrevSpot(dateList, dateId)?.spotType?.isNotMove() ?: true,
                    isLast = it == spotList.last() && it.getNextSpot(dateList, dateId)?.spotType?.isNotMove() ?: true,
                    onClickItem = {
                        onClickSpot(it.index)
                    },
                    useLeftLineMoveColor = it.getPrevSpot(dateList, dateId)?.spotType?.isMove() ?: false || it.spotType.isMove(),
                    useRightLineMoveColor = it.getNextSpot(dateList, dateId)?.spotType?.isMove() ?: false || it.spotType.isMove()
                )
//            }
        }

        //if next date's first spot is move or this date last spot is move : show line
        item{
            if (spotList.isNotEmpty()) {
                val lastSpotIdx = spotList.lastIndexOf(spotList.last())
                val currentSpotSpotTypeIsMove = spotList[lastSpotIdx].spotType.isMove()
                val nextSpotSpotTypeIsMove = spotList[lastSpotIdx].getNextSpot(dateList, dateId)?.spotType?.isMove() ?: false

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(spacerValue)
                            .height(25.dp)
                    ) {
                        Line(
                            isShown = currentSpotSpotTypeIsMove || nextSpotSpotTypeIsMove,
                            isHighlight = currentSpotIdx == lastSpotIdx && spotList[lastSpotIdx].spotType.isMove(),
                            modifier = Modifier.fillMaxWidth(),
                            useMoveColor = currentSpotSpotTypeIsMove || nextSpotSpotTypeIsMove
                        )
                    }
                }
            }
        }


        item{
            if(isEditMode){
                if (spot == null)
                    MySpacerRow(width = 32.dp)

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {

                    NewItemButton(text = stringResource(id = R.string.new_spot), onClick = {
                        addNewSpot()
                    })
                }

                MySpacerRow(width = spacerValue)
            }
        }

        //to next date button
        item {
            if (!isEditMode && dateList.last() != dateList[dateId]) {
                if (spot == null)
                    MySpacerRow(width = 32.dp)

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    ToNextDateButton(
                        text = dateList[dateId + 1].getDateText(dateTimeFormat, false),
                        onClick = { onNextDateClick(dateId + 1) }
                    )
                }

                MySpacerRow(width = spacerValue)
            }
        }
    }
}

@Composable
fun OneProgressBar(
    useLargeItemWidth: Boolean,
    upperText: String?,
    titleText: String?,

    isHighlight: Boolean,
    isLeftHighlight: Boolean,
    isRightHighlight: Boolean,

    isFirst: Boolean,
    isLast: Boolean,
    onClickItem: () -> Unit,

    showPoint: Boolean = true,
    useLeftLineMoveColor: Boolean = false,
    useRightLineMoveColor: Boolean = false,

    includeIconNum: Boolean = false,
    pointColor: Int? = null,
    lineColor: Color? = null,
    iconText: String? = null,
    @ColorInt iconTextColor: Int? = whiteInt,

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

    val boxColor = if (isHighlight) Color(pointColor ?: 0x00000000).copy(alpha = 0.2f)
                    else Color.Transparent


    var itemWidth by rememberSaveable {
        mutableStateOf(
            if (isHighlight && showPoint) {
                if (useLargeItemWidth)    200
                else                160
            }
            else             130
        )
    }

    //width animate
    val width by animateDpAsState(
        targetValue = itemWidth.dp,
        tween(500),
        label = "progress bar item width"
    )

    itemWidth =
        if (isHighlight && showPoint) {
            if (useLargeItemWidth)    200
            else                160
        }
        else             130

    ClickableBox(
        modifier = Modifier
            .width(width)
            .height(PROGRESS_BAR_HEIGHT),
        containerColor = boxColor,
        onClick = onClickItem
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            //upper text
            Box(
                modifier = Modifier.height(30.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = upperText1,
                    style = upperTextStyle
                )
            }

            //point with line
            Box(
                modifier = Modifier.height(25.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Line(
                        isShown = !isFirst,
                        isHighlight = isLeftHighlight,
                        modifier = Modifier.weight(1f),
                        color = lineColor,
                        useMoveColor = useLeftLineMoveColor,
                    )
                    Line(
                        isShown = !isLast,
                        isHighlight = isRightHighlight,
                        modifier = Modifier.weight(1f),
                        color = lineColor,
                        useMoveColor = useRightLineMoveColor,
                    )
                }

                //point line
                if (showPoint)
                    Point(
                        isHighlightPoint = isHighlight,
                        color = pointColor,
                        text = iconText,
                        textColor = iconTextColor,
                        includeNum = includeIconNum
                    )
            }

            //lower text
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
}

@Composable
private fun Point(
    isHighlightPoint: Boolean,
    @ColorInt color: Int? = null,
    text: String? = null,
    @ColorInt textColor: Int? = null,

    includeNum: Boolean = false
){
    val pointSize =
        if(includeNum){
            if (isHighlightPoint)   getSize(Shape.HIGHLIGHT_POINT_NUM)
            else                    getSize(Shape.DEFAULT_POINT_NUM)
        }
        else{
            if (isHighlightPoint)   getSize(Shape.HIGHLIGHT_POINT)
            else                    getSize(Shape.DEFAULT_POINT)
        }


    val pointColor =    if(color != null)           Color(color)
                        else if (isHighlightPoint)  getColor(ColorType.PROGRESS_BAR__POINT_HIGHLIGHT)
                        else                        getColor(ColorType.PROGRESS_BAR__POINT_DEFAULT)

    val iconTextStyle = if (isHighlightPoint)   getTextStyle(TextType.PROGRESS_BAR__ICON_TEXT_HIGHLIGHT).copy(color = Color(textColor ?: whiteInt))
                        else                    getTextStyle(TextType.PROGRESS_BAR__ICON_TEXT).copy(color = Color(textColor ?: whiteInt))

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(pointSize)
            .clip(CircleShape)
            .background(pointColor)
    ){
        if (includeNum && text != null){
            Text(
                text = text,
                style = iconTextStyle
            )
        }
    }
}


@Composable
private fun Line(
    isShown: Boolean,
    isHighlight: Boolean,

    modifier: Modifier = Modifier,
    color: Color? = null,
    useMoveColor: Boolean = false
){
    if (isShown) {
        if (isHighlight)
            Box(
                modifier = modifier
                    .height(getSize(Shape.HIGHLIGHT_LINE))
                    .background(getColor(ColorType.PROGRESS_BAR__LINE_HIGHLIGHT))
            )
        else{
            if (useMoveColor){
                Box(
                    modifier = modifier
                        .height(getSize(Shape.DEFAULT_LINE))
                        .background(getColor(ColorType.PROGRESS_BAR__LINE_DEFAULT_MOVE))
                )
            }
            else{
                Box(
                    modifier = modifier
                        .height(getSize(Shape.DEFAULT_LINE))
                        .background(color ?: getColor(ColorType.PROGRESS_BAR__LINE_DEFAULT))
                )
            }
        }

    }
    else{
        Box(
            modifier = modifier
                .height(getSize(Shape.DEFAULT_LINE))
                .background(Color.Transparent)
        )
    }
}
