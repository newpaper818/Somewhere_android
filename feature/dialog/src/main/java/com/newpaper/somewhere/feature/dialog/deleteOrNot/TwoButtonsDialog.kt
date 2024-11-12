package com.newpaper.somewhere.feature.dialog.deleteOrNot

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DangerDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.PositiveDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun TwoButtonsDialog(
    positiveButtonText: String,
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit,

    titleText: String? = null,
    bodyText: String? = null,
    subBodyText: String? = null,
    buttonLayout: ButtonLayout = ButtonLayout.AUTO,
    positiveIsDangerButton: Boolean = true

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
                    if (positiveIsDangerButton) {
                        DangerDialogButton(
                            text = positiveButtonText,
                            onClick = onClickPositive,
                            modifier = it
                        )
                    }
                    else {
                        PositiveDialogButton(
                            text = positiveButtonText,
                            onClick = onClickPositive,
                            modifier = it
                        )
                    }
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
                    DangerDialogButton(
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
    TwoButtonsDialog(
        bodyText = stringResource(id = R.string.dialog_delete_friend),
        positiveButtonText = stringResource(id = R.string.button_delete),
        onDismissRequest = onDismissRequest,
        onClickPositive = onClickDelete
    )
}

@Composable
fun GetOutSharedTripDialog(
    onDismissRequest: () -> Unit,
    onClickGetOut: () -> Unit
){
    TwoButtonsDialog(
        bodyText = stringResource(id = R.string.leave_shared_trip),
        positiveButtonText = stringResource(id = R.string.button_get_out),
        onDismissRequest = onDismissRequest,
        onClickPositive = onClickGetOut
    )
}
























@Composable
@PreviewLightDark
private fun DeleteOrNotDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            TwoButtonsDialog(
                bodyText = "Delete item??????????????? ????????????\n?????????????????????????",
                positiveButtonText = "Delete",
                onDismissRequest = {},
                onClickPositive = {}
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