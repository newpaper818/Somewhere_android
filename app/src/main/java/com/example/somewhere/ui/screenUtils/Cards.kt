package com.example.somewhere.ui.screenUtils

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import com.example.somewhere.model.Trip
import com.example.somewhere.typeUtils.SpotType
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TitleCard(
    isEditMode: Boolean,
    titleText: String?,
    onTitleChange: (String) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = MaterialTheme.typography.h6,
    bodyTextStyle: TextStyle = MaterialTheme.typography.body1,
    bodyNullTextStyle: TextStyle = MaterialTheme.typography.subtitle1
){
    AnimatedVisibility(
        visible = isEditMode,
        enter =
        scaleIn(animationSpec = tween(400))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(300, 100))
        ,
        exit =
        scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(400))
                + fadeOut(animationSpec = tween(300))
    ) {

        Column {
            Card(
                elevation = 0.dp,
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Trip Title",
                        style = titleTextStyle
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val focusManager = LocalFocusManager.current
                    //var titleTextInput by rememberSaveable{ mutableStateOf(titleText ?: "") }

                    //TODO focus 되면 배경색 달라지게?
                    //TODO text num limit
                    MyTextField(
                        inputText = titleText ?: "",
                        inputTextStyle = bodyTextStyle,

                        placeholderText = "Add a Title",
                        placeholderTextStyle = bodyNullTextStyle,

                        onValueChange = onTitleChange,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            //FIXME done 버튼 누를때 저장하기로 바꾸기
                        }),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TripDurationCard(
    currentTrip: Trip,

    isDateListEmpty: Boolean,
    startDateText: String?,
    endDateText: String?,
    durationText: String?,

    isEditMode: Boolean,
    setTripDuration: (startDate: LocalDate, endDate: LocalDate) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = MaterialTheme.typography.h6,
    bodyTextStyle: TextStyle = MaterialTheme.typography.body1,
    bodyNullTextStyle: TextStyle = MaterialTheme.typography.subtitle1
){
    val textStyle = if (!isDateListEmpty) bodyTextStyle else bodyNullTextStyle

    //date duration picker
    val calendarState = rememberUseCaseState(visible = false, embedded = true)

    val selectedRange = remember {
        val default = if (!isDateListEmpty)
            currentTrip.dateList.first().date..currentTrip.dateList.last().date
        else
            LocalDate.now().let { time -> time.plusDays(1)..time.plusDays(5) }
        mutableStateOf(default.toRange())
    }

    CalendarDialog(
        state = calendarState,
        //header = Header.Default("Select trip date duration"),
        config = CalendarConfig(
            yearSelection = true,
            monthSelection = true,
            style = CalendarStyle.MONTH
        ),
        selection = CalendarSelection.Period(
            selectedRange = selectedRange.value
        ){ startDate, endDate ->
            //TODO check delete date dialog
            setTripDuration(startDate, endDate)
        }
    )

    //ui
    Column {

        Card(
            elevation = 0.dp,
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth(),
            onClick = {
                calendarState.show()
            },
            enabled = isEditMode
        ){
            //Trip Duration     duration text
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = isEditMode,
                        enter = expandHorizontally(tween(400)),
                        exit = shrinkHorizontally(tween(400))
                    ) {
                        Text(
                            text = "Trip Duration",
                            style = titleTextStyle
                        )
                    }

                    Text(
                        text = durationText ?: "",
                        style = titleTextStyle,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = if (isEditMode) TextAlign.End
                        else TextAlign.Center,
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                //start date > end date
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //start date
                    Text(
                        text = startDateText ?: "Start Date",
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )

                    // >
                    DisplayIcon(MyIcons.rightArrow, color = IconColor.gray)

                    //end date
                    Text(
                        text = endDateText ?: "End Date",
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InformationCard(
    list: List<Pair<MyIcon, String?>>,

    modifier: Modifier = Modifier,

    textStyle: TextStyle = MaterialTheme.typography.body1
){
    //information card
    Card(
        elevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            list.forEach{
                if (it.second != null) {
                    IconTextRow(
                        icon = it.first,
                        text = it.second!!,
                        textStyle = textStyle
                    )

                    if (it != list.last())
                        Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun IconTextRow(
    icon: MyIcon,

    text: String,
    textStyle: TextStyle
){
    Row (
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ){
            DisplayIcon(icon = icon)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = textStyle
        )
    }
}



@Composable
fun MemoCard(
    isEditMode: Boolean,
    memoText: String?,
    onMemoChanged: (String) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = MaterialTheme.typography.h6,
    bodyTextStyle: TextStyle = MaterialTheme.typography.body1,
    bodyNullTextStyle: TextStyle = MaterialTheme.typography.subtitle1
){

    //memo card
    Card(
        elevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(tween(400)),
                exit = shrinkVertically(tween(400))
            ) {
                Column {
                    Text(
                        text = "Memo",
                        style = titleTextStyle
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            //view mode
            if(!isEditMode){
                Text(
                    text = memoText ?: "No Memo",
                    style = if (memoText != null)   bodyTextStyle
                            else                    bodyNullTextStyle,
                    textAlign = TextAlign.Justify
                )
            }

            //edit mode
            else{
                val focusManager = LocalFocusManager.current
                //var memoTextInput by rememberSaveable{ mutableStateOf(memoText ?: "") }

                //TODO focus 되면 배경색 달라지게?
                //TODO text num limit
                MyTextField(
                    inputText = memoText ?: "",
                    inputTextStyle = bodyTextStyle,

                    placeholderText = "Add a Memo",
                    placeholderTextStyle = bodyNullTextStyle,

                    onValueChange = onMemoChanged,
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(autoCorrect = true),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        //FIXME done 버튼 누를때 저장하기로 바꾸기
                        //onMemoChanged(memoTextInput)
                    }),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpotTypeCard(
    spotType: SpotType,
    textStyle: TextStyle,
    isShown: Boolean,
    isShownColor: Color,
    isNotShownColor: Color,
    onCardClicked: (SpotType) -> Unit,
    frontText: String = ""
){
    val cardColor =
        if (isShown)    isShownColor
        else    isNotShownColor

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(32.dp),
        backgroundColor = cardColor,
        elevation = 0.dp,
        onClick = { onCardClicked(spotType) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = frontText + stringResource(spotType.textId),
                style = textStyle,
                modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 2.dp)
            )
        }
    }
}

@Composable
fun MyTextField(
    inputText: String,
    inputTextStyle: TextStyle,

    placeholderText: String,
    placeholderTextStyle: TextStyle,

    onValueChange: (String) -> Unit,
    singleLine: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
){
    Box(){
        //text field
        BasicTextField(
            value = inputText ,
            textStyle = inputTextStyle.copy(MaterialTheme.colors.onSurface),
            onValueChange = onValueChange,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            cursorBrush = SolidColor(MaterialTheme.colors.primaryVariant)
        )

        //place holder text
        if (inputText == ""){
            Text(
                text = placeholderText,
                style = placeholderTextStyle
            )
        }
    }

}