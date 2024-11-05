package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun CautionFreePlanDialog(
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit,
    cautionFreePlanViewModel: CautionFreePlanViewModel = hiltViewModel()
){
    val cautionFreePlanUiState by cautionFreePlanViewModel.cautionFreePlanUiState.collectAsStateWithLifecycle()

    MyDialog(
        onDismissRequest = onDismissRequest,
        width = DIALOG_DEFAULT_WIDTH,
        titleText = stringResource(id = R.string.caution),
        bodyContent = {
            Text(
                text = stringResource(id = R.string.it_could_contain_incorrect_information),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(14.dp, 0.dp, 8.dp, 0.dp)
                    .fillMaxWidth()
            )

            MySpacerColumn(height = 4.dp)

            //checkbox with i understand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = cautionFreePlanUiState.isCheckedIUnderstand,
                    onCheckedChange = { cautionFreePlanViewModel.toggleCheckedIUnderstand() }
                )

                Text(
                    text = stringResource(id = R.string.i_understand),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        buttonContent = {
            Row{
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //positive button
                DialogButton(
                    text = stringResource(id = R.string.watch_ad_and_create_trip),
                    onClick = onClickPositive,
                    enabled = cautionFreePlanUiState.isCheckedIUnderstand
                )
            }
        }
    )
}