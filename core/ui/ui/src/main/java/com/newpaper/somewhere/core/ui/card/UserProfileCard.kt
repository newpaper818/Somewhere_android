package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.ImageFromUrl
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
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier,
    showSignInWithInfo: Boolean = false,
    profileImageSize: Dp = 80.dp,
    enabled: Boolean = true,
    onProfileClick: () -> Unit = { },
) {
    val boxModifier =
        if (enabled) modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceBright)
            .clickable { onProfileClick() }
        else modifier
            .clip(MaterialTheme.shapes.medium)

    val userInfo = stringResource(id = R.string.user_info)

    Box(
        modifier = boxModifier
            .semantics(mergeDescendants = true) {
                contentDescription = userInfo
            }
    ){
        Row(
            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //profile image
            ProfileImage(
                profileUserId = userData.userId,
                internetEnabled = internetEnabled,
                profileImagePath = userData.profileImagePath,
                downloadImage = downloadImage,
                size = profileImageSize,
                modifier = Modifier.clearAndSetSemantics { }
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
                    text = userData.email ?: stringResource(id = R.string.no_email),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    overflow = TextOverflow.Ellipsis
                )

                if (showSignInWithInfo) {
                    MySpacerColumn(height = 4.dp)

                    var connectedWithInfoText = stringResource(id = R.string.connected_with) + " "

                    userData.providerIds.forEachIndexed { index, providerId ->
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


/**
 *
 *
 * @param profileUserId
 * @param internetEnabled
 * @param profileImagePath it can be url or image file name
 * @param downloadImage
 * @param modifier
 * @param size
 */
@Composable
fun ProfileImage(
    profileUserId: String,
    internetEnabled: Boolean,
    profileImagePath: String?,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,

    modifier: Modifier = Modifier,
    size: Dp = 80.dp
){
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        if (profileImagePath != null) {
            if ("profile_" in profileImagePath)
                ImageFromFile(
                    internetEnabled = internetEnabled,
                    imageUserId = profileUserId,
                    imagePath = profileImagePath,
                    contentDescription = stringResource(id = R.string.my_profile_image),
                    downloadImage = downloadImage
                )
            else
                ImageFromUrl(
                    imageUrl = profileImagePath,
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
                    profileImagePath = "https://lh3.googleusercontent.com/a/ACg8ocIr4TMfwXVpUC1Bk-VuEgNqPo9A7D0ljwsahznS82iJ-40=s96-c",
                    providerIds = listOf(),
                ),
                internetEnabled = true,
                downloadImage = {_,_,_-> }
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
                    profileImagePath = null,
                    providerIds = listOf(ProviderId.GOOGLE),
                ),
                internetEnabled = true,
                showSignInWithInfo = true,
                enabled = false,
                downloadImage = {_,_,_-> }
            )
        }
    }
}