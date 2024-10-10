package com.newpaper.somewhere.feature.trip.tripAi.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun SelectButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    val buttonModifier = Modifier
        .width(220.dp)
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
        .clickable { onClick() }

    Box(
        modifier = buttonModifier
    ){
        Text(
            text = text,
            modifier = Modifier.padding(20.dp, 12.dp)
        )
    }
}
