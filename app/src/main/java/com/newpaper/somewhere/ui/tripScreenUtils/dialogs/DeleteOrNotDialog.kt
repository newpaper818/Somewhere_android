package com.newpaper.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.getColor

@Composable
fun DeleteOrNotDialog(
    bodyText: String,
    deleteText: String,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        bodyText = bodyText,
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //delete button
                DialogButton(
                    text = deleteText,
                    textColor = getColor(ColorType.BUTTON__DELETE),
                    errorRipple = true,
                    onClick = onDeleteClick
                )
            }
        }
    )
}