package com.newpaper.somewhere.feature.dialog.setEditable

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.FriendInfo
import com.newpaper.somewhere.core.ui.selectSwitch.AllowEditViewSelectSwitch
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SetEditableDialog(
    internetEnabled: Boolean,
    friendData: UserData,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,

    onDismissRequest: () -> Unit,
    onClickOk: (editable: Boolean) -> Unit
) {

    var newEditable by rememberSaveable { mutableStateOf(friendData.allowEdit) }

    MyDialog(
        onDismissRequest = { /*TODO*/ },
        titleText = stringResource(id = R.string.set_editable),
        bodyContent = {
            //user info card
            FriendInfo(
                internetEnabled = internetEnabled,
                friendData = friendData,
                isManager = false,
                downloadImage = downloadImage
            )

            MySpacerColumn(height = 16.dp)

            //allow edit / view only button
            AllowEditViewSelectSwitch(
                isEditable = newEditable,
                setIsAllowEdit = {
                    newEditable = it
                }
            )
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    onClick = {
                        onClickOk(newEditable)
                    }
                )
            }
        }
    )
}



























@Composable
@PreviewLightDark
private fun SetEditableDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            SetEditableDialog(
                internetEnabled = true,
                friendData = UserData(
                    userId = "",
                    userName = "nameee",
                    email = "email@gmail.com",
                    profileImagePath = null,
                    providerIds = listOf()
                ),
                downloadImage = {_,_,_->},
                onDismissRequest = {},
                onClickOk = { _ ->}
            )
        }
    }
}