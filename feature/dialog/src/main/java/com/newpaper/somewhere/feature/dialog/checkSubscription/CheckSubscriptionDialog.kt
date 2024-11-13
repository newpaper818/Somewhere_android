package com.newpaper.somewhere.feature.dialog.checkSubscription

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.ManageSubscriptionButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.PLAY_STORE_SUBSCRIPTIONS_URL
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.PositiveDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun CheckSubscriptionDialog(
    onDismissRequest: () -> Unit,
    onClickPositive: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.check_subscription),
        bodyText = stringResource(id = R.string.unsubscribe_before_deleting_account),
        bodyContent = {
            Text(
                text = stringResource(id = R.string.if_you_are_canceling_the_subscription),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
            )
        },
        buttonContent = {
            ManageSubscriptionButton(
                onClick = {
                    uriHandler.openUri(PLAY_STORE_SUBSCRIPTIONS_URL)
                },
                enabled = true,
                useTextButton = true
            )

            MySpacerColumn(height = 10.dp)

            DialogButtons(
                negativeButtonContent = {
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    PositiveDialogButton(
                        text = stringResource(id = R.string.dialog_continue),
                        onClick = onClickPositive,
                        modifier = it
                    )
                },
                buttonLayout = ButtonLayout.VERTICAL
            )
        }
    )
}