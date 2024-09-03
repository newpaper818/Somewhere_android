package com.newpaper.somewhere.feature.trip.inviteFriend.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.InviteFriendWithQrCodeButton
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.ui.InternetUnavailableText
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.trip.R
import kotlin.math.min

@Composable
internal fun EmailPage(
    internetEnabled: Boolean,
    searchFriendAvailable: Boolean,
    emailText: String,
    onEmailTextChange: (newEmailText: String) -> Unit,
    onSearch: () -> Unit,
    onClickInviteFriendWithQrCode: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        EmailTextField(
            internetEnabled = internetEnabled,
            searchFriendAvailable = searchFriendAvailable,
            emailText = emailText,
            onEmailTextChange = onEmailTextChange,
            onSearch = onSearch
        )

        MySpacerColumn(height = 20.dp)

        AnimatedVisibility(
            visible = !internetEnabled,
            enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
            exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500))
        ) {
            Column {
                InternetUnavailableText()

                MySpacerColumn(height = 24.dp)
            }
        }

        InviteFriendWithQrCodeButton(
            onClick = onClickInviteFriendWithQrCode
        )
    }
}

@Composable
private fun EmailTextField(
    internetEnabled: Boolean,
    searchFriendAvailable: Boolean,
    emailText: String,
    onEmailTextChange: (newEmailText: String) -> Unit,
    onSearch: () -> Unit
){
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.widthIn(max = itemMaxWidthSmall)
    ) {

        Text(
            text = stringResource(id = R.string.friend_to_invite),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier
                .widthIn(max = itemMaxWidthSmall)
                .padding(start = 16.dp)
        )

        MySpacerColumn(height = 8.dp)

        //text field
        MyCard(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(48.dp)
                    .padding(start = 16.dp)
            ) {
                MyTextField(
                    modifier = Modifier.weight(1f),
                    inputText = if (emailText == "") null else emailText,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    placeholderText = stringResource(id = R.string.friend_email),
                    placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    onValueChange = {
                        val newText = it.substring(0, min(200, it.length))
                        onEmailTextChange(newText)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (internetEnabled){
                            focusManager.clearFocus()
                            onSearch()
                        }
                    }),
                    textFieldModifier = Modifier.focusRequester(focusRequester)
                )

                //if texting show x icon
                if (emailText != "")
                    IconButton(
                        onClick = { onEmailTextChange("") }
                    ) {
                        DisplayIcon(icon = MyIcons.clearInputText)
                    }

                IconButton(
                    enabled = emailText != "" && internetEnabled && searchFriendAvailable,
                    onClick = {
                        focusManager.clearFocus()
                        onSearch()
                    }
                ) {
                    DisplayIcon(icon = MyIcons.searchFriend)
                }
            }
        }
    }
}