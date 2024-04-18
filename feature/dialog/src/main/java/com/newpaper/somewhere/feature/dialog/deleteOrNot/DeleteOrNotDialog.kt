package com.newpaper.somewhere.feature.dialog.deleteOrNot

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

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
            Row{
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //delete button
                DialogButton(
                    text = deleteText,
                    textColor = MaterialTheme.colorScheme.error,
                    errorRipple = true,
                    onClick = onDeleteClick
                )
            }
        }
    )
}

@Composable
fun DeleteFriendDialog(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
){
    DeleteOrNotDialog(
        bodyText = stringResource(id = R.string.delete_friend),
        deleteText = stringResource(id = R.string.button_delete),
        onDismissRequest = onDismissRequest,
        onDeleteClick = onDeleteClick
    )
}

@Composable
fun GetOutSharedTripDialog(
    onDismissRequest: () -> Unit,
    onGetOutClick: () -> Unit
){
    DeleteOrNotDialog(
        bodyText = stringResource(id = R.string.get_out_shared_trip),
        deleteText = stringResource(id = R.string.button_get_out),
        onDismissRequest = onDismissRequest,
        onDeleteClick = onGetOutClick
    )
}


























@Composable
@PreviewLightDark
private fun DeleteOrNotDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            DeleteOrNotDialog(
                bodyText = "Delete item??????????????? ????????????\n?????????????????????????",
                deleteText = "Delete",
                onDismissRequest = {},
                onDeleteClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun DeleteFriendDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            DeleteFriendDialog(
                onDismissRequest = {},
                onDeleteClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun GetOutSharedTripDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            GetOutSharedTripDialog(
                onDismissRequest = {},
                onGetOutClick = {}
            )
        }
    }
}