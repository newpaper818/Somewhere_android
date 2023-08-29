package com.example.somewhere.ui.screenUtils

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.util.toRange
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.typeUtils.SpotType
import com.example.somewhere.typeUtils.SpotTypeGroup
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.dateColorList
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime

@Composable
private fun MyDialog(
    onDismissRequest: () -> Unit,

    modifier: Modifier = Modifier,
    titleText: String? = null,
    bodyText: String? = null,
    bodyContent: @Composable() (() -> Unit)? = null,
    buttonContent: @Composable() (() -> Unit)? = null
){
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = getColor(ColorType.DIALOG__BACKGROUND)
        ) {
            Column(
                modifier = modifier
                    .width(370.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //title text / align center
                if (titleText != null){
                    Text(
                        text = titleText,
                        style = getTextStyle(TextType.DIALOG__TITLE)
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body text / align left(fillMaxWidth)
                if (bodyText != null){
                    Text(
                        text = bodyText,
                        style = getTextStyle(TextType.DIALOG__BODY),
                        modifier = Modifier
                            .padding(3.dp, 0.dp)
                            .fillMaxWidth()
                    )

                    MySpacerColumn(height = 16.dp)
                }

                //body
                if (bodyContent != null) {
                    bodyContent()
                    MySpacerColumn(height = 12.dp)
                }

                //buttons
                if (buttonContent != null) {
                    buttonContent()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,

    modifier: Modifier = Modifier,
    buttonColor: Color = Color.Transparent,
    textColor: Color? = null
){
    val textSytle = if (textColor != null)  getTextStyle(TextType.DIALOG__BUTTON).copy(color = textColor)
                    else                    getTextStyle(TextType.DIALOG__BUTTON)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .width(120.dp)
            .height(36.dp),
        elevation = 0.dp,
        backgroundColor = buttonColor,
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = textSytle
            )
        }
    }
}

@Composable
fun DeleteOrNotDialog(
    bodyText: String,
    deleteText: String,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        bodyText = bodyText,
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //delete button
                DialogButton(
                    text = deleteText,
                    textColor = getColor(ColorType.BUTTON__DELETE),
                    onClick = onDeleteClick
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetColorDialog(
    currentDate: Date,

    onDismissRequest: () -> Unit,
    onOkClick: (newColor: Int) -> Unit
){
    var dateColor by rememberSaveable { mutableStateOf(currentDate.iconColor) }


    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.dialog_title_set_color),
        bodyContent = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.heightIn(0.dp, 400.dp).clip(RoundedCornerShape(16.dp))
            ){
                items(dateColorList){
                    Card(
                        elevation = 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .size(67.dp),
                        backgroundColor = Color(it),
                        onClick = {
                            dateColor = it
                        }
                    ){
                        if (dateColor == it) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                DisplayIcon(icon = MyIcons.selectedColor)
                            }
                        }
                    }
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
                    onClick = {
                        onOkClick(dateColor)
                    }
                )
            }
        }
    )
}

