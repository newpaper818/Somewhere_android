package com.example.somewhere.ui.tripScreenUtils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.white

@Composable
fun AnimatedBottomSaveCancelBar(
    visible: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(400),
            initialOffsetY = { (it * 2).toInt() }),
        exit = slideOutVertically(
            animationSpec = tween(400),
            targetOffsetY = { (it * 2).toInt() })
    ) {
        SaveCancelButtons(
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick,
            modifier = modifier
        )
    }
}

@Composable
fun BottomSaveCancelBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SaveCancelButtonsRow(
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick
        )
    }
}
@Composable
private fun SaveCancelButtonsRow(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        MyButton(
            onClick = onCancelClick,
            text = stringResource(id = R.string.cancel),
            buttonColor = getColor(ColorType.BUTTON__NEGATIVE)
        )

        Spacer(modifier = Modifier.width(16.dp))

        MyButton(
            onClick = onSaveClick,
            text = stringResource(id = R.string.save)
        )
    }
}

@Composable
fun SaveCancelButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        SaveCancelButtonsRow(
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick
        )

        MySpacerColumn(height = 10.dp)
    }
}

@Composable
private fun MyButton(
    onClick: () -> Unit,
    text: String,
    buttonColor: Color? = null
){
    val color = if (buttonColor != null) ButtonDefaults.buttonColors(containerColor = buttonColor)
                else                    ButtonDefaults.buttonColors()

    val textStyle = if (buttonColor != null) getTextStyle(TextType.BUTTON).copy(color = white)
                    else                    getTextStyle(TextType.BUTTON).copy(color = MaterialTheme.colorScheme.onPrimary)

    Button(
        modifier = Modifier
            .height(45.dp)
            .width(150.dp),
        contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 2.dp),
        colors = color,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}