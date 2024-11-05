package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.simpleDialog.SimpleDialog

@Composable
fun CautionDialog(
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit
){
    SimpleDialog(
        titleText = stringResource(id = R.string.caution),
        bodyText = stringResource(id = R.string.it_could_contain_incorrect_information),
        positiveButtonText = stringResource(id = R.string.i_understand),
        onDismissRequest = onDismissRequest,
        onClickPositive = onClickPositive
    )
}