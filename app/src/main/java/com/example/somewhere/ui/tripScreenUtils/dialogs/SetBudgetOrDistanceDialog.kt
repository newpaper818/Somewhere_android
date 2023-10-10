package com.example.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.MyTextField

@Composable
fun SetBudgetOrDistanceDialog(
    initialValue: Float,
    onDismissRequest: () -> Unit,
    onSaveClick: (newBudget: Float) -> Unit,
    currencySymbol: String? = null
){
    val titleText = if (currencySymbol == null) stringResource(id = R.string.dialog_title_set_travel_distance)
                    else stringResource(id = R.string.dialog_title_set_budget)

    val newValueText: String by rememberSaveable {
        mutableStateOf(
            if (initialValue == 0.0f) ""
            else initialValue.toString()
        )
    }

    val textFieldValue = remember {
        mutableStateOf(
            TextFieldValue(newValueText, TextRange(newValueText.length))
        )
    }

    var isInvalidText by rememberSaveable { mutableStateOf(false) }

    val borderColor = if (isInvalidText) getColor(ColorType.ERROR_BORDER)
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

            Card(
                colors = CardDefaults.cardColors(getColor(ColorType.DIALOG__CARD)),
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

                    if (currencySymbol != null) {
                        Text(
                            text = currencySymbol,
                            style = getTextStyle(TextType.DIALOG__BODY)
                        )

                        MySpacerRow(width = 8.dp)
                    }

                    MyTextField(
                        inputText = textFieldValue.value,
                        inputTextStyle = getTextStyle(TextType.DIALOG__BODY).copy(textAlign = TextAlign.End),
                        placeholderText = "0.00",
                        placeholderTextStyle = getTextStyle(TextType.DIALOG__BODY_NULL).copy(
                            textAlign = TextAlign.End
                        ),
                        onValueChange = {
                            textFieldValue.value =
                                TextFieldValue(it.text, TextRange(it.text.length))
                            isInvalidText = !isValidFloatText(it.text)
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


                    if (currencySymbol == null) {
                        MySpacerRow(width = 8.dp)

                        Text(
                            text = "km",
                            style = getTextStyle(TextType.DIALOG__BODY)
                        )
                    }
                }
            }

        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
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
    budgetText: String
): Boolean{
    var dotCnt = 0

    for (char in budgetText){
        if (char == '.')                 dotCnt++
        else if (char in '0'..'9')  continue
        else                             return false

        if (dotCnt >= 2)
            return false
    }
    return true
}