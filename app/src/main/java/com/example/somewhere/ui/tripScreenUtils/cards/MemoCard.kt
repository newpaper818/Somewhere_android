package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.tripScreenUtils.MyTextField
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun MemoCard(
    isEditMode: Boolean,
    memoText: String?,
    onMemoChanged: (String) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){

    //memo card
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp, 14.dp)
        ) {
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(tween(400)),
                exit = shrinkVertically(tween(400))
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.memo_card_title),
                        style = titleTextStyle
                    )

                    MySpacerColumn(height = 8.dp)
                }
            }

            //view mode
            if(!isEditMode){
                Text(
                    text = memoText ?: stringResource(id = R.string.memo_card_body_no_memo),
                    style = if (memoText != null)   bodyTextStyle
                            else                    bodyNullTextStyle
                )
            }

            //edit mode
            else{
                val focusManager = LocalFocusManager.current

                //TODO focus 되면 배경색 달라지게?
                //TODO text num limit
                MyTextField(
                    inputText = memoText,
                    inputTextStyle = bodyTextStyle,

                    placeholderText = stringResource(id = R.string.memo_card_body_add_a_memo),
                    placeholderTextStyle = bodyNullTextStyle,

                    onValueChange = onMemoChanged,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(autoCorrect = true),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                )
            }
        }
    }
}