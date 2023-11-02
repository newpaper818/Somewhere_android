package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.ui.theme.white

@Composable
fun AnimatedBottomSaveCancelBar(
    visible: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true
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
            modifier = modifier,
            saveEnabled = saveEnabled
        )
    }
}

@Composable
fun BottomSaveCancelBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    positiveText: String = stringResource(id = R.string.save)
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
            onSaveClick = onSaveClick,
            positiveText = positiveText
        )
    }
}

@Composable
fun SaveCancelButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true,
    positiveText: String = stringResource(id = R.string.save)
) {
    Column(
        modifier = modifier
    ) {
        SaveCancelButtonsRow(
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick,
            positiveText = positiveText,
            saveEnabled = saveEnabled
        )

        MySpacerColumn(height = 10.dp)
    }
}

@Composable
private fun SaveCancelButtonsRow(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,

    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true,
    positiveText: String  = stringResource(id = R.string.save),
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
            text = positiveText,
            enabled = saveEnabled
        )
    }
}

@Composable
private fun MyButton(
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    buttonColor: Color? = null
){
    val color = if (buttonColor != null) ButtonDefaults.buttonColors(containerColor = buttonColor)
                else                    ButtonDefaults.buttonColors()

    val textStyle = if (buttonColor != null) getTextStyle(TextType.BUTTON).copy(color = white)
                    else if (enabled)        getTextStyle(TextType.BUTTON).copy(color = MaterialTheme.colorScheme.onPrimary)
                    else                     getTextStyle(TextType.BUTTON).copy(color = MaterialTheme.colorScheme.surfaceVariant)

    Button(
        modifier = Modifier
            .height(45.dp)
            .width(150.dp),
        contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 2.dp),
        colors = color,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}