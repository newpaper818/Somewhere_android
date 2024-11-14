package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.IconTextButtonIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyButton(
    onClick: () -> Unit,
){
    val grayRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    CompositionLocalProvider(LocalRippleConfiguration provides grayRippleConfiguration) {
        TextButton(
            onClick = onClick
        ) {
            Text(
                text = stringResource(id = R.string.privacy_policy),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Composable
fun RemoveAdsButton(
    onClick: () -> Unit,
) {
    MyTextRippleButton(
        text = stringResource(id = R.string.remove_ads),
        textStyle = MaterialTheme.typography.labelLarge,
        textColor = MaterialTheme.colorScheme.onSurfaceVariant,
        onClick = onClick
    )
}

@Composable
fun MyQrCodeButton(
    onClick: () -> Unit
){
    IconTextButton(
        icon = IconTextButtonIcon.qrCode,
        text = stringResource(id = R.string.my_qr_code),
        onClick = onClick
    )
}

@Composable
fun TryAgainButton(
    onClick: () -> Unit,
    enabled: Boolean
){
    MyTextButton(
        text = stringResource(id = R.string.try_again),
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
fun PrevButton(
    onClick: () -> Unit
){
    MyTextButton(
        text = stringResource(id = R.string.previous),
        onClick = onClick,
        containerColor = Color.Transparent
    )
}

@Composable
fun NextButton(
    onClick: () -> Unit,
    enabled: Boolean
){
    MyTextButton(
        text = stringResource(id = R.string.next),
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
fun CreateTripButton(
    onClick: () -> Unit,
    enabled: Boolean
){
    MyTextButton(
        text = stringResource(id = R.string.create_trip),
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
fun UpdateButton(
    onClick: () -> Unit
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 75.dp),
        text = stringResource(id = R.string.update),
        onClick = onClick
    )
}

@Composable
fun ShareAppButton(
    onClick: () -> Unit,
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 120.dp),
        text = stringResource(id = R.string.share_app),
        containerColor = Color.Transparent,
        onClick = onClick
    )
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 100.dp),
        text = stringResource(id = R.string.settings),
        onClick = onClick
    )
}

@Composable
fun InviteFriendWithEmailButton(
    onClick: () -> Unit,
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 120.dp),
        text = stringResource(id = R.string.invite_friend_with_email),
        containerColor = Color.Transparent,
        onClick = onClick
    )
}

@Composable
fun InviteFriendWithQrCodeButton(
    onClick: () -> Unit,
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 120.dp),
        text = stringResource(id = R.string.invite_friend_with_qr_code),
        containerColor = Color.Transparent,
        onClick = onClick
    )
}

@Composable
fun DeleteAccountButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
){
    MyTextButton(
        text = stringResource(id = R.string.delete_account),
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.width(180.dp),
        containerColor =  MaterialTheme.colorScheme.error,
        textStyle = if (enabled) MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onError)
                    else MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    )
}

@Composable
fun NewItemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconTextButton(
        icon = IconTextButtonIcon.add,
        text = text,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun DeleteItemButton(
    visible: Boolean,
    text: String,
    onClick: () -> Unit
){
    Box(modifier = Modifier.height(40.dp)){
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + scaleIn(tween(300)),
            exit = fadeOut(tween(300)) + scaleOut(tween(400))
        ) {
            IconTextButton(
                icon = IconTextButtonIcon.delete,
                text = text,
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.error,
                textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onError)
            )
        }
    }

}

@Composable
fun ChangeProfileImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconTextButtonColumn(
        icon = MyIcons.changeProfileImage,
        text = stringResource(id = R.string.change_image),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
fun DeleteProfileImageButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
){
    IconTextButtonColumn(
        icon = MyIcons.deleteProfileImage,
        text = stringResource(id = R.string.delete_image),
        onClick = onClick,
        enabled = enabled,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}


@Composable
fun ToPrevDateButton(
    text: String,
    onClick: () -> Unit
){
    Button(
        contentPadding = PaddingValues(8.dp, 0.dp, 20.dp, 0.dp),
        onClick = onClick,
        modifier = Modifier.offset(1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DisplayIcon(icon = IconTextButtonIcon.leftArrow)

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ToNextDateButton(
    text: String,
    onClick: () -> Unit
){
    Button(
        contentPadding = PaddingValues(20.dp, 0.dp, 8.dp, 0.dp),
        onClick = onClick,
        modifier = Modifier.offset((-1).dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            DisplayIcon(icon = IconTextButtonIcon.rightArrow)
        }
    }
}

@Composable
private fun MyTextButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
){
    if (containerColor == Color.Transparent){
        TextButton(
            enabled = enabled,
            onClick = onClick,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = textStyle,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    else {
        Button(
            enabled = enabled,
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = containerColor)
        ) {
            Text(
                text = text,
                style = textStyle,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyTextRippleButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    textColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
){
    val textStyle1 =
        if (enabled) textStyle.copy(color = textColor)
        else textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val rippleConfiguration = RippleConfiguration(
        color = textColor
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
        ) {
            Text(
                text = text,
                style = textStyle1,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IconTextButton(
    icon: MyIcon,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
){
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)
        ),
        contentPadding = PaddingValues(16.dp, 0.dp, 20.dp, 0.dp),
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DisplayIcon(
                icon = icon
            )

            MySpacerRow(8.dp)

            Text(
                text = text,
                style = textStyle,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IconTextButtonColumn(
    icon: MyIcon,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
){
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(14.dp, 8.dp),
        enabled = enabled,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayIcon(icon = icon)

            MySpacerColumn(6.dp)

            Text(
                text = text,
                style = if (enabled) textStyle
                        else textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}






















@Composable
@PreviewLightDark
private fun PrivacyPolicyPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ){
            PrivacyPolicyButton(
                onClick = {},
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun UpdatePreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ){
            UpdateButton(
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun CopyAppPlayStoreLinkButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(260.dp)
        ) {
            ShareAppButton{ }
        }
    }
}

@Composable
@PreviewLightDark
private fun DeleteAccountButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            DeleteAccountButton(
                enabled = true,
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun DeleteAccountButtonDisabledPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            DeleteAccountButton(
                enabled = false,
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun NewItemButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            NewItemButton(
                text = "New item",
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun DeleteItemButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            DeleteItemButton(
                visible = true,
                text = "Delete item",
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ToPrevDateButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            ToPrevDateButton(
                text = "Mar 13",
                onClick = { }
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ToNextDateButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            ToNextDateButton(
                text = "Mar 13",
                onClick = { }
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun MyTextButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            MyTextButton(
                text = "text button",
                onClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun MyTextButtonDisabledPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            MyTextButton(
                text = "text button",
                onClick = {},
                enabled = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun MyTextButtonTransparentPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            MyTextButton(
                text = "text button",
                onClick = {},
                containerColor = Color.Transparent
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun MyTextButtonTransparentDisabledPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            MyTextButton(
                text = "text button",
                onClick = {},
                containerColor = Color.Transparent,
                enabled = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun IconTextButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            IconTextButton(
                icon = IconTextButtonIcon.add,
                text = "Icon text button",
                onClick = {}
            )
        }
    }
}