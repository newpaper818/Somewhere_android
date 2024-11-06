package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun SubscribeButton(
    formattedPrice: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isUsingSomewherePro: Boolean
){
    val buttonModifier = Modifier.width(260.dp).height(70.dp)

    if (isUsingSomewherePro){
        Box(
            modifier = buttonModifier
                .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = stringResource(id = R.string.you_are_subscribed_to_somewhere_pro),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 16.sp * 1.3
            )
        }
    }
    else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = stringResource(R.string.upgrade_to_somewhere_pro, formattedPrice),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp * 1.3
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RestorePurchasesButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    val grayRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    CompositionLocalProvider(LocalRippleConfiguration provides grayRippleConfiguration) {
        TextButton(
            onClick = onClick,
            enabled = enabled
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

@Composable
internal fun ManageSubscriptionButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = R.string.manage_subscription),
            style = MaterialTheme.typography.labelMedium

        )
    }
}