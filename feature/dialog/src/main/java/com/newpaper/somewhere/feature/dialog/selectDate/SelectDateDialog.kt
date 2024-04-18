package com.newpaper.somewhere.feature.dialog.selectDate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.ui.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_MAX_HEIGHT
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog
import java.time.LocalDate

@Composable
fun SelectDateDialog(
    dateTimeFormat: DateTimeFormat,
    initialDate: Date,
    dateList: List<Date>,
    onOkClick: (dateIndex: Int) -> Unit,
    onDismissRequest: () -> Unit
){
    var currentDateIndex by rememberSaveable { mutableIntStateOf(initialDate.index) }

    val maxHeight = 26.dp + 16.dp + 60.dp * dateList.size + 12.dp + 48.dp


    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,
        maxHeight = min(maxHeight, DIALOG_DEFAULT_MAX_HEIGHT),

        titleText = stringResource(id = R.string.move_date),
        bodyContent = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceBright)
            ) {
                items(dateList) { date ->
                    GraphListItem(
                        isEditMode = false,
                        isExpanded = false,
                        sideText = date.getDateText(
                            dateTimeFormat.copy(includeDayOfWeek = false),
                            false
                        ),
                        mainText = date.titleText,
                        expandedText = null,
                        isTextSizeLimit = false,
                        onMainTextChange = { },
                        isFirstItem = date == dateList.first(),
                        isLastItem = date == dateList.last(),
                        deleteEnabled = false,
                        dragEnabled = false,
                        onItemClick = {
                            currentDateIndex = date.index
                        },
                        onExpandedButtonClicked = { },
                        pointColor = Color(date.color.color),
                        isHighlighted = date.index == currentDateIndex
                    )
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    onClick = {
                        onOkClick(currentDateIndex)
                    }
                )
            }
        }
    )
}























@Composable
@PreviewLightDark
private fun Preview_(){
    SomewhereTheme {
        MyScaffold {
            SelectDateDialog(
                dateTimeFormat = DateTimeFormat(),
                initialDate = Date(date = LocalDate.now()),
                dateList = listOf(
                    Date(index = 0, date = LocalDate.now()),
                    Date(index = 1, date = LocalDate.now().plusDays(1)),
                    Date(index = 2, date = LocalDate.now().plusDays(2))
                ),
                onOkClick = {},
                onDismissRequest = {}
            )
        }
    }
}