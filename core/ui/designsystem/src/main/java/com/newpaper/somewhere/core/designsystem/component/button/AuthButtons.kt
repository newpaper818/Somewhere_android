package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.n60
import com.newpaper.somewhere.core.model.enums.ProviderId
import com.newpaper.somewhere.core.ui.designsystem.R

@Composable
fun SignInWithButton(
    providerId: ProviderId,
    onClick: () -> Unit,
    buttonEnabled: Boolean,
    isDarkAppTheme: Boolean,
    modifier: Modifier = Modifier
){
    when (providerId){
        ProviderId.GOOGLE -> {
            SignInAuthButton(
                iconDrawable = R.drawable.google_logo,
                text = stringResource(id = R.string.sign_in_with_google),

                containerColor = Color.White,
                textColor = Color.Black,
                useBorder = !isDarkAppTheme,

                buttonEnabled = buttonEnabled,
                onClick = onClick,

                isSignIn = true,
                modifier = modifier
            )
        }
//        ProviderId.APPLE ->{
//            SignInAuthButton(
//                iconDrawable = R.drawable.apple_logo,
//                text = stringResource(id = R.string.sign_in_with_apple),
//
//                containerColor = Color.Black,
//                textColor = Color.White,
//                useBorder = isDarkAppTheme,
//
//                buttonEnabled = buttonEnabled,
//                onClick = onClick,
//
//                isSignIn = true,
//                modifier = modifier
//            )
//        }
    }
}

@Composable
fun AuthWithButton(
    providerId: ProviderId,
    authingWithThis: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    isDarkAppTheme: Boolean,
    isAuthDone: Boolean
){
    when (providerId){
        ProviderId.GOOGLE -> {
            SignInAuthButton(
                iconDrawable = R.drawable.google_logo,
                text = if (authingWithThis && isAuthDone) stringResource(id = R.string.authentication_complete)
                    else if (authingWithThis) stringResource(id = R.string.authenticating)
                    else stringResource(id = R.string.authentication_with_google),
                containerColor = Color.White,
                textColor = Color.Black,
                useBorder = !isDarkAppTheme,

                buttonEnabled = enabled,
                onClick = onClick,

                isSignIn = false
            )
        }
//        ProviderId.APPLE ->{
//            SignInAuthButton(
//                iconDrawable = R.drawable.apple_logo,
//                text = if (authingWithThis && isAuthDone) stringResource(id = R.string.authentication_complete)
//                    else if (authingWithThis) stringResource(id = R.string.authenticating)
//                    else stringResource(id = R.string.authentication_with_apple),
//                containerColor = Color.Black,
//                textColor = Color.White,
//                useBorder = isDarkAppTheme,
//
//                buttonEnabled = enabled,
//                onClick = onClick,
//
//                isSignIn = false
//            )
//        }
    }
}

@Composable
private fun SignInAuthButton(
    iconDrawable: Int,
    text: String,

    containerColor: Color,
    textColor: Color,
    useBorder: Boolean,

    buttonEnabled: Boolean,
    onClick: () -> Unit,

    isSignIn: Boolean, // or auth
    modifier: Modifier = Modifier
){
    val buttonModifier = if (useBorder) modifier.border(1.dp, MaterialTheme.colorScheme.outline,
        SmoothRoundedCornerShape(999.dp, 1f)
    )
                    else modifier

    ClickableBox(
        modifier = buttonModifier,
        containerColor = containerColor,
        shape = SmoothRoundedCornerShape(999.dp, 1f),

        enabled = buttonEnabled,
        onClick = onClick

    ) {
        val rowModifier =
            if (isSignIn) Modifier
                .width(270.dp)
                .padding(16.dp)
            else Modifier
                .width(300.dp)
                .padding(16.dp)

        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //icon
            Image(
                painter = painterResource(id = iconDrawable),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(26.dp)
            )

            if (isSignIn)
                MySpacerRow(width = 24.dp)

            //text
            Text(
                text = text,
                style = if (buttonEnabled) MaterialTheme.typography.labelLarge.copy(color = textColor)
                        else MaterialTheme.typography.labelLarge.copy(color = n60),
                textAlign = if (!isSignIn) TextAlign.Center
                            else TextAlign.Left,
                modifier = if (!isSignIn) Modifier
                                .fillMaxWidth()
                                .padding(4.dp, 0.dp, 6.dp, 0.dp)
                            else Modifier
            )
        }
    }
}






















@Composable
@PreviewLightDark
private fun SignInButtonsPreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            SignInWithButton(
                providerId = ProviderId.GOOGLE,
                onClick = {  },
                buttonEnabled = true,
                isDarkAppTheme = isSystemInDarkTheme()
            )

            MySpacerColumn(height = 16.dp)

//            SignInWithButton(
//                providerId = ProviderId.APPLE,
//                onClick = {  },
//                buttonEnabled = true,
//                isDarkAppTheme = isSystemInDarkTheme()
//            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SignInDisabledPreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            SignInWithButton(
                providerId = ProviderId.GOOGLE,
                onClick = {  },
                buttonEnabled = false,
                isDarkAppTheme = isSystemInDarkTheme()
            )

            MySpacerColumn(height = 16.dp)

//            SignInWithButton(
//                providerId = ProviderId.APPLE,
//                onClick = {  },
//                buttonEnabled = false,
//                isDarkAppTheme = isSystemInDarkTheme()
//            )
        }
    }
}

@Composable
@PreviewLightDark
private fun AuthButtonsPreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthWithButton(
                providerId = ProviderId.GOOGLE,
                authingWithThis = false,
                onClick = { },
                enabled = true,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthDone = false
            )

            MySpacerColumn(height = 16.dp)
            
//            AuthWithButton(
//                providerId = ProviderId.APPLE,
//                authingWithThis = false,
//                onClick = { },
//                enabled = true,
//                isDarkAppTheme = isSystemInDarkTheme(),
//                isAuthDone = false
//            )
        }
    }
}




@Composable
@PreviewLightDark
private fun AuthDisabledPreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthWithButton(
                providerId = ProviderId.GOOGLE,
                authingWithThis = false,
                onClick = { },
                enabled = false,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthDone = false
            )

            MySpacerColumn(height = 16.dp)

//            AuthWithButton(
//                providerId = ProviderId.APPLE,
//                authingWithThis = false,
//                onClick = { },
//                enabled = false,
//                isDarkAppTheme = isSystemInDarkTheme(),
//                isAuthDone = false
//            )
        }
    }
}

@Composable
@PreviewLightDark
private fun AuthingPreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthWithButton(
                providerId = ProviderId.GOOGLE,
                authingWithThis = true,
                onClick = { },
                enabled = false,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthDone = false
            )

            MySpacerColumn(height = 16.dp)

//            AuthWithButton(
//                providerId = ProviderId.APPLE,
//                authingWithThis = false,
//                onClick = { },
//                enabled = false,
//                isDarkAppTheme = isSystemInDarkTheme(),
//                isAuthDone = false
//            )
        }
    }
}

@Composable
@PreviewLightDark
private fun AuthCompletePreview(){
    SomewhereTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            AuthWithButton(
                providerId = ProviderId.GOOGLE,
                authingWithThis = true,
                onClick = { },
                enabled = false,
                isDarkAppTheme = isSystemInDarkTheme(),
                isAuthDone = true
            )

            MySpacerColumn(height = 16.dp)

//            AuthWithButton(
//                providerId = ProviderId.APPLE,
//                authingWithThis = false,
//                onClick = { },
//                enabled = false,
//                isDarkAppTheme = isSystemInDarkTheme(),
//                isAuthDone = true
//            )
        }
    }
}