@Composable
fun SelectDateDialog(
    currentDate: Date,
    dateList: List<Date>,
    onDateClick: (dateId: Int) -> Unit,
    onDismissRequest: () -> Unit
){
    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.dialog_title_move_date),
        bodyContent = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(0.dp, 400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
            ){
                items(dateList){
                    GraphListItem(
                        isEditMode = false,
                        isExpanded = false,
                        itemId = it.id,
                        sideText = it.getDateText(false),
                        mainText = it.titleText,
                        expandedText = null,
                        onTitleTextChange = { _, _ ->

                        },
                        isFirstItem = it == dateList.first(),
                        isLastItem = it == dateList.last(),
                        availableDelete = false,
                        onItemClick = {newDateId ->
                            onDateClick(newDateId)
                            onDismissRequest()
                        },
                        onExpandedButtonClicked = {},
                        itemColor = if (it == currentDate) getColor(ColorType.CARD_SELECTED)
                                    else                   Color.Transparent
                    )
                }
            }
        },
        buttonContent = {
            DialogButton(
                text = stringResource(id = R.string.dialog_button_cancel),
                onClick = onDismissRequest
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCalendarDialog(
    dialogState: UseCaseState,
    defaultDateRange: ClosedRange<LocalDate>,
    setTripDuration: (startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    CalendarDialog(
        state = dialogState,
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH
        ),
        selection = CalendarSelection.Period(
            selectedRange = defaultDateRange.toRange()
        ){ startDate, endDate ->
            //TODO check delete date dialog
            setTripDuration(startDate, endDate)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTimeDialog(
    dialogState: UseCaseState,
    initialTime: LocalTime?,
    title: String,
    setTime: (time: LocalTime) -> Unit
){
    ClockDialog(
        state = dialogState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            setTime(LocalTime.of(hours, minutes, 0))
        },
        config = ClockConfig(
            defaultTime = initialTime,
            is24HourFormat = false
        ),
        header = Header.Default(title = title)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SetSpotTypeDialog(
    initialSpotType: SpotType,
    onDismissRequest: () -> Unit,
    onOkClick: (SpotType) -> Unit,

    defaultTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL),
    selectedCardColor: Color = getColor(ColorType.CARD_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL).copy(color = getColor(ColorType.CARD_ON_SELECTED), fontWeight = FontWeight.Bold)
){
    val spotTypeGroupList = enumValues<SpotTypeGroup>()
    val spotTypeList = enumValues<SpotType>()

    var currentSpotTypeGroup by rememberSaveable { mutableStateOf(initialSpotType.group) }
    var currentSpotType by rememberSaveable { mutableStateOf(initialSpotType) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = stringResource(id = R.string.dialog_title_set_spot_type),
        bodyContent = {

            //spot type group row list
            LazyRow(
                modifier = Modifier
                    //.height(400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
                    .padding(0.dp, 8.dp)
            ){

                item{
                    MySpacerRow(width = 8.dp)
                }

                items(spotTypeGroupList){
                    val cardColor = if (it == currentSpotTypeGroup) selectedCardColor
                                    else                            Color.Transparent

                    val cardTextStyle = if (it == currentSpotTypeGroup) selectedTextStyle
                                        else                            defaultTextStyle

                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .height(32.dp),
                        backgroundColor = cardColor,
                        elevation = 0.dp,
                        onClick = {
                            currentSpotTypeGroup = it
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp, 4.dp)
                        ){
                            Text(
                                text = stringResource(id = it.textId),
                                style = cardTextStyle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(40.dp)
                            )
                        }
                    }
                    MySpacerRow(width = 8.dp)
                }
            }

            MySpacerColumn(height = 8.dp)

            Column(
                modifier = Modifier
                    .height(400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
            ) {
                //spot type list
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxHeight()
                ){
                    items(spotTypeList){
                        if (it.group == currentSpotTypeGroup) {

                            val cardColor = if (it == currentSpotType) selectedCardColor
                                            else                       Color.Transparent

                            val cardTextStyle = if (it == currentSpotType) selectedTextStyle
                                            else                            defaultTextStyle

                            Card(
                                elevation = 0.dp,
                                backgroundColor = cardColor,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .height(44.dp)
                                    .fillMaxWidth(),
                                onClick = {
                                    currentSpotType = it
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp, 0.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = it.textId),
                                        style = cardTextStyle,
                                    )
                                }

                            }
                        }
                    }
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    //buttonColor = getColor(ColorType.BUTTON__NEGATIVE),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
                    //buttonColor = getColor(ColorType.BUTTON__POSITIVE),
                    onClick = {
                        onOkClick(currentSpotType)
                    }
                )
            }
        }
    )


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SetBudgetOrDistanceDialog(
    initialValue: Float,
    onDismissRequest: () -> Unit,
    onSaveClick: (newBudget: Float) -> Unit,
    currencySymbol: String? = null
){
    val context = LocalContext.current
    val titleText = if (currencySymbol == null) stringResource(id = R.string.dialog_title_set_travel_distance)
                    else                        stringResource(id = R.string.dialog_title_set_budget)

    val invalidToastText = if (currencySymbol == null) stringResource(id = R.string.toast_invalid_travel_distance)
                            else                        stringResource(id = R.string.toast_invalid_budget)

    val newValueText: String by rememberSaveable {
        mutableStateOf(
            if (initialValue == 0.0f) ""
            else                      initialValue.toString()
        )
    }

    val textFieldValue = remember { mutableStateOf(
        TextFieldValue(newValueText, TextRange(newValueText.length))
    ) }

    var isInvalidText by rememberSaveable { mutableStateOf(false) }

    val borderColor = if (isInvalidText)  getColor(ColorType.ERROR)
                        else                    Color.Transparent


    MyDialog(
        onDismissRequest = onDismissRequest,
        titleText = titleText,
        bodyContent = {

            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester()}

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                elevation = 0.dp,
                backgroundColor = getColor(ColorType.DIALOG__CARD),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    if(currencySymbol != null) {
                        Text(
                            text = currencySymbol,
                            style = getTextStyle(TextType.DIALOG__BODY)
                        )

                        MySpacerRow(width = 8.dp)
                    }

                    MyTextField(
                        inputText = textFieldValue.value,
                        inputTextStyle = getTextStyle(TextType.DIALOG__BODY).copy(textAlign = TextAlign.End),
                        placeholderText = "0.00",
                        placeholderTextStyle = getTextStyle(TextType.DIALOG__BODY_NULL).copy(textAlign = TextAlign.End),
                        onValueChange = {
                            textFieldValue.value = TextFieldValue(it.text, TextRange(it.text.length))
                            isInvalidText = !isValidFloatText(it.text)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        modifier = Modifier.weight(1f),
                        textFieldModifier = Modifier.focusRequester(focusRequester)
                    )


                    if (currencySymbol == null){
                        MySpacerRow(width = 8.dp)

                        Text(
                            text = "km",
                            style = getTextStyle(TextType.DIALOG__BODY)
                        )
                    }
                }
            }

        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
                    onClick = {
                        if (!isInvalidText) {
                            val text = if(textFieldValue.value.text == "")  "0"
                                        else    textFieldValue.value.text

                            onSaveClick(text.toFloat())
                        }
                        else{
                            //toast
                            Toast.makeText(context, invalidToastText, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    )
}

private fun isValidFloatText(
    budgetText: String
): Boolean{
    var dotCnt = 0

    for (char in budgetText){
        if (char == '.')                 dotCnt++
        else if (char in '0'..'9')  continue
        else                             return false

        if (dotCnt >= 2)
            return false
    }
    return true
}

