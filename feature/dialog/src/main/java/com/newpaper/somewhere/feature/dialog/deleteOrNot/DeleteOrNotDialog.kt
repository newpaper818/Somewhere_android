package com.newpaper.somewhere.feature.dialog.deleteOrNot

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.NegativeDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun DeleteOrNotDialog(
    deleteButtonText: String,
    onDismissRequest: () -> Unit,
    onClickDelete: () -> Unit,

    titleText: String? = null,
    bodyText: String? = null,
    subBodyText: String? = null,
    buttonLayout: ButtonLayout = ButtonLayout.AUTO

//    bodyContent: @Composable() (() -> Unit)? = null,
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = titleText,
        bodyText = bodyText,
        subBodyText = subBodyText,
        bodyContent = null,
        buttonContent = {
            DialogButtons(
                negativeButtonContent = {
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    NegativeDialogButton(
                        text = deleteButtonText,
                        onClick = onClickDelete,
                        modifier = it
                    )
                },
                buttonLayout = buttonLayout
            )
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
            DialogButtons(
                negativeButtonContent = {
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    NegativeDialogButton(
                        text = deleteText,
                        onClick = onClickDelete,
                        enabled = internetEnabled,
                        modifier = it
                    )
                }
            )
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