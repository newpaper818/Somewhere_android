package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.R
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.enumUtils.ProviderId
import com.newpaper.somewhere.ui.components.ClickableBox
import com.newpaper.somewhere.ui.components.MySpacerColumn
import com.newpaper.somewhere.ui.components.MySpacerRow
import com.newpaper.somewhere.ui.components.cards.ImageFromDrawable
import com.newpaper.somewhere.ui.theme.SomewhereTheme
import com.newpaper.somewhere.ui.theme.n60

@Composable
fun SignInWithGoogleButton(
    buttonEnabled: Boolean,
    onClick: () -> Unit,

    useBorder: Boolean,

    useCenterAlignText: Boolean = false,
    width: Dp? = 270.dp,
    text: String = stringResource(id = R.string.sign_in_with_google)
){
    SignInWithButton(
        iconDrawable = R.drawable.google_logo,
        text = text,

        containerColor = Color.White,
        textColor = Color.Black,
        useBorder = useBorder,

        buttonEnabled = buttonEnabled,
        onClick = onClick,

        useCenterAlignText = useCenterAlignText,
        width = width
    )
}

@Composable
fun SignInWithAppleButton(
    buttonEnabled: Boolean,
    onClick: () -> Unit,

    useBorder: Boolean,

    useCenterAlignText: Boolean = false,
    width: Dp? = 270.dp,
    text: String = stringResource(id = R.string.sign_in_with_apple)
){
    SignInWithButton(
        iconDrawable = R.drawable.apple_logo,
        text = text,

        containerColor = Color.Black,
        textColor = Color.White,
        useBorder = useBorder,

        buttonEnabled = buttonEnabled,
        onClick = onClick,

        useCenterAlignText = useCenterAlignText,
        width = width,
    )
}

@Composable
private fun SignInWithButton(
    iconDrawable: Int,
    text: String,

    containerColor: Color,
    textColor: Color,
    useBorder: Boolean,

    buttonEnabled: Boolean,
    onClick: () -> Unit,

    useCenterAlignText: Boolean = false,
    width: Dp? = 270.dp,
    logoImageDescription: String = "",
){
    val modifier = if (useBorder) Modifier.border(1.dp, MaterialTheme.colorScheme.outline,
        CircleShape
    )
                    else Modifier

    ClickableBox(
        modifier = modifier,
        containerColor = containerColor,
        shape = CircleShape,

        enabled = buttonEnabled,
        onClick = onClick

    ) {
        val rowModifier =
            if (width != null) Modifier
                .width(width)
                .padding(16.dp)
            else Modifier.padding(16.dp)

        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //icon
            ImageFromDrawable(
                imageDrawable = iconDrawable,
                contentDescription = logoImageDescription,
                modifier = Modifier.size(26.dp)
            )

            if (!useCenterAlignText)
                MySpacerRow(width = 24.dp)

            //text
            Text(
                text = text,
                style = if (buttonEnabled) MaterialTheme.typography.labelLarge.copy(color = textColor)
                        else MaterialTheme.typography.labelLarge.copy(color = n60),
                textAlign = if (useCenterAlignText) TextAlign.Center
                            else TextAlign.Left,
                modifier = if (useCenterAlignText && width != null) Modifier
                                .fillMaxWidth()
                                .padding(4.dp, 0.dp, 6.dp, 0.dp)
                            else Modifier
            )
        }
    }
}

@Composable
fun AuthButtons(
    providerIdList: List<ProviderId>,
    enabled: Boolean,
    onClick: (providerId: ProviderId) -> Unit,
    authingWith: ProviderId?,
    isDarkAppTheme: Boolean,
    isAuthing: Boolean,
    isAuthDone: Boolean
){
    if (ProviderId.GOOGLE in providerIdList){
        MySpacerColumn(height = 16.dp)

        SignInWithGoogleButton(
            buttonEnabled = enabled && !isAuthDone,
            onClick = { onClick(ProviderId.GOOGLE) },
            useBorder = !isDarkAppTheme,
            useCenterAlignText = true,
            width = 300.dp,
            text = if (authingWith == ProviderId.GOOGLE && isAuthDone) stringResource(id = R.string.authentication_done)
            else if (authingWith == ProviderId.GOOGLE && isAuthing) stringResource(id = R.string.authenticating)
            else stringResource(id = R.string.authentication_with_google)
        )
    }
    if (ProviderId.APPLE in providerIdList){
        MySpacerColumn(height = 16.dp)

        SignInWithAppleButton(
            buttonEnabled = enabled && !isAuthDone,
            onClick = { onClick(ProviderId.APPLE) },
            useBorder = isDarkAppTheme,
            useCenterAlignText = true,
            width = 300.dp,
            text = if (authingWith == ProviderId.APPLE && isAuthDone) stringResource(id = R.string.authentication_done)
            else if (authingWith == ProviderId.APPLE && isAuthing) stringResource(id = R.string.authenticating)
            else stringResource(id = R.string.authentication_with_apple)
        )
    }
}






















@Composable
@PreviewLightDark
private fun Preview_SignInWithGoogleButton(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            SignInWithGoogleButton(
                buttonEnabled = true,
                onClick = {},
                useBorder = !isSystemInDarkTheme()
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_SignInWithAppleButton(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            SignInWithAppleButton(
                buttonEnabled = true,
                onClick = {},
                useBorder = isSystemInDarkTheme()
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_AuthButtons(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthButtons(
                providerIdList = listOf(ProviderId.GOOGLE, ProviderId.APPLE),
                enabled = true,
                onClick = {},
                authingWith = null,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthing = false,
                isAuthDone = false
            )
        }
    }
}


@Composable
@PreviewLightDark
private fun Preview_AuthButtons1(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthButtons(
                providerIdList = listOf(ProviderId.GOOGLE, ProviderId.APPLE),
                enabled = false,
                onClick = {},
                authingWith = null,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthing = false,
                isAuthDone = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_AuthButtons2(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthButtons(
                providerIdList = listOf(ProviderId.GOOGLE, ProviderId.APPLE),
                enabled = false,
                onClick = {},
                authingWith = ProviderId.GOOGLE,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthing = true,
                isAuthDone = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_AuthButtons3(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthButtons(
                providerIdList = listOf(ProviderId.GOOGLE, ProviderId.APPLE),
                enabled = false,
                onClick = {},
                authingWith = ProviderId.GOOGLE,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthing = true,
                isAuthDone = true
            )
        }
    }
}