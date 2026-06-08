package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R

@Composable
fun FilterChipButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,

    selectedButtonColor: Color = MaterialTheme.colorScheme.inverseSurface, //black - at light mode
    notSelectedButtonColor: Color = Color.Transparent, //white

    selectedTextColor: Color = MaterialTheme.colorScheme.inverseOnSurface, // white
    notSelectedTextColor: Color = MaterialTheme.colorScheme.onSurface, // black
){
    val buttonColor =
        if (selected)    selectedButtonColor
        else             notSelectedButtonColor

    val textStyle =
        if (selected) MaterialTheme.typography.labelMedium.copy(color = selectedTextColor, fontWeight = FontWeight.SemiBold)
        else    MaterialTheme.typography.labelMedium.copy(color = notSelectedTextColor, fontWeight = FontWeight.SemiBold)

    val selectedText = stringResource(id = R.string.selected)
    val notSelectedText = stringResource(id = R.string.not_selected)
    val toggle = stringResource(id = R.string.toggle)


    val buttonModifier = Modifier.semantics {
        stateDescription = if (selected) selectedText else notSelectedText
        onClick(
            label = toggle,
            action = {
                onClick()
                true
            }
        )
    }

    ClickableBox(
        modifier = if (!selected) buttonModifier.border(0.8.dp, MaterialTheme.colorScheme.surfaceVariant, SmoothRoundedCornerShape(999.dp, 1f))
                    else buttonModifier,
        onClick = onClick,
        containerColor = buttonColor,
        shape = SmoothRoundedCornerShape(999.dp, 1f),

    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(14.dp, 8.dp)
        )
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