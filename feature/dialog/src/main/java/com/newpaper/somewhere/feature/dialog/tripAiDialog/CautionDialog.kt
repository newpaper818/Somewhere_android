package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
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
        buttonContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MySpacerColumn(height = 8.dp)

                //positive button
                PositiveDialogButton(
                    text = stringResource(id = R.string.i_understand),
                    onClick = onClickPositive,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )

                MySpacerColumn(height = 12.dp)

                //cancel button
                CancelDialogButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}