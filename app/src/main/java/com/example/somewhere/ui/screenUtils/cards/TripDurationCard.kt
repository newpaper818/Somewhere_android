package com.example.somewhere.ui.screenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyCalendarDialog
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.gray
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripDurationCard(
    dateList: List<Date>,

    isDateListEmpty: Boolean,
    startDateText: String?,
    endDateText: String?,
    durationText: String?,

    isEditMode: Boolean,
    setTripDuration: (startDate: LocalDate, endDate: LocalDate) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    //set body text and body text style
    // Start Date || No Start Date || 2023.06.23
    val startDateText1 =
        startDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_start_date)
        else stringResource(id = R.string.trip_duration_card_body_no_start_date)
    val endDateText1 =
        endDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_end_date)
        else stringResource(id = R.string.trip_duration_card_body_no_end_date)

    val bodyTextStyle1 = if (!isDateListEmpty) bodyTextStyle else bodyNullTextStyle

    //date duration picker dialog
    val calendarState = rememberUseCaseState(visible = false, embedded = true)

    val defaultDateRange =
        if (!isDateListEmpty)
            dateList.first().date..dateList.last().date
        else
            LocalDate.now().let { now -> now.plusDays(1)..now.plusDays(5) }

    MyCalendarDialog(
        dialogState = calendarState,
        defaultDateRange = defaultDateRange,
        setTripDuration = setTripDuration
    )

    //ui
    Column {

        Card(
            elevation = 0.dp,
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth(),
            onClick = {
                calendarState.show()
            },
            enabled = isEditMode
        ){
            //Trip Duration     duration text
            Column(
                modifier = Modifier.padding(16.dp, 14.dp)
            ) {

                AnimatedVisibility(
                    visible = !(!isEditMode && durationText == null),
                    enter = expandVertically(tween(400)),
                    exit = shrinkVertically(tween(400))
                ) {

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedVisibility(
                                visible = isEditMode,
                                enter = expandHorizontally(tween(400)),
                                exit = shrinkHorizontally(tween(400))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.trip_duration_card_title),
                                    style = titleTextStyle
                                )
                            }

                            Text(
                                text = durationText ?: "",
                                style = titleTextStyle,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = if (isEditMode) TextAlign.End
                                else TextAlign.Center,
                            )
                        }

                        MySpacerColumn(height = 8.dp)
                    }
                }

                //start date > end date
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //start date
                    Text(
                        text = startDateText1,
                        style = bodyTextStyle1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )

                    // >
                    DisplayIcon(MyIcons.rightArrow, color = gray)

                    //end date
                    Text(
                        text = endDateText1,
                        style = bodyTextStyle1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}