package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun SubscribeButton(
    formattedPrice: String,
    onClick: () -> Unit,
    enabled: Boolean
){
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.width(260.dp),
        shape = MaterialTheme.shapes.large
    ) {
        val buttonText =
            if (enabled) stringResource(id = R.string.upgrade_to_somewhere_pro, formattedPrice)
            else stringResource(id = R.string.you_are_already_using_somewhere_pro)

        Text(
            text = buttonText,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RestorePurchasesButton(
    onClick: () -> Unit
) {
    val grayRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    CompositionLocalProvider(LocalRippleConfiguration provides grayRippleConfiguration) {
        TextButton(
            onClick = onClick
        ) {
            Text(
                text = stringResource(id = R.string.restore_purchases),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CancelSubscriptionButton(
    onClick: () -> Unit
) {
    val grayRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    CompositionLocalProvider(LocalRippleConfiguration provides grayRippleConfiguration) {
        TextButton(
            onClick = onClick
        ) {
            Text(
                text = stringResource(id = R.string.cancle_subcription),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}