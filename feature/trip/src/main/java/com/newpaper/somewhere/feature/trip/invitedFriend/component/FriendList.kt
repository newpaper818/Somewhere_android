package com.newpaper.somewhere.feature.trip.invitedFriend.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.item.ItemDivider
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun FriendList(
    isLoading: Boolean,
    managerId: String,
    friendList: List<UserData>,
    showEditButton: Boolean,
    internetEnabled: Boolean,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    onClickEditable: (friendUserData: UserData) -> Unit,
    onClickDeleteFriend: (friendUserData: UserData) -> Unit
){
    val friendListSize = friendList.size
    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        Column(
            modifier = Modifier.widthIn(max = itemMaxWidth)
        ) {
            Text(
                text = if (friendListSize == 0) ""
                else "$friendListSize " + stringResource(id = R.string.friends),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

            MySpacerColumn(height = 4.dp)

            if (friendListSize == 0) {
                NoFriendItem()
            } else {
                MyCard {
                    friendList.forEachIndexed { index, friendData ->
                        FriendInfoWithEditButton(
                            friendData = friendData,
                            showEditDeleteButton = showEditButton,
                            showGetOutButton = false,
                            isManager = friendData.userId == managerId,
                            internetEnabled = internetEnabled,
                            downloadImage = downloadImage,
                            onClickEditable = onClickEditable,
                            onClickDeleteFriend = onClickDeleteFriend,
                            onClickGetOut = { }
                        )

                        if (index != friendListSize - 1) {
                            ItemDivider(startPadding = 68.dp)
                        }
                    }
                }
            }
        }
    }
}