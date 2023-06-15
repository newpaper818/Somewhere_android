package com.example.somewhere.ui.screens.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.somewhere.model.Trip
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.BottomSaveCancelBar
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object MainDestination : NavigationDestination {
    override val route = "main"
    override var title = "Somewhere"
}

@Composable
fun MainScreen(
    isEditMode: Boolean,
    onTripClicked: (Trip) -> Unit,
    changeEditMode: (Boolean) -> Unit,

    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
){

    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val tripList = mainUiState.tripList

    val coroutineScope = rememberCoroutineScope()


    //display trips list
    Column {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 200.dp),
            modifier = modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colors.background)
        ) {
            if (tripList != emptyList<Trip>()) {
                items(tripList) {
                    TripListItem(
                        trip = it,
                        isEditMode = isEditMode,
                        onTripClick = onTripClicked,
                        deleteTrip = {
                            //TODO add dialog: Are you sure to delete this trip? cancel / delete
                            coroutineScope.launch {
                                mainViewModel.deleteTrip(it)
                            }
                        }
                    )

                    MySpacerColumn(height = 16.dp)
                }
            }
            else {
                //TODO prettier img / text
                item {
                    Text(
                        text = "No Trip...\nLet's create a new trip!",
                        style = MaterialTheme.typography.h3
                    )
                    MySpacerColumn(height = 16.dp)
                }
            }

            //new trip button
            item{
                NewTripButton{
                    //add new trip view model
                    coroutineScope.launch {
                        val newTrip = mainViewModel.addAndGetNewTrip()

                        if (newTrip != null) {
                            onTripClicked(newTrip)
                            changeEditMode(true)
                        }
                        else
                            Log.d("test", "Can't find new trip")
                    }
                }
            }

            //TODO remove item{} before release!
            item {
                MySpacerColumn(height = 60.dp)
                Test()
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
    modifier: Modifier = Modifier
){

    Card(
        modifier = modifier
            .clickable(enabled = !isEditMode) {
                onTripClick(trip)
            }
            .clip(RoundedCornerShape(16.dp)),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = rememberAsyncImagePainter(model = trip.imagePath),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp),
                contentDescription = ""
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = trip.titleText?: "No Title",
                style = MaterialTheme.typography.h2
            )

            Spacer(modifier = Modifier.weight(1f))

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NewTripButton(
    onClick: () -> Unit
){
    Card(
        modifier = Modifier
            .height(50.dp)
            //.width(140.dp)
            .clip(RoundedCornerShape(25.dp)),
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.primaryVariant,
        elevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MySpacerRow(12.dp)
            DisplayIcon(icon = MyIcons.new)
            MySpacerRow(4.dp)
            Column() {
                Text(text = "New Trip")
                MySpacerColumn(2.dp)
            }
            MySpacerRow(18.dp)
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

        ColorTest(MaterialTheme.colors.primary, "primary")
        ColorTest(MaterialTheme.colors.primaryVariant, "primaryVariant")
        ColorTest(MaterialTheme.colors.secondary, "secondary")
        ColorTest(MaterialTheme.colors.secondaryVariant, "secondaryVariant")
        ColorTest(MaterialTheme.colors.background, "background")
        ColorTest(MaterialTheme.colors.surface, "surface")
        ColorTest(MaterialTheme.colors.error, "error")
    }
}

@Composable
private fun TextTest(
    text: String,
    textStyle: androidx.compose.ui.text.TextStyle
){
    Text(
        text = "${textStyle.fontSize}   $text",
        style = textStyle
    )
}

@Composable
private fun ColorTest(
    color: Color,
    text: String
){
    Row {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ){
            Text(text = "On Text")
        }

        Spacer(modifier = Modifier.width(7.dp))

        Text(text = text)
    }
}