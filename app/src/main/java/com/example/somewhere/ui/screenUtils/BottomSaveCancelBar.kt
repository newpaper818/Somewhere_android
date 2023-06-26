package com.example.somewhere.ui.screenUtils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.format.TextStyle

@Composable
fun BottomSaveCancelBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colors.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        MyButton(
            onClick = onCancelClick,
            text = "Cancel",
            buttonColor = MaterialTheme.colors.surface
        )
        
        Spacer(modifier = Modifier.width(20.dp))

        MyButton(
            onClick = onSaveClick,
            text = "Save",
            buttonColor = MaterialTheme.colors.secondaryVariant
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyButton(
    onClick: () -> Unit,
    text: String,
    //textStyle: TextStyle,
    buttonColor: Color
){
    Card(
        modifier = Modifier
            .height(50.dp)
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp)),
        onClick = onClick,
        backgroundColor = buttonColor,
        elevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.h4
            )
        }
    }
}