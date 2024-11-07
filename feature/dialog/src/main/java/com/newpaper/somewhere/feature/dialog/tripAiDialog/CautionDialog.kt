package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.PositiveDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun CautionDialog(
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.caution),
        bodyText = stringResource(id = R.string.it_could_contain_incorrect_information),
        bodyContent = { },
        buttonContent = {
            DialogButtons(
                buttonLayout = ButtonLayout.VERTICAL,
                negativeButtonContent = {
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    PositiveDialogButton(
                        text = stringResource(id = R.string.i_understand),
                        onClick = onClickPositive,
                        modifier = it
                    )
                }
            )
        }
    )
}