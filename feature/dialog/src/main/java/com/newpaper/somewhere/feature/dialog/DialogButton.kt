package com.newpaper.somewhere.feature.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.CircularLoadingIndicatorSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
){
    val containerColor = if (enabled) buttonColor
                         else MaterialTheme.colorScheme.surfaceVariant

    val contentColor = textColor ?: MaterialTheme.colorScheme.contentColorFor(containerColor)

    val rippleConfiguration = RippleConfiguration(
        color = contentColor
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            modifier = modifier
                .widthIn(min = 140.dp)
                .heightIn(min = 46.dp)
            ,
        ) {
            Text(
                text = text,
                style = textStyle,
                modifier = Modifier.padding(10.dp, 0.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogButtonLoading(
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
){
    val contentColor = MaterialTheme.colorScheme.contentColorFor(buttonColor)

    val rippleConfiguration = RippleConfiguration(
        color = contentColor
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        Button(
            onClick = { },
            enabled = false,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = contentColor,
                disabledContainerColor = buttonColor,
                disabledContentColor = contentColor
            ),
            modifier = modifier
                .widthIn(min = 140.dp)
                .heightIn(min = 46.dp)
            ,
        ) {
            CircularLoadingIndicatorSmall(
                color = contentColor
            )
        }
    }
}










@Composable
internal fun CancelDialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    DialogButton(
        text = stringResource(id = R.string.button_cancel),
        onClick = onClick,
        modifier = modifier,
        buttonColor = MaterialTheme.colorScheme.surfaceTint,
        textColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
internal fun OkDialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    DialogButton(
        text = stringResource(id = R.string.button_ok),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
}

@Composable
internal fun PositiveDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    DialogButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    )
}

@Composable
internal fun NegativeDialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    DialogButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        buttonColor = MaterialTheme.colorScheme.error
    )
}











@PreviewLightDark
@Composable
private fun CancelDialogButton_Preview(

){
    MaterialTheme{
        Box(
            modifier = Modifier
                .width(150.dp).height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceBright),
            contentAlignment = Alignment.Center
        ) {
            CancelDialogButton(
                onClick = { }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PositiveDialogButton_Preview(

){
    MaterialTheme{
        Box(
            modifier = Modifier
                .width(150.dp).height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceBright),
            contentAlignment = Alignment.Center
        ) {
            PositiveDialogButton(
                text = "Positive",
                onClick = { }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PositiveDialogButton_Disabled_Preview(

){
    MaterialTheme{
        Box(
            modifier = Modifier
                .width(150.dp).height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceBright),
            contentAlignment = Alignment.Center
        ) {
            PositiveDialogButton(
                text = "Positive",
                onClick = { },
                enabled = false
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NegativeDialogButton_Preview(

){
    MaterialTheme{
        Box(
            modifier = Modifier
                .width(150.dp).height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceBright),
            contentAlignment = Alignment.Center
        ) {
            NegativeDialogButton(
                text = "Negative",
                onClick = { }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NegativeDialogButton_Disabled_Preview(

){
    MaterialTheme{
        Box(
            modifier = Modifier
                .width(150.dp).height(70.dp)
                .background(MaterialTheme.colorScheme.surfaceBright),
            contentAlignment = Alignment.Center
        ) {
            NegativeDialogButton(
                text = "Negative",
                onClick = { },
                enabled = false
            )
        }
    }
}