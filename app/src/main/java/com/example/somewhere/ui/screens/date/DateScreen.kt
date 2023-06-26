package com.example.somewhere.ui.screens.date

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.somewhere.model.Date
import com.example.somewhere.model.Spot
import com.example.somewhere.model.Trip
import com.example.somewhere.typeUtils.SpotType
import com.example.somewhere.ui.navigation.NavigationDestination
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.GraphListItem
import com.example.somewhere.ui.screenUtils.InformationCard
import com.example.somewhere.ui.screenUtils.MemoCard
import com.example.somewhere.ui.screenUtils.MiniProgressBar
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.SpotTypeCard
import com.example.somewhere.ui.screenUtils.StartEndDummySpaceWithRoundedCorner
import com.example.somewhere.ui.screens.SomewhereViewModelProvider
import com.example.somewhere.ui.screens.trip.TripDestination
import kotlinx.coroutines.launch

object DateDestination : NavigationDestination {
    override val route = "date"
    override var title = "Date screen"
    const val dateIdArg = "dateId"
    val routeWithArgs = "$route/{$dateIdArg}"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DateScreen(
    isEditMode: Boolean,
    currentTrip: Trip,
    currentDate: Date,

    updateCurrentDate: (Int) -> Unit,
    onSpotClicked: (Spot) -> Unit,

    modifier: Modifier = Modifier,
    dateViewModel: DateViewModel = viewModel(factory = SomewhereViewModelProvider.Factory)
){
    val dateList = currentTrip.dateList
    val coroutineScope = rememberCoroutineScope()

    val pageCount = currentTrip.dateList.size
    val pagerState = rememberPagerState(
        initialPage = currentTrip.dateList.indexOf(currentDate)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            elevation = 0.dp
        ) {
            Column() {
                //progress bar
                MiniProgressBar(
                    pointCount = pageCount,
                    currentIndex = pagerState.currentPage,
                    isMove = false
                )

                // |< <  2023.03.28  > >|
                // date buttons
                DateTextWithButtons(
                    currentDate = currentDate,
                    isFirstDate = dateList.first() == currentDate,
                    isLastDate = dateList.last() == currentDate,
                    onNextDateButtonClicked = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    onPrevDateButtonClicked = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    onFirstDateButtonClicked = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onLastDateButtonClicked = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pageCount - 1)
                        }
                    },
                    modifier = modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        //spot page
        HorizontalPager(
            pageCount = pageCount,
            state = pagerState
        ) {pageIndex ->
            updateCurrentDate(pagerState.currentPage)

            DateInfo(
                isEditMode = isEditMode,
                currentTrip = currentTrip,
                currentDate = dateList[pageIndex],
                onSpotClicked = onSpotClicked,
                modifier = modifier
            )
        }
    }
}

