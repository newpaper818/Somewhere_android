package com.newpaper.somewhere.feature.trip.tripAi.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun SelectButton(
    icon: MyIcon?,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    val selected = stringResource(id = R.string.selected)
    val unSelected = stringResource(id = R.string.not_selected)

    val buttonModifier = Modifier
        .widthIn(min = 260.dp)
        .clip(CircleShape)
        .background(
            if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else Color.Transparent
        )
        .border(
            width = 2.dp,
            color =
            if (!isSelected) MaterialTheme.colorScheme.outlineVariant
            else Color.Transparent,
            shape = CircleShape
        )
        .semantics {
            stateDescription = if (isSelected) selected
                                else unSelected
        }
        .clickable(
            onClickLabel = stringResource(id = R.string.select),
        ) { onClick() }

    Box(
        modifier = buttonModifier
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp, 16.dp)
        ) {
            if (icon != null) {
                DisplayIcon(icon = icon)

                MySpacerRow(width = 8.dp)
            }

            Text(text = text)
        }
    }
}
