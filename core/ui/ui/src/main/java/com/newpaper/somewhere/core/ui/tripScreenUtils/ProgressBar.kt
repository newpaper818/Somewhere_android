package com.newpaper.somewhere.core.ui.tripScreenUtils

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.NewItemButton
import com.newpaper.somewhere.core.designsystem.component.button.ToNextDateButton
import com.newpaper.somewhere.core.designsystem.component.button.ToPrevDateButton
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.ProgressBarColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.whiteInt
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.convert.getDateText
import com.newpaper.somewhere.core.utils.convert.getNextSpot
import com.newpaper.somewhere.core.utils.convert.getPrevSpot
import com.newpaper.somewhere.core.utils.convert.getStartTimeText
import java.time.LocalDate

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
        Shape.DEFAULT_POINT_NUM -> 22.dp
        Shape.HIGHLIGHT_POINT_NUM -> 25.dp
        Shape.DEFAULT_POINT -> 13.dp
        Shape.HIGHLIGHT_POINT -> 18.dp

        Shape.DEFAULT_LINE -> 6.dp
        Shape.HIGHLIGHT_LINE -> 8.dp
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
                lineColor = ProgressBarColor.lineDefaultMove
            )
        }

        item{ MySpacerRow(width = endSpacerValue) }
    }
}

@Composable
fun SpotListProgressBar(
    useLargeItemWidth: Boolean,
    spacerValue: Dp,
    progressBarState: LazyListState,
    isEditMode: Boolean,

    dateTimeFormat: DateTimeFormat,
    dateList: List<Date>,
    dateIndex: Int,
    spotList: List<Spot>,
    currentSpotIndex: Int,

    addNewSpot: () -> Unit,
    onClickSpot: (spotId: Int) -> Unit,
    onPrevDateClick: (dateId: Int) -> Unit,
    onNextDateClick: (dateId: Int) -> Unit,
){
    val spot = spotList.getOrNull(currentSpotIndex)

    val moveIdx =
        if (spot?.spotType?.isMove() == true){
            currentSpotIndex - 1
        }
        else    null

    LazyRow(
        state = progressBarState,
        modifier = Modifier.fillMaxWidth()
    ){

        //to prev date button
        item {
            if (!isEditMode && dateList.first() != dateList[dateIndex]) {
                MySpacerRow(width = spacerValue)

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    ToPrevDateButton(
                        text = dateList[dateIndex - 1].getDateText(dateTimeFormat, false),
                        onClick = { onPrevDateClick(dateIndex - 1) }
                    )
                }
            }
        }

        //if prev date's last spot is move or this date first spot is move : show line
        item{
            if (spotList.isNotEmpty()) {
                val currentSpotSpotTypeIsMove = spotList[0].spotType.isMove()
                val prevSpotSpotTypeIsMove =
                    spotList[0].getPrevSpot(dateList, dateIndex)?.spotType?.isMove() ?: false

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
                            isHighlight = currentSpotIndex == 0 && spotList[0].spotType.isMove(),
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
                        || it.spotType.isMove() && it.index == currentSpotIndex,
                isRightHighlight = moveIdx != null && it.index == moveIdx
                        || it.spotType.isMove() && it.index == currentSpotIndex,
                isFirst = it == spotList.first() && it.getPrevSpot(dateList, dateIndex)?.spotType?.isNotMove() ?: true && it.spotType.isNotMove(),
                isLast = it == spotList.last() && it.getNextSpot(dateList, dateIndex)?.spotType?.isNotMove() ?: true && it.spotType.isNotMove(),
                onClickItem = {
                    onClickSpot(it.index)
                },
                useLeftLineMoveColor = it.getPrevSpot(dateList, dateIndex)?.spotType?.isMove() ?: false || it.spotType.isMove(),
                useRightLineMoveColor = it.getNextSpot(dateList, dateIndex)?.spotType?.isMove() ?: false || it.spotType.isMove()
            )
//            }
        }

        //if next date's first spot is move or this date last spot is move : show line
        item{
            if (spotList.isNotEmpty()) {
                val lastSpotIdx = spotList.lastIndexOf(spotList.last())
                val currentSpotSpotTypeIsMove = spotList[lastSpotIdx].spotType.isMove()
                val nextSpotSpotTypeIsMove = spotList[lastSpotIdx].getNextSpot(dateList, dateIndex)?.spotType?.isMove() ?: false

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
                            isHighlight = currentSpotIndex == lastSpotIdx && spotList[lastSpotIdx].spotType.isMove(),
                            modifier = Modifier.fillMaxWidth(),
                            useMoveColor = currentSpotSpotTypeIsMove || nextSpotSpotTypeIsMove
                        )
                    }
                }
            }
        }

        item{
            if(isEditMode && spotList.size < 100){
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
            if (!isEditMode && dateList.last() != dateList[dateIndex]) {
                if (spot == null)
                    MySpacerRow(width = 32.dp)

                Column(
                    modifier = Modifier.height(PROGRESS_BAR_HEIGHT),
                    verticalArrangement = Arrangement.Center
                ) {
                    ToNextDateButton(
                        text = dateList[dateIndex + 1].getDateText(dateTimeFormat, false),
                        onClick = { onNextDateClick(dateIndex + 1) }
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
    @ColorInt iconTextColor: Int? = whiteInt
){
    val upperTextStyle =    if (isHighlight)    MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    else                MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val titleTextStyle =    if (isHighlight && titleText != null)   MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
    else if (isHighlight)                   MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    else                                    MaterialTheme.typography.bodySmall

    val upperText1 =    upperText ?: ""

    val titleText1 =    if (isHighlight)  titleText ?: stringResource(id = R.string.no_title)
    else            titleText ?: ""

    val boxColor = if (isHighlight) Color(pointColor ?: 0x00000000).copy(alpha = 0.2f)
    else Color.Transparent


    var itemWidth by rememberSaveable {
        mutableStateOf(
            if (isHighlight && showPoint) {
                if (useLargeItemWidth)    220
                else                170
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
            if (useLargeItemWidth)    220
            else                170
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
    else if (isHighlightPoint)  ProgressBarColor.pointHighlight
    else                        ProgressBarColor.pointDefault

    val iconTextStyle = if (isHighlightPoint)   MaterialTheme.typography.labelMedium.copy(color = Color(textColor ?: whiteInt), fontWeight = FontWeight.Bold)
    else                    MaterialTheme.typography.labelSmall.copy(color = Color(textColor ?: whiteInt), fontWeight = FontWeight.Bold)

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
                    .background(ProgressBarColor.lineHighlight)
            )
        else{
            if (useMoveColor){
                Box(
                    modifier = modifier
                        .height(getSize(Shape.DEFAULT_LINE))
                        .background(ProgressBarColor.lineDefaultMove)
                )
            }
            else{
                Box(
                    modifier = modifier
                        .height(getSize(Shape.DEFAULT_LINE))
                        .background(color ?: MaterialTheme.colorScheme.outline)
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
























@Composable
@PreviewLightDark
private fun Preview(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .width(600.dp)
                .padding(0.dp, 16.dp)
        ) {
            DateListProgressBar(
                startSpacerValue = 16.dp,
                endSpacerValue = 16.dp,
                progressBarState = LazyListState(),
                dateList = listOf(
                    Date(date = LocalDate.now(), titleText = ""),
                    Date(date = LocalDate.now().plusDays(1), titleText = "title 2"),
                    Date(date = LocalDate.now().plusDays(1), titleText = "title 3")
                ),
                currentDateIdx = 0,
                dateTimeFormat = DateTimeFormat()
            ) {

            }
        }
    }
}