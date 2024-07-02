package com.newpaper.somewhere.core.ui.card.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.HyperlinkedText
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.ui.R

private const val MAX_MEMO_LENGTH = 3000

@Composable
fun MemoCard(
    isEditMode: Boolean,
    memoText: String?,
    onMemoChanged: (String) -> Unit,
    isLongText: (Boolean) -> Unit,
    showMemoDialog: () -> Unit,

    modifier: Modifier = Modifier
){
    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isEditMode){
        isTextSizeLimit = (memoText ?: "").length > MAX_MEMO_LENGTH
    }

    val borderColor = if (isTextSizeLimit) CustomColor.outlineError
                        else Color.Transparent

    val viewMemoText = memoText ?: stringResource(id = R.string.memo_card_body_no_memo)

    //memo card
    MyCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        enabled = !isEditMode && memoText != null,
        onClick =
            if (!isEditMode && memoText != null) {
                //show dialog
                { showMemoDialog() }
            }
            else {
                null
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
                    Row {
                        Text(
                            text = stringResource(id = R.string.memo_card_title),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        //up to 100 characters
                        if (isTextSizeLimit){
                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = stringResource(id = R.string.long_text, MAX_MEMO_LENGTH),
                                style = MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError)
                            )
                        }
                    }


                    MySpacerColumn(height = 8.dp)
                }
            }

            //view mode
            if(!isEditMode){
                SelectionContainer {
                    HyperlinkedText(
                        text = viewMemoText,
                        defaultTextStyle = if (memoText != null) MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                                            else MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        onClickText = {
                            //show dialog
                            if (memoText != null)
                                showMemoDialog()
                        }
                    )
                }
            }

            //edit mode
            else{
                val focusManager = LocalFocusManager.current

                //TODO focus 되면 배경색 달라지게?
                //TODO text num limit
                MyTextField(
                    inputText = memoText,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,

                    placeholderText = stringResource(id = R.string.memo_card_body_add_a_memo),
                    placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),

                    onValueChange = {
                        if (it.length > MAX_MEMO_LENGTH && !isTextSizeLimit){
                            isTextSizeLimit = true
                            isLongText(true)
                        }
                        else if (it.length <= MAX_MEMO_LENGTH && isTextSizeLimit) {
                            isTextSizeLimit = false
                            isLongText(false)
                        }
                        onMemoChanged(it)
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






















@Composable
@PreviewLightDark
private fun Preview_MemoCard(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            MemoCard(
                isEditMode = false,
                memoText = "this is memo text",
                onMemoChanged = {},
                isLongText = {},
                showMemoDialog = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_MemoCard_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            MemoCard(
                isEditMode = true,
                memoText = "this is memo text - edit mode\nhahahahahhahaahahahahahahahaaaaaaaaaaa",
                onMemoChanged = {},
                isLongText = {},
                showMemoDialog = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_MemoCard_noMemo(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            MemoCard(
                isEditMode = false,
                memoText = null,
                onMemoChanged = {},
                isLongText = {},
                showMemoDialog = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_MemoCard_noMemo_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            MemoCard(
                isEditMode = true,
                memoText = null,
                onMemoChanged = {},
                isLongText = {},
                showMemoDialog = {}
            )
        }
    }
}