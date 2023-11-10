package com.newpaper.somewhere.ui.tripScreenUtils.cards

import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.tripScreenUtils.dialogs.DateRangeDialog
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.theme.n60
import com.newpaper.somewhere.utils.millisToLocalDate
import com.newpaper.somewhere.viewModel.DateTimeFormat
import java.time.LocalDate

@Composable
fun TripDurationCard(
    defaultDateRange: ClosedRange<LocalDate>,

    isDateListEmpty: Boolean,
    startDateText: String?,
    endDateText: String?,
    durationText: String?,

    dateTimeFormat: DateTimeFormat,

    isEditMode: Boolean,
    setShowBottomSaveCancelBar: (Boolean) -> Unit,
    setTripDuration: (startDate: LocalDate, endDate: LocalDate) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    var showDateRangePickerDialog by rememberSaveable { mutableStateOf(false) }

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


    if (showDateRangePickerDialog) {
        DateRangeDialog(
            defaultDateRange = defaultDateRange,
            dateTimeFormat = dateTimeFormat,
            onDismissRequest = {
                showDateRangePickerDialog = false
                setShowBottomSaveCancelBar(true)
            },
            onConfirm = {startDateMillis, endDateMillis ->
                if (startDateMillis != null && endDateMillis != null){
                    setTripDuration(millisToLocalDate(startDateMillis), millisToLocalDate(endDateMillis))
                }
                showDateRangePickerDialog = false
                setShowBottomSaveCancelBar(true)
            }
        )
    }

    //ui
    Column {

        ClickableBox(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier.fillMaxWidth(),
            onClick = {
                showDateRangePickerDialog = true
                setShowBottomSaveCancelBar(false)
            },
            enabled = isEditMode
        ) {
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

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = durationText ?: "",
                                style = titleTextStyle
                            )
                        }

                        MySpacerColumn(height = 8.dp)
                    }
                }

                //start date > end date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    //start date
                    Text(
                        text = startDateText1,
                        style = bodyTextStyle1,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    if (defaultDateRange.start != defaultDateRange.endInclusive) {
                        // > icon
                        DisplayIcon(MyIcons.rightArrowTo)

                        Spacer(modifier = Modifier.weight(1f))

                        //end date
                        Text(
                            text = endDateText1,
                            style = bodyTextStyle1,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}