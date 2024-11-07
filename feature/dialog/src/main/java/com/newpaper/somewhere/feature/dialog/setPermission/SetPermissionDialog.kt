package com.newpaper.somewhere.feature.dialog.setPermission

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.UserInfo
import com.newpaper.somewhere.core.ui.segmentedButtons.AllowEditViewSegmentedButtons
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.OkDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SetPermissionDialog(
    internetEnabled: Boolean,
    friendData: UserData,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,

    onDismissRequest: () -> Unit,
    onClickOk: (editable: Boolean) -> Unit
) {

    var newEditable by rememberSaveable { mutableStateOf(friendData.allowEdit) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.set_editable),
        bodyContent = {
            //user info card
            UserInfo(
                internetEnabled = internetEnabled,
                userData = friendData,
                isManager = false,
                downloadImage = downloadImage,
                modifier = Modifier.padding(start = 8.dp)
            )

            MySpacerColumn(height = 16.dp)

            //allow edit / view only button
            AllowEditViewSegmentedButtons(
                isEditable = newEditable,
                setIsAllowEdit = {
                    newEditable = it
                }
            )
        },
        buttonContent = {
            DialogButtons(
                buttonLayout = ButtonLayout.HORIZONTAL,
                negativeButtonContent = {
                    //cancel button
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    //ok button
                    OkDialogButton(
                        onClick = { onClickOk(newEditable) },
                        modifier = it
                    )
                }
            )
        }
    )
}



























@Composable
@PreviewLightDark
private fun SetEditableDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            SetPermissionDialog(
                internetEnabled = true,
                friendData = UserData(
                    userId = "",
                    userName = "nameee",
                    email = "email@gmail.com",
                    profileImagePath = null,
                    providerIds = listOf(),
                ),
                downloadImage = {_,_,_->},
                onDismissRequest = {},
                onClickOk = { _ ->}
            )
        }
    }
}