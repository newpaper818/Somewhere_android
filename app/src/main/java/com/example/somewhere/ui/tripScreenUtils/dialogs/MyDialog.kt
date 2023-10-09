package com.example.somewhere.ui.tripScreenUtils.dialogs

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

val DIALOG_DEFAULT_WIDTH = 360.dp
val DIALOG_DEFAULT_MAX_HEIGHT = 570.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog1(
    onDismissRequest: () -> Unit,

    modifier: Modifier = Modifier,
    width: Dp? = DIALOG_DEFAULT_WIDTH,
    setMaxHeight: Boolean = false,
    maxHeight: Dp = DIALOG_DEFAULT_MAX_HEIGHT,
    titleText: String? = null,
    bodyText: String? = null,
    bodyContent: @Composable() (() -> Unit)? = null,
    buttonContent: @Composable() (() -> Unit)? = null,
    setBodySpacer: Boolean = true
){
    var columnModifier =
        if (width != null) modifier
            .width(width)
            .padding(16.dp)
        else               modifier.padding(16.dp)

    columnModifier =
        if (setMaxHeight) columnModifier.heightIn(max = maxHeight)
        else              columnModifier



    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.wrapContentHeight(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = getColor(ColorType.DIALOG__BACKGROUND)
        ) {
            Column(
                modifier = columnModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                //title text / align center
                if (titleText != null){
                    Text(
                        text = titleText,
                        style = getTextStyle(TextType.DIALOG__TITLE)
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body text / align left(fillMaxWidth)
                if (bodyText != null){
                    Text(
                        text = bodyText,
                        style = getTextStyle(TextType.DIALOG__BODY),
                        modifier = Modifier
                            .padding(3.dp, 0.dp)
                            .fillMaxWidth()
                    )

                    MySpacerColumn(height = 16.dp)
                }


                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column {
                        if (bodyContent != null) {
                            bodyContent()
                        }

//                        Column(modifier = Modifier.height(60.dp)) {
//                            Spacer(modifier = Modifier.weight(1f))
//                        }
                        
//                        if (setBodySpacer) {
//                            MySpacerColumn(height = 12.dp)
//                        }
//
//                        if (buttonContent != null){
//                            MySpacerColumn(height = 48.dp)
//                        }
                    }
                    Column {
                        if (buttonContent != null) {
                            buttonContent()
                        }
                    }
                }
            }
        }
    }
}

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
    bodyContent: @Composable() (() -> Unit)? = null,
    buttonContent: @Composable() (() -> Unit)? = null,
    setBodySpacer: Boolean = true
){
    var columnModifier =
        if (width != null) modifier
            .width(width)
            .padding(16.dp)
        else               modifier.padding(16.dp)

    columnModifier =
        if (setMaxHeight) columnModifier.heightIn(max = maxHeight)
        else              columnModifier



    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.wrapContentHeight(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = getColor(ColorType.DIALOG__BACKGROUND)
        ) {
            Column(
                modifier = columnModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                //title text / align center
                if (titleText != null){
                    Text(
                        text = titleText,
                        style = getTextStyle(TextType.DIALOG__TITLE)
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body text / align left(fillMaxWidth)
                if (bodyText != null){
                    Text(
                        text = bodyText,
                        style = getTextStyle(TextType.DIALOG__BODY),
                        modifier = Modifier
                            .padding(3.dp, 0.dp)
                            .fillMaxWidth()
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body
                if (bodyContent != null) {
                    Column(
                        modifier = if (setMaxHeight) Modifier.weight(1f) else Modifier
                    ) {
                        bodyContent()
                    }

                    if(setBodySpacer) {
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




private object ErrorRippleTheme: RippleTheme {
    @Composable
    override fun defaultColor() = MaterialTheme.colorScheme.errorContainer

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        MaterialTheme.colorScheme.errorContainer, true
    )
}

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    textColor: Color? = null,
    errorRipple: Boolean = false,
    enabled: Boolean = true
){
    val textStyle = if (textColor != null)  getTextStyle(TextType.DIALOG__BUTTON).copy(color = textColor)
                    else                    getTextStyle(TextType.DIALOG__BUTTON)

    if (errorRipple) {
        CompositionLocalProvider(LocalRippleTheme provides ErrorRippleTheme) {
            TextButton(
                onClick = onClick,
                enabled = enabled,
                modifier = modifier.width(120.dp),
            ) {
                Text(
                    text = text,
                    style = textStyle
                )
            }
        }
    }
    else {
        TextButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.width(120.dp),
        ) {
            Text(
                text = text,
                style = textStyle
            )
        }
    }
}

