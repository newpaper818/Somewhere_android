package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.R
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun AnimatedBottomSaveCancelButtons(
    visible: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true,
    use2PanesAndSpotScreen: Boolean = false
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
        if (!use2PanesAndSpotScreen) {
            SaveCancelButtons(
                onCancelClick = onCancelClick,
                onSaveClick = onSaveClick,
                modifier = modifier,
                saveEnabled = saveEnabled
            )
        }
        else{
            Row {
                Spacer(modifier = Modifier.weight(1f))

                MySpacerRow(width = 316.dp)

                Spacer(modifier = Modifier.weight(2f))

                SaveCancelButtons(
                    onCancelClick = onCancelClick,
                    onSaveClick = onSaveClick,
                    modifier = modifier,
                    saveEnabled = saveEnabled
                )
                Spacer(modifier = Modifier.weight(1f))
            }

        }
    }
}

@Composable
fun SaveCancelButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true
) {
    Column(
        modifier = modifier
    ) {
        Row {
            BigCancelButton(onClick = onCancelClick)

            Spacer(modifier = Modifier.width(16.dp))

            BigSaveButton(
                onClick = onSaveClick,
                enabled = saveEnabled
            )
        }

        MySpacerColumn(height = 10.dp)
    }
}

@Composable
private fun BigCancelButton(
    onClick: () -> Unit,
    enabled: Boolean = true
){
    Button(
        modifier = Modifier
            .height(45.dp)
            .width(150.dp)
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.large
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.large,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = R.string.cancel),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun BigSaveButton(
    onClick: () -> Unit,
    enabled: Boolean = true
){
    val shadowElevation = if (enabled) 4.dp
                            else 0.dp

    Button(
        modifier = Modifier
            .height(45.dp)
            .width(150.dp)
            .shadow(
                elevation = shadowElevation,
                shape = MaterialTheme.shapes.large
            ),
        shape = MaterialTheme.shapes.large,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = stringResource(id = R.string.save),
            style = MaterialTheme.typography.labelLarge
        )
    }
}





















@Composable
@PreviewLightDark
private fun SaveCancelButtonPreview(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ){
            SaveCancelButtons(
                onCancelClick = {},
                onSaveClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SaveCancelButtonPreview1(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ){
            SaveCancelButtons(
                onCancelClick = {},
                onSaveClick = {},
                saveEnabled = false
            )
        }
    }
}