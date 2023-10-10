package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.tripScreenUtils.MyTextField
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.tripScreenUtils.dialogs.MemoDialog

const val MAX_MEMO_LENGTH = 5000
const val MAX_VIEW_MEMO_LENGTH = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoCard(
    isEditMode: Boolean,
    memoText: String?,
    onMemoChanged: (String) -> Unit,
    onTextSizeLimit: () -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    val borderColor = if (isTextSizeLimit) getColor(ColorType.ERROR_BORDER)
                        else Color.Transparent

    var viewMemoText = memoText ?: stringResource(id = R.string.memo_card_body_no_memo)

    val isMemoLong = viewMemoText.length > MAX_VIEW_MEMO_LENGTH

    if (isMemoLong){
        viewMemoText = viewMemoText.substring(0, MAX_VIEW_MEMO_LENGTH)
        viewMemoText += "..."
    }


    var showMemoDialog by rememberSaveable { mutableStateOf(false) }

    if (showMemoDialog){
        MemoDialog(
            memoText = memoText ?: "",
            onDismissRequest = {
                showMemoDialog = false
            }
        )
    }

    //memo card
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        enabled = isEditMode || memoText != null,
        onClick = {
            //show dialog
            if (!isEditMode && memoText != null)
                showMemoDialog = true
        }
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
                    text = viewMemoText,
                    style = if (memoText != null)   bodyTextStyle
                            else                    bodyNullTextStyle,
                    overflow = TextOverflow.Ellipsis
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

                    onValueChange = {
                        var text = it

                        if(it.length > MAX_MEMO_LENGTH) {
                            isTextSizeLimit = true
                            onTextSizeLimit()
                            text = text.substring(0, MAX_MEMO_LENGTH)
                        }
                        else
                            isTextSizeLimit = false

                        onMemoChanged(text)
                    },
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