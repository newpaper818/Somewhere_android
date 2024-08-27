package com.newpaper.somewhere.feature.trip.invitedFriend.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.FriendInfo

@Composable
internal fun FriendInfoWithEditButton(
    friendData: UserData,
    showEditDeleteButton: Boolean,
    showGetOutButton: Boolean,
    isManager: Boolean,
    internetEnabled: Boolean,

    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    onClickEditable: (friendUserData: UserData) -> Unit,
    onClickDeleteFriend: (friendUserData: UserData) -> Unit,
    onClickGetOut: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 8.dp, 16.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FriendInfo(
            internetEnabled = internetEnabled,
            friendData = friendData,
            isManager = isManager,
            downloadImage = downloadImage,
            modifier = Modifier.weight(1f)
        )

        if (showEditDeleteButton) {
            //allow edit / view only button
            MyPlainTooltipBox(
                tooltipText = if (friendData.allowEdit) stringResource(id = MyIcons.allowEdit.descriptionTextId)
                                else stringResource(id = MyIcons.viewOnly.descriptionTextId)
            ) {
                IconButton(
                    enabled = internetEnabled,
                    onClick = { onClickEditable(friendData) }
                ) {
                    DisplayIcon(
                        icon = if (friendData.allowEdit) MyIcons.allowEdit
                        else MyIcons.viewOnly
                    )
                }
            }

            //delete friend button
            if (internetEnabled) {
                MyPlainTooltipBox(tooltipText = stringResource(id = MyIcons.deleteFriend.descriptionTextId)) {
                    IconButton(
                        onClick = { onClickDeleteFriend(friendData) }
                    ) {
                        DisplayIcon(icon = MyIcons.deleteFriend)
                    }
                }
            }
        }

        if (showGetOutButton && internetEnabled) {
            MyPlainTooltipBox(tooltipText = stringResource(id = MyIcons.leaveTrip.descriptionTextId)) {
                IconButton(
                    onClick = { onClickGetOut() }
                ) {
                    DisplayIcon(icon = MyIcons.leaveTrip)
                }
            }
        }
    }
}