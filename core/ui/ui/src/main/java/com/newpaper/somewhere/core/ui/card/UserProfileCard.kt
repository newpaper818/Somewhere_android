package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.ImageFromUrl
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun UserProfileCard(
    userData: UserData,
    internetEnabled: Boolean,
    downloadImage: (imagePath: String) -> Boolean,

    modifier: Modifier = Modifier,
    showSignInWithInfo: Boolean = false,
    profileImageSize: Dp = 80.dp,
    enabled: Boolean = true,
    onProfileClick: () -> Unit = { }
) {
    MyCard (
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        enabled = enabled,
        onClick = onProfileClick,
        modifier = modifier
    ){
        Row(
            modifier = Modifier.padding(12.dp, 16.dp, 16.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //profile image
            ProfileImage(
                userId = userData.userId,
                internetEnabled = internetEnabled,
                profileImage = userData.profileImage,
                downloadImage = downloadImage,
                size = profileImageSize
            )

            MySpacerRow(width = 16.dp)

            Column {
                //user name
                val nameText = userData.userName ?: stringResource(id = R.string.no_name)

                val textStyle = if (userData.userName != null) MaterialTheme.typography.bodyLarge
                                else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

                Text(
                    text = nameText,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                //email
                MySpacerColumn(height = 4.dp)

                Text(
                    text = userData.email ?: "no email",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    overflow = TextOverflow.Ellipsis
                )

                if (showSignInWithInfo) {
                    MySpacerColumn(height = 4.dp)

                    var connectedWithInfoText = stringResource(id = R.string.connected_with) + " "

                    userData.providerIdList.forEachIndexed { index, providerId ->
                        if (index != 0) {
                            connectedWithInfoText += ", "
                        }
                        connectedWithInfoText += providerId.providerName
                    }

                    Text(
                        text = connectedWithInfoText,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ProfileImage(
    userId: String,
    internetEnabled: Boolean,
    profileImage: String?,
    downloadImage: (imagePath: String) -> Boolean,

    modifier: Modifier = Modifier,
    size: Dp = 80.dp
){
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        if (profileImage != null) {
            if ("profile_" in profileImage)
                ImageFromFile(
                    internetEnabled = internetEnabled,
                    imagePath = profileImage,
                    contentDescription = stringResource(id = R.string.my_profile_image),
                    downloadImage = downloadImage,
                )
            else
                ImageFromUrl(
                    imageUrl = profileImage,
                    contentDescription = stringResource(id = R.string.my_profile_image),
                    modifier = Modifier.fillMaxSize()
                )
        } else {
            ImageFromDrawable(
                imageDrawable = R.drawable.default_profile,
                contentDescription = stringResource(id = R.string.default_profile_image)
            )
        }
    }
}






















@Composable
@PreviewLightDark
private fun UserProfileCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            UserProfileCard(
                userData = UserData(
                    userId = "",
                    userName = "user name",
                    email = "somewhere@gmail.com",
                    profileImage = null,
                    providerIdList = listOf()
                ),
                internetEnabled = true,
                downloadImage = {true}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun UserProfileCardWithProviderIdPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            UserProfileCard(
                userData = UserData(
                    userId = "",
                    userName = "user name",
                    email = "somewhere@gmail.com",
                    profileImage = null,
                    providerIdList = listOf(ProviderId.GOOGLE),
                ),
                internetEnabled = true,
                downloadImage = {true},
                showSignInWithInfo = true,
                enabled = false
            )
        }
    }
}