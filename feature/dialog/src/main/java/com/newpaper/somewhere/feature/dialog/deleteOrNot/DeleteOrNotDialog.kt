package com.newpaper.somewhere.feature.dialog.deleteOrNot

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.NegativeDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DIALOG_DEFAULT_WIDTH
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
                CancelDialogButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )

                MySpacerRow(width = 12.dp)

                //delete button
                NegativeDialogButton(
                    text = deleteButtonText,
                    onClick = onClickDelete,
                )
            }
        }
    )
}

@Composable
fun DeleteOrLeaveTripDialog(
    deleteTrip: Boolean, // else leave shared trip
    sharingToIsEmpty: Boolean,

    internetEnabled: Boolean,
    onDismissRequest: () -> Unit,
    onClickDelete: () -> Unit,

    bodyContent: @Composable() () -> Unit = {},
){
    val titleText = if (!deleteTrip) stringResource(id = R.string.dialog_title_leave_shared_trip)
        else stringResource(id = R.string.dialog_title_delete_trip)

    var subBodyText = if (!deleteTrip || sharingToIsEmpty) null
        else stringResource(id = R.string.dialog_sub_body_delete_trip)

    if (!internetEnabled && subBodyText == null) {
        subBodyText = if (deleteTrip) stringResource(id = R.string.connect_internet_to_delete_trip)
            else stringResource(id = R.string.connect_internet_to_leave_trip)
    }
    else if (!internetEnabled) {
        subBodyText += if (deleteTrip) "\n" + stringResource(id = R.string.connect_internet_to_delete_trip)
            else "\n" + stringResource(id = R.string.connect_internet_to_leave_trip)
    }

    val deleteText = if (!deleteTrip) stringResource(id = R.string.dialog_button_leave)
        else stringResource(id = R.string.dialog_button_delete)

    MyDialog(
        onDismissRequest = onDismissRequest,
        width = null,
        titleText = titleText,
        bodyText = null,
        subBodyText = subBodyText,
        bodyContent = bodyContent,
        buttonContent = {
            Row{
                //cancel button
                CancelDialogButton(
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //delete button
                NegativeDialogButton(
                    text = deleteText,
                    onClick = onClickDelete,
                    enabled = internetEnabled
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