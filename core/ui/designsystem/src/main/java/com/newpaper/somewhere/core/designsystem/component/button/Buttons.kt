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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.IconTextButtonIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R


@Composable
fun PrivacyPolicyButton(
    onClick: () -> Unit,
){
    MyTextButton(
        text = stringResource(id = R.string.privacy_policy),
        textStyle = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.Underline),
        onClick = onClick,
        containerColor = Color.Transparent
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
fun NewTripButton(
    visible: Boolean,
    onClick: () -> Unit
){
    Box(modifier = Modifier.height(40.dp)){
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(300)) + scaleIn(tween(300)),
            exit = fadeOut(tween(300)) + scaleOut(tween(300))
        ) {
            IconTextButton(
                icon = IconTextButtonIcon.add,
                text = stringResource(id = R.string.new_trip),
                onClick = onClick,
                textStyle = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun InviteButton(
    onClick: () -> Unit,
    enabled: Boolean
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 120.dp),
        text = stringResource(id = R.string.invite),
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
    enabled: Boolean
){
    MyTextButton(
        modifier = Modifier.widthIn(min = 120.dp),
        text = stringResource(id = R.string.save),
        onClick = onClick,
        enabled = enabled
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
    onClick: () -> Unit
){
    IconTextButton(
        icon = IconTextButtonIcon.add,
        text = text,
        onClick = onClick
    )
}

@Composable
fun DeleteItemButton(
    text: String,
    onClick: () -> Unit
){
    IconTextButton(
        icon = IconTextButtonIcon.delete,
        text = text,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.error,
        textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onError)
    )
}


@Composable
fun ToPrevDateButton(
    text: String,
    onClick: () -> Unit
){
    Button(
        contentPadding = PaddingValues(8.dp, 0.dp, 20.dp, 0.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DisplayIcon(icon = IconTextButtonIcon.leftArrow)

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
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
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
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
                style = textStyle
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
                style = textStyle
            )
        }
    }
}

@Composable
private fun IconTextButton(
    icon: MyIcon,
    text: String,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
){
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)
        ),
        contentPadding = PaddingValues(16.dp, 0.dp, 20.dp, 0.dp),
        onClick = onClick
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
                style = textStyle
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
private fun NewTripButtonPreview(){
    SomewhereTheme {
        Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .width(190.dp)
        ) {
            NewTripButton(visible = true) {}
        }
    }
}

@Composable
@PreviewLightDark
private fun InviteButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            InviteButton(
                onClick = { },
                enabled = true
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun InviteButtonDisabledPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            InviteButton(
                onClick = { },
                enabled = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SaveButtonPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            SaveButton(
                onClick = { },
                enabled = true
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SaveButtonDisabledPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(190.dp)
        ) {
            SaveButton(
                onClick = { },
                enabled = false
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