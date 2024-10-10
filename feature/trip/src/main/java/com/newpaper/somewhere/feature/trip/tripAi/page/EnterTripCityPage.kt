package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun EnterTripCityPage(
    searchText: String,
    onTextChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onKeyboardActionClicked: () -> Unit
){
    TripAiPage(
        title = stringResource(id = R.string.where_are_you_going),
        subTitle = stringResource(id = R.string.type_a_destination),
        content = {
            //search box
            SearchBox(
                searchText = searchText,
                onTextChanged = onTextChanged,
                onClearClicked = onClearClicked,
                onKeyboardActionClicked = onKeyboardActionClicked
            )
        }
    )
}

@Composable
private fun SearchBox(
    searchText: String,
    onTextChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onKeyboardActionClicked: () -> Unit
){
    val boxModifier = Modifier
        .height(50.dp)
        .widthIn(max = 450.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceBright)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = boxModifier
    ) {
        MySpacerRow(width = 20.dp)

        MyTextField(
            inputText = if (searchText == "") null else searchText,
            inputTextStyle = MaterialTheme.typography.bodyLarge,
            placeholderText = stringResource(id = R.string.type_city_or_country_name),
            placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            onValueChange = onTextChanged,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onKeyboardActionClicked()
            }),
            modifier = Modifier.weight(1f),
            textSizeLimit = 50
        )

        //if texting show x icon
        if (searchText != "") {
            IconButton(
                onClick = { onClearClicked() }
            ) {
                DisplayIcon(icon = MyIcons.clearInputText)
            }
        }
    }
}