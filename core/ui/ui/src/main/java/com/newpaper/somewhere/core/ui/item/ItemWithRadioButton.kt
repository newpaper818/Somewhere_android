package com.newpaper.somewhere.core.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun ItemWithRadioButton(
    isSelected: Boolean,
    text: String,
    onItemClick: () -> Unit
){
    ClickableBox(
        onClick = { onItemClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp, 0.dp, 16.dp, 0.dp)
        ) {
            MyRadioButton(
                selected = isSelected,
                onClick = onItemClick
            )

            MySpacerRow(width = 8.dp)

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun MyRadioButton(
    selected: Boolean,
    onClick: () -> Unit
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        colors = RadioButtonDefaults.colors(
//            selectedColor = MaterialTheme.colorScheme.primary,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}



















@Composable
@PreviewLightDark
private fun Preview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(250.dp)
        ) {
            ListGroupCard {
                ItemWithRadioButton(
                    isSelected = true,
                    text = "selected item",
                    onItemClick = {}
                )

                ItemWithRadioButton(
                    isSelected = false,
                    text = "unselected item",
                    onItemClick = {}
                )
            }
        }
    }
}