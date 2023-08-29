package com.example.somewhere.ui.screenUtils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.white

@Composable
fun SomewhereFloatingButton(
    text: String,
    icon: MyIcon,
    onButtonClicked: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = { Text(text = text, style = getTextStyle(TextType.FLOATING_BUTTON)) },
        icon = { DisplayIcon(icon, color = white) },
        onClick = onButtonClicked,
        backgroundColor = getColor(ColorType.BUTTON)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewItemButton(
    text: String,
    onClick: () -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.BUTTON)
){
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .height(36.dp),
        onClick = onClick,
        backgroundColor = getColor(ColorType.BUTTON),
        elevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MySpacerRow(12.dp)

            //icon
            DisplayIcon(icon = MyIcons.add)

            MySpacerRow(4.dp)

            //text
            Column {
                Text(
                    text = text,
                    style = textStyle
                )
                MySpacerColumn(2.dp)
            }

            MySpacerRow(18.dp)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteItemButton(
    text: String,
    onClick: () -> Unit,
    textStyle: TextStyle = getTextStyle(TextType.BUTTON)
){
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .height(36.dp),
        onClick = onClick,
        backgroundColor = getColor(ColorType.BUTTON__DELETE),
        elevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MySpacerRow(12.dp)

            DisplayIcon(icon = MyIcons.delete, onColor = true)

            MySpacerRow(4.dp)

            Column {
                Text(
                    text = text,
                    style = textStyle
                )
                MySpacerColumn(2.dp)
            }

            MySpacerRow(18.dp)
        }
    }
}