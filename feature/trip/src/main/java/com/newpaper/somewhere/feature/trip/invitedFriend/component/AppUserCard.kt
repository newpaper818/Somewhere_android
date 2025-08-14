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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.trip.R

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
        Column(
            modifier = Modifier
                .widthIn(max = itemMaxWidth)
                .semantics(mergeDescendants = true) { }
        ) {
            Text(
                text = stringResource(id = R.string.me),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

            MySpacerColumn(height = 4.dp)

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
}