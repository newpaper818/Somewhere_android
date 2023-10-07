package com.example.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.enumUtils.CurrencyType
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun SetCurrencyTypeDialog(
    initialCurrencyType: CurrencyType,
    onOkClick: (tCurrencyType: CurrencyType) -> Unit,
    onDismissRequest: () -> Unit
){
    val currencyTypeList = enumValues<CurrencyType>()

    var currentCurrencyType by rememberSaveable { mutableStateOf(initialCurrencyType) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,

        titleText = stringResource(id = R.string.dialog_title_set_currency_type),
        bodyContent = {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
            ) {
                items(currencyTypeList) {
                    OneCurrencyType(
                        currencyType = it,
                        isSelected = it == currentCurrencyType,
                        onClick = {
                            currentCurrencyType = it
                        }
                    )
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
                    onClick = {
                        onOkClick(currentCurrencyType)
                    }
                )
            }
        }
    )
}

@Composable
private fun OneCurrencyType(
    currencyType: CurrencyType,
    isSelected: Boolean,
    onClick: () -> Unit,

    defaultCardColor: Color = getColor(ColorType.DIALOG__CARD),
    selectedCardColor: Color = getColor(ColorType.CARD_SELECTED),

    defaultTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL),
    selectedTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL).copy(color = getColor(ColorType.CARD_ON_SELECTED), fontWeight = FontWeight.Bold),

    defaultSymbolTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY),
    selectedSymbolTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY).copy(color = getColor(ColorType.CARD_ON_SELECTED), fontWeight = FontWeight.Bold),
){
    val cardColor = if (isSelected)  selectedCardColor
    else            defaultCardColor

    val symbolTextStyle = if (isSelected)    selectedSymbolTextStyle
    else            defaultSymbolTextStyle

    val textStyle = if (isSelected)  selectedTextStyle
    else            defaultTextStyle


    ClickableBox(
        shape = MaterialTheme.shapes.small,
        containerColor = cardColor,
        modifier = Modifier
            .height(54.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MySpacerRow(width = 10.dp)

            Text(
                text = currencyType.symbol,
                style = symbolTextStyle,
                modifier = Modifier.width(30.dp),
                textAlign = TextAlign.Center
            )

            MySpacerRow(width = 2.dp)

            Text(
                text = currencyType.code,
                style = textStyle,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center
            )

            MySpacerRow(width = 4.dp)

            Text(
                text = currencyType.currencyName,
                style = textStyle
            )
        }
    }

}