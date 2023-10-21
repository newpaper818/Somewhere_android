package com.newpaper.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.newpaper.somewhere.R
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.tripScreenUtils.GraphListItem
import com.newpaper.somewhere.viewModel.DateTimeFormat

@Composable
fun SelectDateDialog(
    dateTimeFormat: DateTimeFormat,
    initialDate: Date,
    dateList: List<Date>,
    onOkClick: (dateId: Int) -> Unit,
    onDismissRequest: () -> Unit
){
    var currentDateId by rememberSaveable { mutableStateOf(initialDate.id) }

    val maxHeight = 26.dp + 16.dp + 60.dp * dateList.size + 12.dp + 48.dp


    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,
        maxHeight = min(maxHeight, DIALOG_DEFAULT_MAX_HEIGHT),

        titleText = stringResource(id = R.string.select_date_dialog_title),
        bodyContent = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
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
                        onTitleTextChange = { },
                        isFirstItem = date == dateList.first(),
                        isLastItem = date == dateList.last(),
                        deleteEnabled = false,
                        dragEnabled = false,
                        onItemClick = {
                            currentDateId = date.id
                        },
                        onExpandedButtonClicked = {},
                        itemColor = if (date.id == currentDateId) getColor(ColorType.CARD_SELECTED)
                        else Color.Transparent,
                        isLongText = { }
                    )
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
                    onClick = {
                        onOkClick(currentDateId)
                    }
                )
            }
        }
    )
}