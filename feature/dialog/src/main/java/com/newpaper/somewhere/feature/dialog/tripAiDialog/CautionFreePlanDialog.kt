package com.newpaper.somewhere.feature.dialog.tripAiDialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtonLoading
import com.newpaper.somewhere.feature.dialog.PositiveDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun CautionFreePlanDialog(
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit,
    cautionFreePlanViewModel: CautionFreePlanViewModel = hiltViewModel()
){
    val cautionFreePlanUiState by cautionFreePlanViewModel.cautionFreePlanUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        cautionFreePlanViewModel.setShowLoading(false)
    }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { cautionFreePlanViewModel.toggleCheckedIUnderstand() }
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //positive button
                if (cautionFreePlanUiState.showLoading){
                    DialogButtonLoading(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
                else {
                    PositiveDialogButton(
                        text = stringResource(id = R.string.watch_ad_and_create_trip),
                        onClick = {
                            cautionFreePlanViewModel.setShowLoading(true)
                            onClickPositive()
                        },
                        enabled = cautionFreePlanUiState.isCheckedIUnderstand,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }

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