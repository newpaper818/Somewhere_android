package com.newpaper.somewhere.feature.dialog.simpleDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.PositiveDialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SimpleDialog(
    positiveButtonText: String,
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit,

    titleText: String? = null,
    bodyText: String? = null,
    subBodyText: String? = null,

    width: Dp? = DIALOG_DEFAULT_WIDTH,
    bodyContent: @Composable() (() -> Unit)? = null,
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        width = width,
        titleText = titleText,
        bodyText = bodyText,
        subBodyText = subBodyText,
        bodyContent = bodyContent,
        buttonContent = {
            Row{
                //cancel button
                CancelDialogButton(
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 12.dp)

                //positive button
                PositiveDialogButton(
                    text = positiveButtonText,
                    onClick = onClickPositive
                )
            }
        }
    )
}