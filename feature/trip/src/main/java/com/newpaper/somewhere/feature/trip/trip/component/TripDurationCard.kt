package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.trip.R
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TripDurationCard(
    defaultDateRange: ClosedRange<LocalDate>,

    isDateListEmpty: Boolean,
    startDateText: String?,
    endDateText: String?,
    durationText: String?,

    isEditMode: Boolean,
    onClick: () -> Unit,

    modifier: Modifier = Modifier
){
    //set body text and body text style
    // Start Date || No Start Date || 2023.06.23
    val startDateText1 =
        startDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_start_date)
                        else stringResource(id = R.string.trip_duration_card_body_no_start_date)
    val endDateText1 =
        endDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_end_date)
                        else stringResource(id = R.string.trip_duration_card_body_no_end_date)

    val bodyTextStyle1 = if (!isDateListEmpty) MaterialTheme.typography.bodyLarge
                            else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)


    //ui
    Column {
        MyCard(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            enabled = isEditMode
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Trip Duration / duration text
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = expandVertically(tween(400)),
                        exit = shrinkVertically(tween(400))
                    ) {

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.trip_duration_card_title),
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )

                                MySpacerRow(width = 8.dp)

                                Text(
                                    text = durationText ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            MySpacerColumn(height = 8.dp)
                        }
                    }

                    //start date -> end date
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {

                        //start date
                        Box(
                            modifier = Modifier.height(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = startDateText1,
                                style = bodyTextStyle1,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (defaultDateRange.start != defaultDateRange.endInclusive) {
                            Row {
                                MySpacerRow(width = 16.dp)

                                // -> icon
                                Box(
                                    modifier = Modifier.height(26.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DisplayIcon(MyIcons.rightArrowTo)
                                }

                                MySpacerRow(width = 16.dp)

                                //end date
                                Box(
                                    modifier = Modifier.height(26.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = endDateText1,
                                        style = bodyTextStyle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (!isEditMode) {
                            Box(
                                modifier = Modifier
                                    .height(26.dp)
                                    .padding(8.dp, 0.dp, 0.dp, 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = durationText ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }

                //clickable icon
                if (isEditMode)
                    DisplayIcon(icon = MyIcons.clickableItem)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
























@Composable
@PreviewLightDark
private fun Preview_TripDurationCard(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TripDurationCard(
                defaultDateRange = LocalDate.now() .. LocalDate.now().plusDays(3),
                isDateListEmpty = false,
                startDateText = "2023.03.14",
                endDateText = "03.16",
                durationText = "3 days",
                isEditMode = false,
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TripDurationCard_edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TripDurationCard(
                defaultDateRange = LocalDate.now() .. LocalDate.now().plusDays(3),
                isDateListEmpty = false,
                startDateText = "2023.03.14",
                endDateText = "03.16",
                durationText = "3 days",
                isEditMode = true,
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TripDurationCard_noDate(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TripDurationCard(
                defaultDateRange = LocalDate.now() .. LocalDate.now().plusDays(3),
                isDateListEmpty = true,
                startDateText = null,
                endDateText = null,
                durationText = null,
                isEditMode = false,
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TripDurationCard_noDate_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TripDurationCard(
                defaultDateRange = LocalDate.now() .. LocalDate.now().plusDays(3),
                isDateListEmpty = true,
                startDateText = null,
                endDateText = null,
                durationText = null,
                isEditMode = true,
                onClick = {}
            )
        }
    }
}