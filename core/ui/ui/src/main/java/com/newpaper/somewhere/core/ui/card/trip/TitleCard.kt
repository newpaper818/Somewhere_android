package com.newpaper.somewhere.core.ui.card.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.ui.R

const val MAX_TITLE_LENGTH = 100

@Composable
fun TitleCard(
    isEditMode: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    modifier: Modifier = Modifier,
){
    AnimatedVisibility(
        visible = isEditMode,
        enter = scaleIn(animationSpec = tween(300, delayMillis = 30))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(250, delayMillis = 30)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300, delayMillis = 30))
                + fadeOut(animationSpec = tween(250))
    ) {
        Column(modifier = modifier) {
            Row {
                //TODO focus 되면 배경색 달라지게?
                TitleCardUi(isEditMode, titleText, onTitleChange, focusManager, isLongText,
                    getCardHeight = { }, modifier = Modifier.weight(1f)
                )
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
fun TitleWithColorCard(
    isEditMode: Boolean,

    titleText: String?,
    onTitleChange: (newTitle: String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    color: MyColor,
    onClickColorCard: () -> Unit,

    modifier: Modifier = Modifier
){
    var cardHeight by rememberSaveable { mutableIntStateOf(0) }

    AnimatedVisibility(
        visible = isEditMode,
        enter = scaleIn(animationSpec = tween(300, delayMillis = 30))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(250, delayMillis = 30)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300, delayMillis = 30))
                + fadeOut(animationSpec = tween(250))
    ) {
        Column(modifier = modifier) {
            Row {
                //TODO focus 되면 배경색 달라지게?
                TitleCardUi(isEditMode, titleText, onTitleChange, focusManager, isLongText,
                    getCardHeight = {newCardHeight ->
                        cardHeight = newCardHeight
                    }, modifier = Modifier.weight(1f)
                )

                MySpacerRow(width = 16.dp)

                //color card
                ClickableBox(
                    containerColor = Color(color.color),
                    modifier = Modifier
                        .width(77.dp)
                        .height((cardHeight / LocalDensity.current.density).toInt().dp),
                    contentAlignment = Alignment.Center,
                    onClick = onClickColorCard

                ) {
                    DisplayIcon(icon = MyIcons.setColor, color = Color(color.onColor))
                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
private fun TitleCardUi(
    isEditMode: Boolean,

    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    getCardHeight: (cardHeight: Int) -> Unit,

    modifier: Modifier = Modifier
){
    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isEditMode){
        isTextSizeLimit = (titleText ?: "").length > MAX_TITLE_LENGTH
    }

    val borderColor = if (isTextSizeLimit) CustomColor.outlineError
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
                        //title
                        Text(
                            text = stringResource(id = R.string.title_card_title),
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        //up to 100 characters
                        if (isTextSizeLimit){
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
                //TODO focus 되면 배경색 달라지게?
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
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    })
                )
            }
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
                titleText = null,
                onTitleChange = {},
                focusManager = LocalFocusManager.current,
                isLongText = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TitleWithColorCard(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TitleWithColorCard(
                isEditMode = true,
                titleText = "title text",
                onTitleChange = {},
                focusManager = LocalFocusManager.current,
                isLongText = { },
                color = MyColor(),
                onClickColorCard = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_TitleWithColorCard_Empty(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            TitleWithColorCard(
                isEditMode = true,
                titleText = null,
                onTitleChange = {},
                focusManager = LocalFocusManager.current,
                isLongText = { },
                color = MyColor(),
                onClickColorCard = {}
            )
        }
    }
}