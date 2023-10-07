package com.example.somewhere.ui.tripScreenUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun NewItemButton(
    text: String,
    onClick: () -> Unit
){
    IconTextButton(
        icon = MyIcons.add,
        text = text,
        onClick = onClick
    )
}

@Composable
fun DeleteItemButton(
    text: String,
    onClick: () -> Unit
){
    IconTextButton(
        icon = MyIcons.deleteButton,
        text = text,
        onClick = onClick,
        iconColor = MaterialTheme.colorScheme.onErrorContainer,
        buttonColor = MaterialTheme.colorScheme.errorContainer,
        textStyle = getTextStyle(TextType.BUTTON).copy(color = MaterialTheme.colorScheme.onErrorContainer)
    )
}

@Composable
private fun IconTextButton(
    icon: MyIcon,
    text: String,
    onClick: () -> Unit,
    iconColor: Color? = null,
    buttonColor: Color = getColor(ColorType.BUTTON),
    textStyle: TextStyle = getTextStyle(TextType.BUTTON)
){
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        contentPadding = PaddingValues(16.dp, 0.dp, 24.dp, 0.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DisplayIcon(
                icon = icon,
                onColor = iconColor == null,
                color = iconColor
            )

            MySpacerRow(8.dp)

            Column {
                Text(
                    text = text,
                    style = textStyle
                )
                MySpacerColumn(2.dp)
            }
        }
    }
}