@Composable
fun DateInfo(
    isEditMode: Boolean,
    currentTrip: Trip,
    currentDate: Date,

    onSpotClicked: (Spot) -> Unit,

    modifier: Modifier = Modifier   //padding(L,R) 16.dp
){
    val spotList = currentDate.spotList

    var spotTypeWithShownList by rememberSaveable{
        mutableStateOf(getSpotTypeListWithShownList(currentDate))
    }


    LazyColumn(
        contentPadding = PaddingValues(top = 0.dp, bottom = 200.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top

    ) {
        item {
            //budget, travel distance info card
            InformationCard(
                modifier = modifier,
                list = listOf(
                    //Pair(MyIcons.date, currentDate.getDateText()),
                    Pair(MyIcons.budget, currentDate.getTotalBudgetText(currentTrip)),
                    Pair(
                        MyIcons.travelDistance,
                        currentDate.getTotalTravelDistanceText(currentTrip)
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            //memo card
            MemoCard(
                isEditMode = isEditMode,
                memoText = currentDate.memo,
                onMemoChanged = {

                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (currentDate.spotList.isNotEmpty()) {

            item{
                //spot type card list horizontal
                // 3 Tour / 2 Food / 5 Move ...
                FilterCards(
                    date = currentDate,
                    spotTypeWithShownList = spotTypeWithShownList,
                    onCardClicked = {spotType ->
                        val newList = spotTypeWithShownList.map{
                            if (it.first == spotType) Pair (it.first, !it.second)
                            else it
                        }

                        spotTypeWithShownList = newList
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            //currentDate.allSpotCollapse()

            //spot list
            if(checkSpotListIsShown(spotTypeWithShownList)) {
                item {
                    StartEndDummySpaceWithRoundedCorner(
                        isFirst = true,
                        isLast = false,
                        modifier = modifier
                    )
                }

                //list with line
                items(spotList) {

                    //set point color
                    val pointColor =
                        if (it.spotType == SpotType.MOVE)
                            Color.Transparent
                        else
                            MaterialTheme.colors.primaryVariant

                    //list item
                    GraphListItem(
                        isEditMode = isEditMode,
                        isExpanded = false,
                        itemId = 1,

                        sideText = it.getTimeText(false),
                        mainText = it.titleText ?: "No Title",
                        expandedText = it.getExpandedText(currentTrip),

                        onTitleTextChange = { id, text ->

                        },

                        isFirstItem = it == spotList.first(),
                        isLastItem = it == spotList.last(),

                        onItemClick = onSpotClicked,
                        onExpandedButtonClicked = {id ->

                        },

                        pointColor = pointColor,
                        modifier = modifier
                    )
                }

                item {
                    StartEndDummySpaceWithRoundedCorner(
                        isFirst = false,
                        isLast = true,
                        modifier = modifier
                    )
                }
            }
            else {
                item {
                    NoChosenSpotTypeText()
                }
            }
        }
        else{
            // No plan... Let's create a new plan!
            item {
                NoPlanText()
            }
        }
    }
}

private fun getSpotTypeListWithShownList(
    date: Date
):  List<Pair<SpotType, Boolean>>{
    val list: MutableList<Pair<SpotType, Boolean>> = mutableListOf()

    val spotTypeList = enumValues<SpotType>()

    for (spotType in spotTypeList){
        if (date.getSpotTypeCount(spotType = spotType) != 0)
            list.add(Pair(spotType, true))
    }

    return list
}

private fun checkSpotListIsShown(
    spotTypeWithShownList: List<Pair<SpotType, Boolean>>
): Boolean {
    for (spotWithBoolean in spotTypeWithShownList){
        if (spotWithBoolean.second)
            return true
    }
    return false
}

//  <   3.28   >
@Composable
private fun DateTextWithButtons(
    currentDate: Date,
    isFirstDate: Boolean,
    isLastDate: Boolean,
    onNextDateButtonClicked: () -> Unit,
    onPrevDateButtonClicked: () -> Unit,
    onFirstDateButtonClicked: () -> Unit,
    onLastDateButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // left buttons
        if (isFirstDate) {
            IconButton(onClick = {}) {
                DisplayIcon(MyIcons.disabledToFirstPage)
            }
            IconButton(onClick = {}) {
                DisplayIcon(MyIcons.disabledLeftArrow)
            }
        } else {
            IconButton(onClick = onFirstDateButtonClicked) {
                DisplayIcon(MyIcons.toFirstPage)
            }
            IconButton(onClick = onPrevDateButtonClicked) {
                DisplayIcon(MyIcons.leftArrow)
            }
        }

        //date text
        Box(
            modifier = Modifier.width(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentDate.getDateText(),
                style = MaterialTheme.typography.h2
            )
        }

        //right buttons
        if (isLastDate) {
            IconButton(onClick = {}) {
                DisplayIcon(MyIcons.disabledRightArrow)
            }
            IconButton(onClick = {}) {
                DisplayIcon(MyIcons.disabledToLastPage)
            }
        } else {
            IconButton(onClick = onNextDateButtonClicked) {
                DisplayIcon(MyIcons.rightArrow)
            }
            IconButton(onClick = onLastDateButtonClicked) {
                DisplayIcon(MyIcons.toLastPage)
            }
        }
    }
}

@Composable
private fun FilterCards(
    date: Date,
    spotTypeWithShownList: List<Pair<SpotType, Boolean>>,
    onCardClicked: (SpotType) -> Unit,

    textStyle: TextStyle = MaterialTheme.typography.body2,
    isShownColor: Color = MaterialTheme.colors.secondaryVariant,
    isNotShownColor: Color = MaterialTheme.colors.surface,
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(16.dp, 0.dp, 4.dp, 0.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        items(spotTypeWithShownList){

            val spotTypeCount = date.getSpotTypeCount(it.first)

            if (spotTypeCount != 0) {
                Row{
                    SpotTypeCard(
                        frontText = "$spotTypeCount ",
                        spotType = it.first,
                        textStyle = textStyle,
                        isShown = it.second,
                        isShownColor = isShownColor,
                        isNotShownColor = isNotShownColor,
                        onCardClicked = {spotType ->
                            onCardClicked(spotType)
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }

}

@Composable
private fun NoPlanText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.subtitle1
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "No Plan...",
            style = textStyle
        )
        Text(
            text = "Let's create a new plan!",
            style = textStyle
        )
    }
}

@Composable
private fun NoChosenSpotTypeText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.subtitle1
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Choose a Spot Type!",
            style = textStyle
        )
    }
}
