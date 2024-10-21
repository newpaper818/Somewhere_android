package com.newpaper.somewhere.feature.dialog.simpleDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
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
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //positive button
                DialogButton(
                    text = positiveButtonText,
                    onClick = onClickPositive
                )
            }
        }
    )
}