package com.newpaper.somewhere.feature.trip.trips

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.NewTripButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getAllImagesPath
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.trip.Image.ImageViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.component.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.component.GoogleBannerAd
import com.newpaper.somewhere.feature.trip.trips.component.LoadingTripsItem
import com.newpaper.somewhere.feature.trip.trips.component.NoTripCard
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import java.time.LocalDate

internal val tripCardHeightDp = 120.dp


@Composable
fun TripsRoute(
    appUserId: String,
    internetEnabled: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,
    isEditMode: Boolean,
    changeEditMode: (editMode: Boolean?) -> Unit,

    spacerValue: Dp,

    lazyListState: LazyListState,

    dateTimeFormat: DateTimeFormat,

    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,
    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,


    modifier: Modifier = Modifier,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    imageViewModel: ImageViewModel = hiltViewModel()
){
    val context = LocalContext.current
    var loadingTrips by rememberSaveable { mutableStateOf(true) }

    //get trips
    LaunchedEffect(Unit) {

        loadingTrips = true
        tripsViewModel.updateTrips(
            internetEnabled = internetEnabled,
            appUserId = appUserId
        )
        loadingTrips = false

//        tripViewModel.updateGlanceSpotInfo(
//            internetEnabled = internetEnabled,
//            appUserId = appUserId,
//            firestoreDb = firestoreDb
//        )
    }

    val adView = AdView(context).apply {
        setAdSize(AdSize.BANNER)
        //FIXME when debug mode
//        adUnitId = if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/6300978111"
//                    else "ca-app-pub-9435484963354123/6706048530"
        adUnitId = "ca-app-pub-3940256099942544/6300978111"


        loadAd(AdRequest.Builder().build())
    }

    val tripUiState by tripsViewModel.tripsUiState.collectAsState()
    val originalTripList = tripUiState.trips.trips ?: listOf()
    val tempTripList = tripUiState.trips.tempTrips ?: listOf()

    val originalSharedTripList = tripUiState.trips.sharedTrips ?: listOf()
    val tempSharedTripList = tripUiState.trips.tempSharedTrips ?: listOf()

    val showingTripList =
        if (isEditMode) tempTripList
        else            originalTripList

    val showingSharedTripList =
        if (isEditMode) tempSharedTripList
        else            originalSharedTripList

    val density = LocalDensity.current

    var lazyColumnHeightDp by rememberSaveable { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBackButtonClick = {
        if (originalTripList != tempTripList || originalSharedTripList != tempSharedTripList)
            showExitDialog = true
        else
            changeEditMode(false)
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }




}

@Composable
internal fun TripsScreen(
    appUserId: String,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,

    spacerValue: Dp,
    isEditMode: Boolean,
    lazyListState: LazyListState,
    dateTimeFormat: DateTimeFormat,

    adView: AdView,

    internetEnabled: Boolean,
    loadingTrips: Boolean,

    showingTrips: List<Trip>,
    showingSharedTrips: List<Trip>,
    glance: Glance,

    onBackButtonClick: () -> Unit,
    changeEditMode: (editMode: Boolean?) -> Unit,

    onDeleteTrip: () -> Unit,
    onDeleteSharedTrip: () -> Unit,

    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    onClickGlanceSpot: () -> Unit,

    saveTrips: () -> Unit,
    unSaveTempTrips: () -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,

    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit,

    downloadImage: (imagePath: String) -> Boolean,

    setIsLoadingTrips: (loadingTrips: Boolean) -> Unit,

    modifier: Modifier = Modifier
){

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val density = LocalDensity.current
    var lazyColumnHeightDp by rememberSaveable { mutableIntStateOf(0) }

    val slideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingTrips.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    val sharedTripsSlideStates = remember { mutableStateMapOf<Int, SlideState>(
        *showingSharedTrips.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    MyScaffold(
        modifier = Modifier,

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                internetEnabled = internetEnabled,
                title = if (!isEditMode) stringResource(id = R.string.trips)
                        else stringResource(id = R.string.edit_trips),

                actionIcon2 = if (!isEditMode && !loadingTrips) TopAppBarIcon.edit else null,
                actionIcon2Onclick = {
                    changeEditMode(null)
                },
                startPadding = spacerValue
            )
        },

        //fab
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            GlanceSpot(
                visible = glance.visible && !isEditMode,
                dateTimeFormat = dateTimeFormat,
                trip = glance.trip ?: Trip(), //if glanceVisible is true, glanceTrip, Date, Spot is not null
                date = glance.date ?: Date(date = LocalDate.now()),
                spot = glance.spot ?: Spot(date = LocalDate.now()),
                onClick = {
                    //go to spot screen
                    onClickGlanceSpot()
                },
            )
        },

        //bottom save cancel button
        bottomSaveCancelBarVisible = isEditMode,
        onCancelClick = { onBackButtonClick() },
        onSaveClick = {
            saveTrips()

            organizeAddedDeletedImages(true)
        }
    ) { paddingValues ->

        if(showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { showExitDialog = false },
                onDeleteClick = {
                    showExitDialog = false
                    changeEditMode(false)
                    unSaveTempTrips()
                    organizeAddedDeletedImages(false)
                }
            )
        }

        //display trips list (my trips + shared trips)
        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(spacerValue, 16.dp, spacerValue, 200.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .onSizeChanged {
                    with(density) {
                        lazyColumnHeightDp = it.height.toDp().value.toInt()
                    }
                }
        ) {
            item{
                GoogleBannerAd (
                    internetEnabled = internetEnabled,
                    adView = adView
                )
            }

            if (showingTrips.isNotEmpty() || showingSharedTrips.isNotEmpty()) {

                firstLaunchToFalse()

                //each my trip item ================================================================
                if (showingTrips.isNotEmpty()){
                    item {
                        Text(
                            text = stringResource(id = R.string.my_trips),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier
                                .height(34.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .wrapContentHeight(Alignment.Bottom)
                        )
                    }
                }

                items(showingTrips) { trip ->

                    //delete trip dialog
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                    if (showDeleteDialog) {
                        DeleteOrNotDialog(
                            bodyText = stringResource(id = R.string.dialog_body_delete_trip),
                            deleteText = stringResource(id = R.string.dialog_button_delete),
                            onDismissRequest = { showDeleteDialog = false },
                            onDeleteClick = {
                                onDeleteTrip()
                                showDeleteDialog = false
                            }
                        )
                    }

                    key(showingTrips) {
                        TripItem(
                            isEditMode = isEditMode,
                            internetEnabled = internetEnabled,
                            dateTimeFormat = dateTimeFormat,
                            firstLaunch = firstLaunch,
                            trip = trip,
                            trips = showingTrips,
                            onClick = {
                                setIsLoadingTrips(true)
                                navigateToTrip(false, it)
                            },
                            onLongClick = {
                                showDeleteDialog = true
                                addDeletedImages(it.getAllImagesPath())
                            },
                            downloadImage = downloadImage,
                            slideState = slideStates[trip.id] ?: SlideState.NONE,
                            updateSlideState = { tripId, newSlideState ->
                                slideStates[showingTrips[tripId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                updateItemPosition(currentIndex, destinationIndex)

                                //all slideState to NONE
                                slideStates.putAll(showingTrips.map { it.id }
                                    .associateWith { SlideState.NONE })
                            }
                        )
                    }
                }

                //each shared trip item ================================================================
                if (showingSharedTrips.isNotEmpty()){
                    item {
                        Text(
                            text = stringResource(id = R.string.shared_trips),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier
                                .height(34.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .wrapContentHeight(Alignment.Bottom)
                        )
                    }
                }

                items(showingSharedTrips) { sharedTrip ->

                    //get out trip dialog
                    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

                    if (showDeleteDialog) {
                        DeleteOrNotDialog(
                            bodyText = stringResource(id = R.string.dialog_title_get_out_shared_trip),
                            deleteText = stringResource(id = R.string.dialog_button_exit),
                            onDismissRequest = { showDeleteDialog = false },
                            onDeleteClick = {
                                onDeleteSharedTrip()
                                showDeleteDialog = false
                            }
                        )
                    }

                    key(showingSharedTrips) {
                        TripItem(
                            isEditMode = isEditMode,
                            internetEnabled = internetEnabled,
                            dateTimeFormat = dateTimeFormat,
                            firstLaunch = firstLaunch,
                            trip = sharedTrip,
                            trips = showingSharedTrips,
                            onClick = {
                                setIsLoadingTrips(true)
                                navigateToTrip(false, it)
                            },
                            onLongClick = {
                                showDeleteDialog = true
                                addDeletedImages(it.getAllImagesPath())
                            },
                            downloadImage = downloadImage,
                            slideState = sharedTripsSlideStates[sharedTrip.id] ?: SlideState.NONE,
                            updateSlideState = { tripId, newSlideState ->
                                sharedTripsSlideStates[showingTrips[tripId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                updateItemPosition(currentIndex, destinationIndex)

                                //all slideState to NONE
                                sharedTripsSlideStates.putAll(showingTrips.map { it.id }
                                    .associateWith { SlideState.NONE })
                            }
                        )
                    }
                }
            }
            else {
                if (!loadingTrips) {
                    item {
                        NoTripCard()
                    }
                }
                else {
                    item {
                        LoadingTripsItem()
                    }
                }
            }

            item {
                NewTripButton(
                    visible = !loadingTrips && !isEditMode && showingTrips.size < 50,
                    onClick = {
                        setIsLoadingTrips(true)
                        navigateToTrip(true, null)
                    }
                )
            }
        }
    }
}



