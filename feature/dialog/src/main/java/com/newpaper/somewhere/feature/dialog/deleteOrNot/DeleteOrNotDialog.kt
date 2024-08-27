package com.newpaper.somewhere.feature.dialog.deleteOrNot

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun DeleteOrNotDialog(
    deleteButtonText: String,
    onDismissRequest: () -> Unit,
    onClickDelete: () -> Unit,

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

                //delete button
                DialogButton(
                    text = deleteButtonText,
                    textColor = MaterialTheme.colorScheme.error,
                    errorRipple = true,
                    onClick = onClickDelete
                )
            }
        }
    )
}

@Composable
fun DeleteFriendDialog(
    onDismissRequest: () -> Unit,
    onClickDelete: () -> Unit
){
    DeleteOrNotDialog(
        bodyText = stringResource(id = R.string.dialog_delete_friend),
        deleteButtonText = stringResource(id = R.string.button_delete),
        onDismissRequest = onDismissRequest,
        onClickDelete = onClickDelete
    )
}

@Composable
fun GetOutSharedTripDialog(
    onDismissRequest: () -> Unit,
    onClickGetOut: () -> Unit
){
    DeleteOrNotDialog(
        bodyText = stringResource(id = R.string.leave_shared_trip),
        deleteButtonText = stringResource(id = R.string.button_get_out),
        onDismissRequest = onDismissRequest,
        onClickDelete = onClickGetOut
    )
}


























@Composable
@PreviewLightDark
private fun DeleteOrNotDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            DeleteOrNotDialog(
                bodyText = "Delete item??????????????? ????????????\n?????????????????????????",
                deleteButtonText = "Delete",
                onDismissRequest = {},
                onClickDelete = {}
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
                onClickDelete = {}
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
                onClickGetOut = {}
            )
        }
    }
}