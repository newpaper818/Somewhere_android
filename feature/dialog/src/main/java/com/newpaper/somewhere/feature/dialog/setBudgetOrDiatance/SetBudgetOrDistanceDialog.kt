package com.newpaper.somewhere.feature.dialog.setBudgetOrDiatance

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SetBudgetOrDistanceDialog(
    initialValue: Float,
    onDismissRequest: () -> Unit,
    onSaveClick: (newBudget: Float) -> Unit,
    currencyType: CurrencyType? = null
){
    val titleText = if (currencyType == null) stringResource(id = R.string.set_travel_distance)
    else stringResource(id = R.string.set_budget)

    val newValueText: String by rememberSaveable {
        mutableStateOf(
            if (initialValue == 0.0f) ""
            else if (initialValue == initialValue.toInt().toFloat())
                initialValue.toInt().toString()
            else
                initialValue.toString().trimEnd('0').trimEnd('.')
        )
    }

    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(newValueText, TextRange(newValueText.length))
        )
    }

    var isInvalidText by rememberSaveable { mutableStateOf(false) }

    val borderColor = if (isInvalidText) CustomColor.outlineError
    else Color.Transparent


    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = titleText,
        bodyContent = {

            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            MyCard(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceDim),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    val numberOfDecimalPlaces = currencyType?.numberOfDecimalPlaces ?: 2
                    var placeholderText = "0"

                    if (numberOfDecimalPlaces > 0){
                        placeholderText = "0."
                        for (cnt in 1..numberOfDecimalPlaces){
                            placeholderText += "0"
                        }
                    }



                    if (currencyType != null) {
                        Text(
                            text = currencyType.symbol,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        MySpacerRow(width = 8.dp)
                    }

                    MyTextField(
                        inputText = textFieldValue.value,
                        inputTextStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                        placeholderText = placeholderText,
                        placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant).copy(
                            textAlign = TextAlign.End
                        ),
                        onValueChange = {
                            textFieldValue.value =
                                TextFieldValue(it.text, TextRange(it.text.length))
                            isInvalidText = !isValidFloatText(it.text, numberOfDecimalPlaces)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        modifier = Modifier.weight(1f),
                        textFieldModifier = Modifier.focusRequester(focusRequester)
                    )


                    if (currencyType == null) {
                        MySpacerRow(width = 8.dp)

                        Text(
                            text = stringResource(id = R.string.km),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    enabled = !isInvalidText,
                    onClick = {
                        if (!isInvalidText) {
                            val text = if (textFieldValue.value.text == "") "0"
                            else textFieldValue.value.text

                            onSaveClick(text.toFloat())
                        }
                    }
                )
            }
        }
    )
}

private fun isValidFloatText(
    numberText: String,
    numberOfDecimalPlaces: Int
): Boolean{

    val dotLimitCnt = if (numberOfDecimalPlaces == 0) 0
    else 1

    var dotCnt = 0

    for (char in numberText){
        if (char == '.')                 dotCnt++
        else if (char in '0'..'9')  continue
        else                             return false

        if (dotCnt > dotLimitCnt)
            return false
    }
    return true
}






















@Composable
@PreviewLightDark
private fun Preview_SetBudgetDialog(){
    SomewhereTheme {
        MyScaffold {
            SetBudgetOrDistanceDialog(
                initialValue = 0.0f,
                onDismissRequest = {},
                onSaveClick = {},
                currencyType = CurrencyType.USD
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_SetDistanceDialog(){
    SomewhereTheme {
        MyScaffold {
            SetBudgetOrDistanceDialog(
                initialValue = 0.0f,
                onDismissRequest = {},
                onSaveClick = {},
            )
        }
    }
}