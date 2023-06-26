package com.example.somewhere.ui.screenUtils

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.somewhere.model.Date
import com.example.somewhere.typeUtils.SpotType
import com.example.somewhere.R
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import java.time.LocalDate


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    isEditMode: Boolean,
    imgList: List<String>,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,

    titleTextStyle: TextStyle = MaterialTheme.typography.h6,
    bodyTextStyle: TextStyle = MaterialTheme.typography.body1,
    bodyNullTextStyle: TextStyle = MaterialTheme.typography.subtitle1
){
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){uriList ->
        onAddImages(uriList.map { it.toString() })
    }

    AnimatedVisibility(
        visible = isEditMode || imgList.isNotEmpty(),
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {

        Column {
            Card(
                elevation = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                //edit mode
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = fadeIn(animationSpec = tween(100, 0)),
                    exit = fadeOut(animationSpec = tween(500, 0))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            //Images text
                            Text(
                                text = stringResource(id = R.string.image_card_title, imgList.size),
                                style = titleTextStyle,
                                modifier = Modifier.padding(16.dp, 14.dp, 0.dp, 8.dp),
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            //add images text
                            val toastText = stringResource(id = R.string.image_card_image_limit_toast)
                            
                            Text(
                                text = stringResource(id = R.string.image_card_subtitle_add_images),
                                style = titleTextStyle,
                                modifier = Modifier
                                    .clickable {
                                        if (imgList.size <= 9)
                                            galleryLauncher.launch("image/*")
                                        else
                                            Toast
                                                .makeText(
                                                    context,
                                                    toastText,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                    }
                                    .padding(16.dp, 14.dp, 16.dp, 8.dp)
                            )
                        }

                        //if no images
                        if (imgList.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.image_card_body_no_image),
                                style = bodyNullTextStyle,
                                modifier = Modifier.padding(16.dp, 0.dp)
                            )
                        }

                        //images
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            contentPadding = PaddingValues(16.dp, 0.dp, 0.dp, 0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(imgList) {
                                Card(
                                    modifier = Modifier
                                        .size(105.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    elevation = 0.dp
                                ) {
                                    Box {
                                        DisplayImage(imagePath = it)


                                        Column {
                                            MySpacerColumn(height = 4.dp)

                                            //delete icon
                                            Row {
                                                Spacer(modifier = Modifier.weight(1f))

                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colors.secondary)
                                                        .clickable { deleteImage(it) }
                                                ) {
                                                    DisplayIcon(icon = MyIcons.deleteImage)
                                                }

                                                MySpacerRow(width = 4.dp)
                                            }
                                        }
                                    }
                                }
                                MySpacerRow(width = 16.dp)
                            }
                        }
                        MySpacerColumn(height = 16.dp)
                    }
                }

                //not edit mode showing images
                AnimatedVisibility(
                    visible = !isEditMode,
                    enter = expandVertically(tween(400), expandFrom = Alignment.Bottom),
                    exit = shrinkVertically(
                        tween(350, easing = LinearEasing),
                        shrinkTowards = Alignment.Bottom
                    )
                ) {
                    val pageState = rememberPagerState()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        HorizontalPager(
                            pageCount = imgList.size,
                            state = pageState
                        ) {
                            DisplayImage(imagePath = imgList[it])
                        }

                        if (imgList.size != 1)
                            ImageIndicateDots(
                                pageCount = imgList.size,
                                currentPage = pageState.currentPage
                            )
                    }

                }
            }
            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
fun DisplayImage(
    imagePath: String
){
    val imagePainter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = Uri.parse(imagePath))
            .build()
    )

    Image(
        painter = imagePainter,
        contentDescription = stringResource(id = R.string.image_card_display_image_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
    )

}

@Composable
fun ImageIndicateDots(
    pageCount:Int,
    currentPage: Int
){
    Column {
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) {
                val color =
                    if (currentPage == it) Color.LightGray
                    else Color.DarkGray

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}


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
            scaleIn(animationSpec = tween(170, 0, LinearEasing))
            + expandVertically(animationSpec = tween(190, 0, LinearEasing))
            + fadeIn(animationSpec = tween(300, 100))
        ,
        exit =
            scaleOut(animationSpec = tween(250, 100))
            + shrinkVertically(animationSpec = tween(320, 100))
            + fadeOut(animationSpec = tween(300, 100))
    ) {

        Column {
            Card(
                elevation = 0.dp,
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp, 14.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.title_card_title),
                        style = titleTextStyle
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val focusManager = LocalFocusManager.current

                    //TODO focus 되면 배경색 달라지게?
                    //TODO text num limit
                    MyTextField(
                        inputText = titleText ?: "",
                        inputTextStyle = bodyTextStyle,

                        placeholderText = stringResource(id = R.string.title_card_body_add_a_title),
                        placeholderTextStyle = bodyNullTextStyle,

                        onValueChange = onTitleChange,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            //FIXME done 버튼 누를때 저장하기로 바꾸기
                        })
                    )
                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TripDurationCard(
    dateList: List<Date>,

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
    //set body text and body text style
    // Start Date || No Start Date || 2023.06.23
    val startDateText1 =
        startDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_start_date)
                            else stringResource(id = R.string.trip_duration_card_body_no_start_date)
    val endDateText1 =
        endDateText ?: if (isEditMode) stringResource(id = R.string.trip_duration_card_body_end_date)
                        else stringResource(id = R.string.trip_duration_card_body_no_end_date)

    val bodyTextStyle1 = if (!isDateListEmpty) bodyTextStyle else bodyNullTextStyle

    //date duration picker dialog
    val calendarState = rememberUseCaseState(visible = false, embedded = true)

    val defaultDateRange =
        if (!isDateListEmpty)
            dateList.first().date..dateList.last().date
        else
            LocalDate.now().let { now -> now.plusDays(1)..now.plusDays(5) }

    CalendarDialog(
        state = calendarState,
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
                modifier = Modifier.padding(16.dp, 14.dp)
            ) {

                AnimatedVisibility(
                    visible = !(!isEditMode && durationText == null),
                    enter = expandVertically(tween(400)),
                    exit = shrinkVertically(tween(400))
                ) {

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AnimatedVisibility(
                                visible = isEditMode,
                                enter = expandHorizontally(tween(400)),
                                exit = shrinkHorizontally(tween(400))
                            ) {
                                Text(
                                    text = stringResource(id = R.string.trip_duration_card_title),
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

                        MySpacerColumn(height = 8.dp)
                    }
                }

                //start date > end date
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //start date
                    Text(
                        text = startDateText1,
                        style = bodyTextStyle1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                    )

                    // >
                    DisplayIcon(MyIcons.rightArrow, color = IconColor.gray)

                    //end date
                    Text(
                        text = endDateText1,
                        style = bodyTextStyle1,
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
            modifier = Modifier.padding(16.dp, 14.dp)
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
                    inputText = memoText ?: "",
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
    Box {
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
