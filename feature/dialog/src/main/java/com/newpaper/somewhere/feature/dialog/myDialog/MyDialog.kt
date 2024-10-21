package com.newpaper.somewhere.feature.dialog.myDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

internal val DIALOG_DEFAULT_WIDTH = 360.dp
internal val DIALOG_DEFAULT_MAX_HEIGHT = 570.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    onDismissRequest: () -> Unit,

    modifier: Modifier = Modifier,
    width: Dp? = DIALOG_DEFAULT_WIDTH,
    setMaxHeight: Boolean = false,
    maxHeight: Dp = DIALOG_DEFAULT_MAX_HEIGHT,
    titleText: String? = null,
    bodyText: String? = null,
    subBodyText: String? = null,
    bodyContent: @Composable() (() -> Unit)? = null,
    buttonContent: @Composable() (() -> Unit)? = null,
    setBodySpacer: Boolean = true,
    closeIcon: Boolean = false
){
    var columnModifier =
        if (width != null) modifier
            .width(width)
            .padding(16.dp)
        else               modifier.padding(16.dp)

    columnModifier =
        if (setMaxHeight) columnModifier.heightIn(max = maxHeight)
        else              columnModifier



    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = columnModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                //title text / align center
                if (titleText != null) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        if (closeIcon) {
                            Box(
                                modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                MyPlainTooltipBox(tooltipText = stringResource(id = TopAppBarIcon.close.descriptionTextId!!)) {
                                    IconButton(onClick = onDismissRequest) {
                                        DisplayIcon(icon = TopAppBarIcon.close)
                                    }
                                }
                            }
                        }
                    }

                    MySpacerColumn(height = 16.dp)
                }



                //body text / align left(fillMaxWidth)
                if (bodyText != null) {
                    Text(
                        text = bodyText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                    )
                    MySpacerColumn(height = 16.dp)
                }



                //sub body text / align center
                if (subBodyText != null){
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subBodyText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.outline
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    MySpacerColumn(height = 16.dp)
                }



                //body content
                if (bodyContent != null) {
                    Column(
                        modifier = if (setMaxHeight) Modifier.weight(1f)
                                    else Modifier
                    ) {
                        bodyContent()
                    }

                    if (setBodySpacer) {
                        MySpacerColumn(height = 12.dp)
                    }
                }



                //buttons
                if (buttonContent != null) {
                    buttonContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DialogButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
){
    val textStyle =
        if (enabled) MaterialTheme.typography.labelLarge.copy(color = textColor, fontWeight = FontWeight.Bold)
        else MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)

    val rippleConfiguration = RippleConfiguration(
        color = textColor
    )

    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfiguration) {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.widthIn(min = 120.dp),
        ) {
            Text(
                text = text,
                style = textStyle,
                modifier = Modifier.padding(10.dp, 0.dp)
            )
        }
    }
}
























@Composable
@PreviewLightDark
private fun MyDialogPreview(){
    SomewhereTheme {
        MyScaffold {
            MyDialog(
                titleText = "Dailog title",
                bodyText = "body text some text....... some textttttt somewhere trip planner",
                bodyContent = {
                    MyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {

                    }

                },
                buttonContent = {
                    Row {
                        //cancel button
                        DialogButton(
                            text = "Cancel",
                            textColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {}
                        )

                        MySpacerRow(width = 16.dp)

                        //delete button
                        DialogButton(
                            text = "OK",
                            onClick = {}
                        )
                    }
                },
                onDismissRequest = {}
            )
        }
    }
}