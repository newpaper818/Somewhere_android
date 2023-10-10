package com.example.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun MemoDialog(
    memoText: String,
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    onDismissRequest: () -> Unit,
) {

    MyDialog(
        onDismissRequest = onDismissRequest,
//        setMaxHeight = true,
        width = 600.dp,
//        maxHeight = 900.dp,
        setBodySpacer = false,
        closeIcon = true,

        titleText = stringResource(id = R.string.dialog_title_memo),
        bodyContent = {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp)
//                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(getColor(ColorType.DIALOG__CARD))

            ){
                item {
                    Text(
                        text = memoText,
                        style = bodyTextStyle
                    )
                }
            }
        },
//        buttonContent = {
//            Row {
//                //cancel button
//                DialogButton(
//                    text = stringResource(id = R.string.dialog_button_close),
//                    onClick = onDismissRequest
//                )
//            }
//        }
    )
}