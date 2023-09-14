package com.example.somewhere.ui.screenUtils.cards

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.MyTextField
import com.example.somewhere.ui.screenUtils.SetColorDialog
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun TitleWithColorCard(
    isEditMode: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,

    modifier: Modifier = Modifier,
    date: Date? = null, //if not null, show color card
    onColorChange: (newMyColor: MyColor) -> Unit = {},
){
    var cardHeight by rememberSaveable { mutableStateOf(0) }

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
                TitleCard(isEditMode, titleText, onTitleChange, focusManager,
                    getCardHeight = {cardHeight_ ->
                        cardHeight = cardHeight_
                    }, modifier = Modifier.weight(1f)
                )

                if (date != null) {
                    var showColorDialog by rememberSaveable { mutableStateOf(false) }

                    if(showColorDialog){
                        SetColorDialog(
                            currentDate = date,
                            onDismissRequest = { showColorDialog = false },
                            onOkClick = {
                                onColorChange(it)
                                showColorDialog = false
                            }
                        )
                    }

                    
                    MySpacerRow(width = 16.dp)

                    //color card
                    Card(
                        elevation = 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size((cardHeight / LocalDensity.current.density).toInt().dp),
                        backgroundColor = Color(date.color.color),
                        onClick = {
                            showColorDialog = true
                        }
                    ) {

                    }
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
    focusManager: FocusManager
){
    TitleCard(isEditMode, titleText, onTitleChange, focusManager, getCardHeight = { })
}

@Composable
fun TitleCard(
    isEditMode: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,
    focusManager: FocusManager,

    getCardHeight: (cardHeight: Int) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    Card(
        elevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
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
                    Text(
                        text = stringResource(id = R.string.title_card_title),
                        style = titleTextStyle
                    )

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
                //TODO text num limit
                MyTextField(
                    inputText = titleText,
                    inputTextStyle = bodyTextStyle,

                    placeholderText = stringResource(id = R.string.title_card_body_add_a_title),
                    placeholderTextStyle = bodyNullTextStyle,

                    onValueChange = onTitleChange,
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