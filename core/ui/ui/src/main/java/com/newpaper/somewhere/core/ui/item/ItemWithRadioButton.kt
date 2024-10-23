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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.listItemHeight

@Composable
fun ItemWithRadioButton(
    isSelected: Boolean,
    text: String,
    onItemClick: () -> Unit
){
    val select = stringResource(id = R.string.select)
    val selected = stringResource(id = R.string.selected)
    val notSelected = stringResource(id = R.string.not_selected)

    ClickableBox(
        onClick = { onItemClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(listItemHeight)
            .semantics {
                role = Role.RadioButton
                stateDescription = if (isSelected) selected else notSelected
                onClick(
                    label = select,
                    action = {
                        onItemClick()
                        true
                    }
                )
            }
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp, 0.dp, 16.dp, 0.dp)
        ) {
            MyRadioButton(
                selected = isSelected,
                onClick = onItemClick,
                modifier = Modifier.clearAndSetSemantics {  }
            )

            MySpacerRow(width = 2.dp)

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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RadioButton(
        modifier = modifier,
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