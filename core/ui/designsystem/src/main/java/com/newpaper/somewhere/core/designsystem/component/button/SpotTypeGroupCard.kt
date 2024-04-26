package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun SpotTypeGroupCard(
    text: String,
    color: Int,
    onColor: Int,

    selected: Boolean,

    onClick: () -> Unit,
){
    val buttonColor =
        if (selected)    Color(color)
        else             Color.Transparent

    val border =
        if (selected)   null
        else            BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)

    val textStyle =
        if (selected) MaterialTheme.typography.labelLarge.copy(color = Color(onColor))
        else    MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    Button(
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        border = border,
        contentPadding = PaddingValues(12.dp, 0.dp, 12.dp, 1.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = textStyle
            )
        }
    }
}


























@Composable
@PreviewLightDark
private fun SpotTypeGroupCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(100.dp)
        ) {
            SpotTypeGroupCard(
                text = "5 Tour",
                color = 0xff493cfa.toInt(),
                onColor = 0xffffffff.toInt(),
                selected = true,
                onClick = { }
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SpotTypeGroupCard1Preview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(100.dp)
        ) {
            SpotTypeGroupCard(
                text = "Food",
                color = 0xFFedb25f.toInt(),
                onColor = 0xff000000.toInt(),
                selected = true,
                onClick = { }
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SpotTypeGroupCardOutlinePreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(100.dp)
        ) {
            SpotTypeGroupCard(
                text = "5 Tour",
                color = 0xff493cfa.toInt(),
                onColor = 0xffffffff.toInt(),
                selected = false,
                onClick = { }
            )
        }
    }
}