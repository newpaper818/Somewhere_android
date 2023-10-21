package com.newpaper.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle

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

        titleText = stringResource(id = R.string.memo_dialog_title),
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
                    SelectionContainer {
                        Text(
                            text = memoText,
                            style = bodyTextStyle
                        )
                    }
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