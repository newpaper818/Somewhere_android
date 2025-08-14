package com.newpaper.somewhere.feature.dialog.setCurrencyType

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.CurrencyType
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.OkDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

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

        titleText = stringResource(id = R.string.set_currency_type),
        bodyContent = {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(35.dp))
                    .background(MaterialTheme.colorScheme.surfaceBright)
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
            DialogButtons(
                buttonLayout = ButtonLayout.HORIZONTAL,
                negativeButtonContent = {
                    //cancel button
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    //ok button
                    OkDialogButton(
                        onClick = { onOkClick(currentCurrencyType) },
                        modifier = it
                    )
                }
            )
        }
    )
}

@Composable
private fun OneCurrencyType(
    currencyType: CurrencyType,
    isSelected: Boolean,
    onClick: () -> Unit
){
    val cardColor = if (isSelected)  MaterialTheme.colorScheme.primaryContainer
                    else            MaterialTheme.colorScheme.surfaceBright

    val symbolTextStyle = if (isSelected)    MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                            else            MaterialTheme.typography.bodyLarge

    val textStyle = if (isSelected)  MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    else            MaterialTheme.typography.bodyMedium


    ClickableBox(
        shape = CircleShape,
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
                text = currencyType.name,
                style = textStyle,
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center
            )

            MySpacerRow(width = 4.dp)

            Text(
                text = stringResource(id = currencyType.currencyName),
                style = textStyle
            )
        }
    }

}














@Composable
@PreviewLightDark
private fun Preview_SetCurrencyTypeDialog(){
    SomewhereTheme {
        MyScaffold {
            SetCurrencyTypeDialog(
                initialCurrencyType = CurrencyType.KRW,
                onOkClick = {},
                onDismissRequest = {}
            )
        }
    }
}