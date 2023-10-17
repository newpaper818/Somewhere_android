package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.tripScreenUtils.MyTextField
import com.example.somewhere.ui.tripScreenUtils.dialogs.SetColorDialog
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

const val MAX_TITLE_LENGTH = 100

@OptIn(ExperimentalAnimationApi::class)
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
        enter =
        scaleIn(animationSpec = tween(170, 0, LinearEasing))
                + expandVertically(animationSpec = tween(190, 0, LinearEasing))
                + fadeIn(animationSpec = tween(300, 100)),
        exit =
        scaleOut(animationSpec = tween(250, 100))
                + shrinkVertically(animationSpec = tween(320, 100))
                + fadeOut(animationSpec = tween(300, 100))
    ) {
        Column(modifier = modifier) {
            Row {
                //TODO focus 되면 배경색 달라지게?
                //TODO text num limit
                TitleCardUi(isEditMode, titleText, onTitleChange, focusManager, isLongText,
                    getCardHeight = { }, modifier = Modifier.weight(1f)
                )
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TitleWithColorCard(
    isEditMode: Boolean,

    titleText: String?,
    onTitleChange: (newTitle: String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    color: MyColor,
    onColorChange: (newMyColor: MyColor) -> Unit,

    setShowBottomSaveCancelBar: (Boolean) -> Unit,

    modifier: Modifier = Modifier
){
    var cardHeight by rememberSaveable { mutableStateOf(0) }
    var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isEditMode,
        enter =
        scaleIn(animationSpec = tween(170, 0, LinearEasing))
                + expandVertically(animationSpec = tween(190, 0, LinearEasing))
                + fadeIn(animationSpec = tween(300, 100)),
        exit =
        scaleOut(animationSpec = tween(250, 100))
                + shrinkVertically(animationSpec = tween(320, 100))
                + fadeOut(animationSpec = tween(300, 100))
    ) {
        Column(modifier = modifier) {
            Row {
                //TODO focus 되면 배경색 달라지게?
                //TODO text num limit
                TitleCardUi(isEditMode, titleText, onTitleChange, focusManager, isLongText,
                    getCardHeight = {newCardHeight ->
                        cardHeight = newCardHeight
                    }, modifier = Modifier.weight(1f)
                )

                if(showColorPickerDialog){
                    SetColorDialog(
                        initialColor = color,
                        onDismissRequest = {
                            showColorPickerDialog = false
                            setShowBottomSaveCancelBar(true)
                        },
                        onOkClick = {
                            showColorPickerDialog = false
                            setShowBottomSaveCancelBar(true)
                            onColorChange(it)

                        }
                    )
                }

                MySpacerRow(width = 16.dp)

                //color card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(color.color)),
                    modifier = Modifier.size((cardHeight / LocalDensity.current.density).toInt().dp),
                    onClick = {
                        showColorPickerDialog = true
                        setShowBottomSaveCancelBar(false)
                    }
                ) {

                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
fun TitleCardMove(
    isEditMode: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit
){
    TitleCardUi(isEditMode, titleText, onTitleChange, focusManager, isLongText, getCardHeight = { })
}

@Composable
private fun TitleCardUi(
    isEditMode: Boolean,

    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,
    isLongText: (Boolean) -> Unit,

    getCardHeight: (cardHeight: Int) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    errorTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE_ERROR),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    var isTextSizeLimit by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isEditMode){
        isTextSizeLimit = (titleText ?: "").length > MAX_TITLE_LENGTH
    }

    val borderColor = if (isTextSizeLimit) getColor(ColorType.ERROR_BORDER)
                    else Color.Transparent

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .onSizeChanged {
                getCardHeight(it.height)
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
                        //title
                        Text(
                            text = stringResource(id = R.string.title_card_title),
                            style = titleTextStyle
                        )

                        //up to 100 characters
                        if (isTextSizeLimit){
                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = stringResource(id = R.string.long_text, MAX_TITLE_LENGTH),
                                style = errorTextStyle
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
                    style = if (titleText != null)   bodyTextStyle
                    else                    bodyNullTextStyle
                )
            }

            //edit mode
            else{
                //TODO focus 되면 배경색 달라지게?
                MyTextField(
                    inputText = titleText,
                    inputTextStyle = bodyTextStyle,

                    placeholderText = stringResource(id = R.string.title_card_body_add_a_title),
                    placeholderTextStyle = bodyNullTextStyle,

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