package com.example.somewhere.ui.screenUtils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyTextField(
    inputText: String?,
    inputTextStyle: TextStyle,

    placeholderText: String,
    placeholderTextStyle: TextStyle,

    onValueChange: (String) -> Unit,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
){
    Box(modifier = modifier) {
        //text field
        BasicTextField(
            value = inputText ?: "" ,
            textStyle = inputTextStyle.copy(MaterialTheme.colors.onSurface),
            onValueChange = onValueChange,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(MaterialTheme.colors.primaryVariant)
        )

        //place holder text
        if (inputText == null){
            Text(
                text = placeholderText,
                style = placeholderTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
fun MyTextField(
    inputText: TextFieldValue,
    inputTextStyle: TextStyle,

    placeholderText: String,
    placeholderTextStyle: TextStyle,

    onValueChange: (TextFieldValue) -> Unit,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier
){
    Box(modifier = modifier) {
        //text field
        BasicTextField(
            value = inputText,
            textStyle = inputTextStyle.copy(MaterialTheme.colors.onSurface),
            onValueChange = onValueChange,
            singleLine = singleLine,
            modifier = textFieldModifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(MaterialTheme.colors.primaryVariant),
        )

        //place holder text
        if (inputText.text.isEmpty()){
            Text(
                text = placeholderText,
                style = placeholderTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}