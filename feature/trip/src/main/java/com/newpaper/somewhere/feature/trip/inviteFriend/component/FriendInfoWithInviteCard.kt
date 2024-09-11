package com.newpaper.somewhere.feature.trip.inviteFriend.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.NegativePositiveButtons
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.UserInfo
import com.newpaper.somewhere.core.ui.segmentedButtons.AllowEditViewSegmentedButtons
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun FriendInfoWithInviteCard(
    visible: Boolean,
    internetEnabled: Boolean,
    friendUserData: UserData,

    isEditable: Boolean,
    setIsAllowEdit: (isAllowEdit: Boolean) -> Unit,

    inviteButtonEnabled: Boolean,
    onClickCancel: () -> Unit,
    onClickInvite: () -> Unit,
    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(500),
            initialOffsetY = { (it * 2.5f).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(500),
            targetOffsetY = { (it * 2.5f).toInt() })
    ) {
        MyCard {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //friend info
                UserInfo(
                    internetEnabled = internetEnabled,
                    userData = friendUserData,
                    isManager = false,
                    downloadImage = downloadImage,
                    modifier = Modifier.widthIn(min = 330.dp)
                )

                MySpacerColumn(height = 16.dp)

                //set permission (allow edit / view only)
                AllowEditViewSegmentedButtons(
                    isEditable = isEditable,
                    setIsAllowEdit = setIsAllowEdit
                )

                MySpacerColumn(height = 16.dp)

                //cancel / invite buttons
                NegativePositiveButtons(
                    positiveButtonText = stringResource(id = R.string.invite),
                    onClickCancel = onClickCancel,
                    onClickPositive = onClickInvite,
                    positiveEnabled = inviteButtonEnabled
                )
            }
        }
    }
}
