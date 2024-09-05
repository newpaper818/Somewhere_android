package com.newpaper.somewhere.feature.dialog.myQrCode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeView
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.ui.card.ProfileImage
import com.newpaper.somewhere.feature.dialog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyQrCodeDialog(
    userData: UserData,
    internetEnabled: Boolean,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    onDismissRequest: () -> Unit
){
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserInfo(
                    internetEnabled = internetEnabled,
                    userData = userData,
                    downloadImage = downloadImage,
                    modifier = Modifier.width(230.dp)
                )

                MySpacerColumn(height = 20.dp)

                //qr code
                UserQrCode(
                    userId = userData.userId,
                    modifier = Modifier.size(230.dp)
                )
            }
        }
    }
}


@Composable
private fun UserInfo(
    internetEnabled: Boolean,
    userData: UserData,
    downloadImage: (imagePath: String, profileUserId: String, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ProfileImage(
            profileUserId = userData.userId,
            internetEnabled = internetEnabled,
            profileImagePath = userData.profileImagePath,
            downloadImage = downloadImage,
            size = 44.dp
        )

        MySpacerRow(width = 12.dp)

        Column {
            //user name
            UserName(userData = userData)

            MySpacerColumn(height = 4.dp)

            UserEmail(userData = userData)
        }
    }
}

@Composable
private fun UserName(
    userData: UserData
){
    val nameText = userData.userName ?: stringResource(id = R.string.no_name)

    val textStyle = if (userData.userName != null) MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
        else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    Text(
        text = nameText,
        style = textStyle,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun UserEmail(
    userData: UserData
){
    val emailText = userData.email ?: stringResource(id = R.string.no_email)

    val textStyle = if (userData.userName != null) MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        else MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    Text(
        text = emailText,
        style = textStyle,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun UserQrCode(
    userId: String,
    modifier: Modifier = Modifier
){
    QrCodeView(
        data = "somewhere_$userId",
        modifier = modifier,
        dotShape = DotShape.Circle
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            ImageFromDrawable(
                imageDrawable = com.newpaper.somewhere.core.ui.ui.R.drawable.app_icon_fit,
                contentDescription = stringResource(id = com.newpaper.somewhere.core.ui.ui.R.string.somewhere_app_icon),
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxSize()
            )
        }
    }
}

@PreviewLightDark
@Composable
fun UserQrCodePreview(){
    SomewhereTheme {
        MyScaffold {
            MyQrCodeDialog(
                userData = UserData(
                    userId = "123",
                    userName = "user name",
                    email = "useremail@gmail.com",
                    profileImagePath = "",
                    providerIds = listOf(),
                    allowEdit = false
                ),
                internetEnabled = true,
                downloadImage = { _, _, _ ->},
                onDismissRequest = {}
            )
        }
    }
}
