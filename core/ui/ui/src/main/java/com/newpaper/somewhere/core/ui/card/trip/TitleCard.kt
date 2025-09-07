package com.newpaper.somewhere.core.ui.card.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.enterVertically
import com.newpaper.somewhere.core.utils.enterVerticallyScaleIn
import com.newpaper.somewhere.core.utils.enterVerticallyScaleInDelay
import com.newpaper.somewhere.core.utils.exitVertically
import com.newpaper.somewhere.core.utils.exitVerticallyScaleOut

const val MAX_TITLE_LENGTH = 70

@Composable
fun TitleCard(
    isEditMode: Boolean,
    useDelayEnter: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    modifier: Modifier = Modifier,
    upperTitleText: String = stringResource(id = R.string.title_card_title)
){
    AnimatedVisibility(
        visible = isEditMode,
        enter = if (useDelayEnter) enterVerticallyScaleInDelay else enterVerticallyScaleIn,
        exit = exitVerticallyScaleOut
    ) {
        Column(modifier = modifier) {
            Row {
                //TODO focus 되면 배경색 달라지게?
                TitleCardUi(
                    isEditMode = isEditMode,
                    upperTitleText = upperTitleText,
                    titleText = titleText,
                    onTitleChange = onTitleChange,
                    focusManager = focusManager,
                    isLongText = isLongText,
                    getCardHeight = { }, modifier = Modifier.weight(1f)
                )
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}




@Composable
private fun TitleCardUi(
    isEditMode: Boolean,

    upperTitleText: String,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    //delete?
    getCardHeight: (cardHeight: Int) -> Unit,

    modifier: Modifier = Modifier
){
    var useErrorBorder by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isEditMode){
        useErrorBorder = (titleText ?: "").length > MAX_TITLE_LENGTH
    }

    val borderColor = if (useErrorBorder) CustomColor.outlineError
                        else Color.Transparent

    MyCard(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .onSizeChanged {
                getCardHeight(it.height)
            }
            .semantics(mergeDescendants = true) { }
    ) {
        TitleLayout(
            isEditMode = isEditMode,
            upperTitleText = upperTitleText,
            titleText = titleText,
            onTitleChange = onTitleChange,
            focusManager = focusManager,
            isLongText = {
                useErrorBorder = it
                isLongText(it)
            },
            modifier = Modifier.padding(16.dp, 14.dp)
        )
    }
}

@Composable
fun TitleLayout(
    isEditMode: Boolean,

    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    modifier: Modifier = Modifier,
    upperTitleText: String = stringResource(id = R.string.title_card_title),
    useUpperTitleAnimation: Boolean = false,
){

    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isEditMode){
        isTextSizeLimit = (titleText ?: "").length > MAX_TITLE_LENGTH
    }

    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = if (useUpperTitleAnimation) isEditMode else true,
            enter = enterVertically,
            exit = exitVertically
        ) {
            Column {
                Row {
                    //title
                    Text(
                        text = upperTitleText,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    //up to [MAX_TITLE_LENGTH] characters
                    if (isTextSizeLimit) {
                        MySpacerRow(width = 4.dp)

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = stringResource(id = R.string.long_text, MAX_TITLE_LENGTH),
                            style = MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError),
                            textAlign = TextAlign.Right,
                            maxLines = 2
                        )
                    }
                }

                MySpacerColumn(height = 8.dp)
            }
        }

        //view mode
        if(!isEditMode){
            Text(
                text = titleText ?: stringResource(id = R.string.no_title),
                style = if (titleText != null)   MaterialTheme.typography.bodyLarge
                        else                    MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        //edit mode
        else{
            MyTextField(
                inputText = titleText,
                inputTextStyle = MaterialTheme.typography.bodyLarge,

                placeholderText = stringResource(id = R.string.title_card_body_add_a_title),
                placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),

                onValueChange = {
                    if (it.length > MAX_TITLE_LENGTH && !isTextSizeLimit){
                        isTextSizeLimit = true
                        isLongText(true)
                    }
                    else if (it.length <= MAX_TITLE_LENGTH && isTextSizeLimit){
                        isTextSizeLimit = false
                        isLongText(false)
                    }
                    onTitleChange(it)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
        }
    }
}





















@Composable
@PreviewLightDark
private fun Preview_TitleCard_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TitleCard(
                isEditMode = true,
                useDelayEnter = true,
                titleText = "title text",
                onTitleChange = {},
                focusManager = LocalFocusManager.current,
                isLongText = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TitleCard_Edit_Empty(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TitleCard(
                isEditMode = true,
                useDelayEnter = true,
                titleText = null,
                onTitleChange = {},
                focusManager = LocalFocusManager.current,
                isLongText = {}
            )
        }
    }
}
