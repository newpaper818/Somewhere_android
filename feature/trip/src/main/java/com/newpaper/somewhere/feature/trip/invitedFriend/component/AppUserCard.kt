package com.newpaper.somewhere.feature.trip.invitedFriend.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
internal fun AppUserCard(
    isLoading: Boolean,
    appUserData: UserData,
    isManager: Boolean,
    internetEnabled: Boolean,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    onClickGetOut: () -> Unit
){
    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500))
    ) {
        MyCard(
            modifier = Modifier.widthIn(max = itemMaxWidth)
        ) {
            FriendInfoWithEditButton(
                friendData = appUserData,
                showEditDeleteButton = false,
                showGetOutButton = !isManager && internetEnabled,
                isManager = isManager,
                internetEnabled = internetEnabled,
                downloadImage = downloadImage,
                onClickEditable = { },
                onClickDeleteFriend = { },
                onClickGetOut = onClickGetOut
            )
        }
    }
}