package com.newpaper.somewhere.ui.settingScreenUtils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.gray

/*
      ---------------------------------------------------------------
    /                                                                 \
    |   (o)  text                                                     |
    \                                                                 /
      ---------------------------------------------------------------
 */


@Composable
fun ItemWithRadioButton(
    isSelected: Boolean,
    text: String,
    onItemClick: () -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY1)
){
    ClickableBox(
        onClick = { onItemClick() },
        modifier = Modifier.fillMaxWidth().height(60.dp)
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
                style = textStyle
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
            unselectedColor = gray//MaterialTheme.colorScheme.onSurfaceVariant,
//            disabledSelectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
//            disabledUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}