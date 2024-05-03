package com.newpaper.somewhere.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.card.ProfileImage
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun FriendInfo(
    internetEnabled: Boolean,
    friendData: UserData,
    isManager: Boolean,
    downloadImage: (imagePath: String) -> Boolean,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        MySpacerRow(width = 8.dp)

        ProfileImage(
            userId = friendData.userId,
            internetEnabled = internetEnabled,
            profileImage = friendData.profileImage,
            downloadImage = downloadImage,
            size = 40.dp
        )

        MySpacerRow(width = 12.dp)

        Column {
            //user name
            val nameText = friendData.userName ?: stringResource(id = R.string.unknown)

            val textStyle = if (friendData.userName != null) MaterialTheme.typography.bodyLarge
            else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nameText,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isManager) {
                    MySpacerRow(width = 6.dp)
                    DisplayIcon(icon = MyIcons.manager)
                }
            }

            //email
            if (friendData.email != null){
                MySpacerColumn(height = 2.dp)

                Text(
                    text = friendData.email!!,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}