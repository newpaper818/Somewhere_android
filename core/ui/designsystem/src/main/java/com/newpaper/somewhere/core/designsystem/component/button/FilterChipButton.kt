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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R

@Composable
fun FilterChipButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,

    selectedButtonColor: Color = MaterialTheme.colorScheme.inverseSurface, //black
    notSelectedButtonColor: Color = Color.Transparent, //white

    selectedTextColor: Color = MaterialTheme.colorScheme.inverseOnSurface, // white
    notSelectedTextColor: Color = MaterialTheme.colorScheme.onSurface, // black
){
    val buttonColor =
        if (selected)    selectedButtonColor
        else             notSelectedButtonColor

    val border =
        if (selected)   null
        else            BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)

    val textStyle =
        if (selected) MaterialTheme.typography.labelLarge.copy(color = selectedTextColor)
        else    MaterialTheme.typography.labelLarge.copy(color = notSelectedTextColor)

    val selectedText = stringResource(id = R.string.selected)
    val notSelectedText = stringResource(id = R.string.not_selected)
    val toggle = stringResource(id = R.string.toggle)


    Button(
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        border = border,
        contentPadding = PaddingValues(12.dp, 0.dp, 12.dp, 1.dp),
        onClick = onClick,
        modifier = Modifier.semantics {
            stateDescription = if (selected) selectedText else notSelectedText
            onClick(
                label = toggle,
                action = {
                    onClick()
                    true
                }
            )
        }
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
private fun FilterChipButtonPreview(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(300.dp)
        ) {
            Row(

            ) {
                FilterChipButton(
                    text = "All",
                    selected = true,
                    onClick = { }
                )
                MySpacerRow(16.dp)

                FilterChipButton(
                    text = "Current",
                    selected = false,
                    onClick = { }
                )
                MySpacerRow(16.dp)

                FilterChipButton(
                    text = "Previous",
                    selected = false,
                    onClick = { }
                )
            }

        }
    }
}

@Composable
@PreviewLightDark
private fun FilterChipButtonPreview2(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(300.dp)
        ) {
            Row(

            ) {
                FilterChipButton(
                    text = "Tour",
                    selected = true,
                    selectedButtonColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = { }
                )
                MySpacerRow(16.dp)

                FilterChipButton(
                    text = "Food",
                    selected = true,
                    selectedButtonColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = { }
                )
                MySpacerRow(16.dp)

                FilterChipButton(
                    text = "Move",
                    selected = false,
                    notSelectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { }
                )
            }

        }
    }
}