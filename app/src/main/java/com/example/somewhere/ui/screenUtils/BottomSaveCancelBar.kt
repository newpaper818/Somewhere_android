package com.example.somewhere.ui.screenUtils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.white

@Composable
fun BottomSaveCancelBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row() {
            MyButton(
                onClick = onCancelClick,
                text = "Cancel",
                buttonColor = getColor(ColorType.BUTTON__NEGATIVE)
            )

            Spacer(modifier = Modifier.width(16.dp))

            MyButton(
                onClick = onSaveClick,
                text = "Save",
                buttonColor = getColor(ColorType.BUTTON__POSITIVE)
            )
        }

        MySpacerColumn(height = 10.dp)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyButton(
    onClick: () -> Unit,
    text: String,
    buttonColor: Color
){
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(45.dp)
            .width(150.dp),
        onClick = onClick,
        backgroundColor = buttonColor,
        elevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = getTextStyle(TextType.BUTTON).copy(color = white)
            )
        }
    }
}