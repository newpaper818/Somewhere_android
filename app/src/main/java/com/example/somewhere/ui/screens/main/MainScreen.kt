package com.example.somewhere.ui.screens.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somewhere.R
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DeleteOrNotDialog
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.cards.DisplayImage
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screenUtils.NewItemButton
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import com.example.somewhere.ui.screens.somewhere.SomewhereTopAppBar
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import kotlinx.coroutines.launch

object MainDestination : NavigationDestination {
    override val route = "main"
    override var title = "Somewhere"
}

@Composable
fun MainScreen(
    isEditMode: Boolean,
    changeEditMode: (editMode: Boolean?) -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,

    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(factory = SomewhereViewModelProvider.Factory),
){
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val tripList = mainUiState.tripList

    val coroutineScope = rememberCoroutineScope()

    Scaffold (
        //top app bar
        topBar = {
            SomewhereTopAppBar(
                isEditMode = isEditMode,
                title = if (!isEditMode) MainDestination.title
                        else stringResource(id = R.string.top_bar_title_edit_trips),

                navigationIcon = MyIcons.menu,
                navigationIconOnClick = {/*TODO*/},

                actionIcon1 = if (!isEditMode) MyIcons.edit
                                else MyIcons.done,
                actionIcon1Onclick = {
                    changeEditMode(null)
                },

                actionIcon2 = null,
                actionIcon2Onclick = {}
            )
        }
    ) { i ->
        val a = i

        //display trips list
        Column {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 200.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colors.background)
            ) {
                if (tripList != emptyList<Trip>()) {
                    items(tripList) { it ->

                        var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                        if(showDeleteDialog){
                            DeleteOrNotDialog(
                                bodyText = stringResource(id = R.string.dialog_body_delete_trip),
                                deleteText = stringResource(id = R.string.dialog_button_delete),
                                onDismissRequest = { showDeleteDialog = false },
                                onDeleteClick = {
                                    showDeleteDialog = false
                                    coroutineScope.launch {
                                        mainViewModel.deleteTrip(it)
                                    }
                                }
                            )
                        }

                        TripListItem(
                            trip = it,
                            isEditMode = isEditMode,
                            onTripClick = { trip ->
                                navigateToTrip(false, trip)
                            },
                            deleteTrip = {
                                showDeleteDialog = true
                            }
                        )

                        MySpacerColumn(height = 16.dp)
                    }
                } else {
                    //TODO prettier img / text
                    item {
                        Text(
                            text = stringResource(id = R.string.no_trip),
                            style = getTextStyle(TextType.CARD__BODY)
                        )
                        MySpacerColumn(height = 16.dp)
                    }
                }

                //new trip button
                item {
                    MySpacerColumn(height = 16.dp)

                    NewItemButton(
                        text = stringResource(id = R.string.new_trip),
                        onClick = {
                            //add new trip view model
                            coroutineScope.launch {
                                val newTrip = mainViewModel.addAndGetNewTrip()

                                if (newTrip != null) {
                                    navigateToTrip(true, newTrip)
                                    changeEditMode(true)
                                } else
                                    Log.d("debug", "New Trip Button onClick - navigate to new trip - Can't find new trip")
                            }
                        }
                    )
                }

                //TODO remove item{} before release!
                item {
                    MySpacerColumn(height = 60.dp)
                    Test()
                }
            }

        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TripListItem(
    trip: Trip,
    isEditMode: Boolean,
    onTripClick: (Trip) -> Unit,
    deleteTrip: (Trip) -> Unit,

    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE),
    titleNullTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__TITLE_NULL),
    subtitleTextStyle: TextStyle = getTextStyle(TextType.TRIP_LIST_ITEM__SUBTITLE)
    ){

    val titleText = if (trip.titleText == null || trip.titleText == "") stringResource(id = R.string.no_title)
                    else trip.titleText
    val titleTextStyle1 = if (trip.titleText == null || trip.titleText == "") titleNullTextStyle
                            else titleTextStyle

    val subTitleText = trip.getStartEndDateText(true) ?: stringResource(id = R.string.trip_item_no_date)


    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isEditMode) {
                onTripClick(trip)
            },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            //image
            if (trip.imagePathList.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .size(98.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    elevation = 0.dp
                ) {
                    DisplayImage(imagePath = trip.imagePathList.first())
                }

                MySpacerRow(width = 12.dp)
            }
            
            MySpacerRow(width = 4.dp)
            
            //text title & trip date
            Column {
                //trip title
                Text(
                    text = titleText,
                    style = titleTextStyle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                MySpacerColumn(height = 8.dp)

                //trip start date - end date
                Text(
                    text = subTitleText,
                    style = subtitleTextStyle
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            //delete icon when edit mode
            AnimatedVisibility(
                visible = isEditMode,
                enter = scaleIn(tween(300)),
                exit = scaleOut(tween(400)) + fadeOut(tween(300))
            ) {
                IconButton(onClick = {
                    deleteTrip(trip)
                }) {
                    DisplayIcon(MyIcons.delete)
                }
            }
        }
    }
}


//test ======================================================================
@Composable
private fun Test(){
    Column {
        TextTest("H1", MaterialTheme.typography.h1)
        TextTest("H2", MaterialTheme.typography.h2)
        TextTest("H3", MaterialTheme.typography.h3)
        TextTest("H4", MaterialTheme.typography.h4)
        TextTest("H5", MaterialTheme.typography.h5)
        TextTest("H6", MaterialTheme.typography.h6)
        TextTest("subtitle1", MaterialTheme.typography.subtitle1)
        TextTest("subtitle2", MaterialTheme.typography.subtitle2)
        TextTest("body1", MaterialTheme.typography.body1)
        TextTest("body2", MaterialTheme.typography.body2)

        Spacer(modifier = Modifier.height(10.dp))

        ColorTest(MaterialTheme.colors.primary,         "primary",          MaterialTheme.colors.onPrimary)
        ColorTest(MaterialTheme.colors.primaryVariant,  "primaryVariant",   MaterialTheme.colors.onPrimary)
        ColorTest(MaterialTheme.colors.secondary,       "secondary",        MaterialTheme.colors.onSecondary)
        ColorTest(MaterialTheme.colors.secondaryVariant,"secondaryVariant", MaterialTheme.colors.onSecondary)
        ColorTest(MaterialTheme.colors.background,      "background",       MaterialTheme.colors.onBackground)
        ColorTest(MaterialTheme.colors.surface,         "surface",          MaterialTheme.colors.onSurface)
        ColorTest(MaterialTheme.colors.error,           "error",            MaterialTheme.colors.onError)
    }
}

@Composable
private fun TextTest(
    text: String,
    textStyle: TextStyle
){
    Text(
        text = "${textStyle.fontSize}   $text",
        style = textStyle
    )
}

@Composable
private fun ColorTest(
    color: Color,
    text: String,
    textColor: Color
){
    Row {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "On Text",
                style = MaterialTheme.typography.body1.copy(color = textColor)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(text = text)
    }
}
