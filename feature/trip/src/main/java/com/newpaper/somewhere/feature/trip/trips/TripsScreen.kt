package com.newpaper.somewhere.feature.trip.trips

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.NewTripButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.AD_UNIT_ID
import com.newpaper.somewhere.core.utils.AD_UNIT_ID_TEST
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.convert.getAllImagesPath
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrNotDialog
import com.newpaper.somewhere.feature.trip.BuildConfig
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.component.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.component.GoogleBannerAd
import com.newpaper.somewhere.feature.trip.trips.component.LoadingTripsItem
import com.newpaper.somewhere.feature.trip.trips.component.NoTripCard
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import kotlinx.coroutines.launch
import java.time.LocalDate

internal val tripCardHeightDp = 120.dp


@Composable
fun TripsRoute(
    commonTripViewModel: CommonTripViewModel,
    tripsViewModel: TripsViewModel,
    appUserId: String,
    internetEnabled: Boolean,
    useBottomNavBar: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,
    isEditMode: Boolean,

    spacerValue: Dp,

    lazyListState: LazyListState,

    dateTimeFormat: DateTimeFormat,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,

    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()
    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()

    val originalTrips = commonTripUiState.tripInfo.trips ?: listOf()
    val tempTrips = commonTripUiState.tripInfo.tempTrips ?: listOf()

    val originalSharedTrips = commonTripUiState.tripInfo.sharedTrips ?: listOf()
    val tempSharedTrips = commonTripUiState.tripInfo.tempSharedTrips ?: listOf()

    val coroutineScope = rememberCoroutineScope()

    //get trips
    LaunchedEffect(Unit) {
        tripsViewModel.setLoadingTrips(true)

        //update trips
        tripsViewModel.updateTrips(
            internetEnabled = internetEnabled,
            appUserId = appUserId
        )
        tripsViewModel.setLoadingTrips(false)

        //update glance
        val glanceTripWithEmptyDateList = tripsViewModel.findCurrentDateTripAndUpdateGlanceTrip()

        if (glanceTripWithEmptyDateList != null &&
            glanceTripWithEmptyDateList.dateList.isEmpty()){

            val glanceTrip = commonTripViewModel.updateTrip(
                internetEnabled = internetEnabled,
                appUserId = appUserId,
                tripWithEmptyDateList = glanceTripWithEmptyDateList
            )

            tripsViewModel.updateGlanceSpotInfo(
                //update trip info (only at empty date list - load once)
                glanceTrip = glanceTrip
            )
        }
    }

    val adView = AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = if (BuildConfig.DEBUG) AD_UNIT_ID_TEST
                    else AD_UNIT_ID

        loadAd(AdRequest.Builder().build())
    }

    val showingTrips =
        if (isEditMode) tempTrips
        else            originalTrips

    val showingSharedTrips =
        if (isEditMode) tempSharedTrips
        else            originalSharedTrips

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBackButtonClick = {
        if (originalTrips != tempTrips || originalSharedTrips != tempSharedTrips)
            showExitDialog = true
        else
            commonTripViewModel.setIsEditMode(false)
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }



    TripsScreen(
        spacerValue = spacerValue,
        dateTimeFormat = dateTimeFormat,
        internetEnabled = internetEnabled,
        useBottomNavBar = useBottomNavBar,

        firstLaunch = firstLaunch,
        firstLaunchToFalse = firstLaunchToFalse,

        loadingTrips = tripsUiState.loadingTrips,
        setIsLoadingTrips = { tripsViewModel.setLoadingTrips(it) },

        isEditMode = isEditMode,
        setEditMode = commonTripViewModel::setIsEditMode,

        showExitDialog = showExitDialog,
        showExitDialogToFalse = { showExitDialog = false },

        lazyListState = lazyListState,
        adView = adView,

        tripsInfo = TripsInfo(
            showingTrips = showingTrips,
            showingSharedTrips = showingSharedTrips,
            glance = tripsUiState.glance,
        ),
        image = TripsImage(
            downloadImage = commonTripViewModel::getImage,
            addDeletedImages = { commonTripViewModel.addDeletedImages(it) },
            organizeAddedDeletedImages = { commonTripViewModel.organizeAddedDeletedImages(
                tripManagerId = appUserId,
                isClickSave = it,
                isInTripsScreen = true
            ) }
        ),
        tripsEdit = TripsEdit(
            saveTrips = {
                coroutineScope.launch {
                    tripsViewModel.saveTrips(
                        appUserId = appUserId
                    )
                }
            },
            unSaveTempTrips = { commonTripViewModel.unSaveTempTrips() },
            onDeleteTrip = {
                tripsViewModel.deleteTrip(
                    trip = it,
                    appUserId = appUserId
                )
            }
        ),
        navigate = TripsNavigate(
            onBackButtonClick = onBackButtonClick,
            navigateToTrip = { isNewTrip, trip ->
                if (isNewTrip && trip == null) {
                    val newTrip = tripsViewModel.addAndGetNewTrip(appUserId)
                    navigateToTrip(isNewTrip, newTrip)
                }
                else
                    navigateToTrip(isNewTrip, trip!!)
            },
            navigateToGlanceSpot = { navigateToGlanceSpot(tripsUiState.glance) },
        ),


        updateTripItemOrder = tripsViewModel::reorderTempTrips,
        modifier = modifier
    )
}



data class TripsInfo(
    val showingTrips: List<Trip> = listOf(),
    val showingSharedTrips: List<Trip> = listOf(),
    val glance: Glance = Glance(),
)

data class TripsImage(
    val downloadImage: (imagePath: String, imageUserId: String, result:(Boolean) -> Unit) -> Unit = {_,_,_ ->},
    val addDeletedImages: (imageFiles: List<String>) -> Unit = {},
    val organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit = {},
)

data class TripsEdit(
    val saveTrips: () -> Unit = {},
    val unSaveTempTrips: () -> Unit = {},
    val onDeleteTrip: (trip: Trip) -> Unit = {},
)

data class TripsNavigate(
    val onBackButtonClick: () -> Unit = {},
    val navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit = {_,_ ->},
    val navigateToGlanceSpot: () -> Unit = {},
)

@Composable
private fun TripsScreen(
    spacerValue: Dp,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,
    useBottomNavBar: Boolean,

    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,

    loadingTrips: Boolean,
    setIsLoadingTrips: (loadingTrips: Boolean) -> Unit,

    isEditMode: Boolean,
    setEditMode: (editMode: Boolean?) -> Unit,

    showExitDialog: Boolean,
    showExitDialogToFalse: () -> Unit,

    lazyListState: LazyListState,
    adView: AdView,

    tripsInfo: TripsInfo,
    image: TripsImage,
    tripsEdit: TripsEdit,
    navigate: TripsNavigate,

    updateTripItemOrder: (isSharedTrips: Boolean, currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier
){

    val showingTrips = tripsInfo.showingTrips
    val showingSharedTrips = tripsInfo.showingSharedTrips
    val glance = tripsInfo.glance

    val density = LocalDensity.current
    var lazyColumnHeightDp by rememberSaveable { mutableIntStateOf(0) }

    val slideStates = remember { mutableStateMapOf(
        *showingTrips.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    val sharedTripsSlideStates = remember { mutableStateMapOf(
        *showingSharedTrips.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    val tripsIsEmpty = showingTrips.isEmpty() && showingSharedTrips.isEmpty()


    MyScaffold(
        modifier = modifier,

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                internetEnabled = internetEnabled,
                title = if (!isEditMode) stringResource(id = R.string.trips)
                        else stringResource(id = R.string.edit_trips),

                actionIcon2 = if (!isEditMode && !loadingTrips) TopAppBarIcon.edit else null,
                actionIcon2Onclick = {
                    setEditMode(null)
                },
                startPadding = spacerValue
            )
        },

        //fab
        floatingActionButtonPosition = FabPosition.Center,
        glanceSpot = {
            GlanceSpot(
                visible = glance.visible && !isEditMode,
                dateTimeFormat = dateTimeFormat,
                trip = glance.trip ?: Trip(id = 0, managerId = ""), //if glanceVisible is true, glanceTrip, Date, Spot is not null
                date = glance.date ?: Date(date = LocalDate.now()),
                spot = glance.spot ?: Spot(id= 0, date = LocalDate.now()),
                onClick = {
                    //go to spot screen
                    navigate.navigateToGlanceSpot()
                },
            )
        },

        //bottom save cancel button
        bottomSaveCancelBarVisible = isEditMode,
        useBottomNavBar = useBottomNavBar,
        onCancelClick = { navigate.onBackButtonClick() },
        onSaveClick = {
            tripsEdit.saveTrips()

            image.organizeAddedDeletedImages(true)
        }
    ) { paddingValues ->

        if(showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = showExitDialogToFalse,
                onDeleteClick = {
                    showExitDialogToFalse()
                    setEditMode(false)
                    tripsEdit.unSaveTempTrips()
                    image.organizeAddedDeletedImages(false)
                }
            )
        }





        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            //display trips list (my trips + shared trips)
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(spacerValue, 16.dp, spacerValue, 200.dp),
                modifier = modifier
                    .fillMaxSize()
//                .padding(top = paddingValues.calculateTopPadding())
                    .padding(paddingValues)
                    .navigationBarsPadding()
                    .onSizeChanged {
                        with(density) {
                            lazyColumnHeightDp = it.height.toDp().value.toInt()
                        }
                    }
            ) {
                item {
                    GoogleBannerAd(
                        internetEnabled = internetEnabled,
                        adView = adView
                    )
                }


                if (!tripsIsEmpty) {

                    //each my trip item ================================================================
                    if (showingTrips.isNotEmpty()) {
                        item {
                            LaunchedEffect(Unit) {
                                firstLaunchToFalse()
                            }

                            Text(
                                text = stringResource(id = R.string.my_trips),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
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
                                    tripsEdit.onDeleteTrip(trip)
                                    showDeleteDialog = false
                                    image.addDeletedImages(trip.getAllImagesPath())
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
                                    navigate.navigateToTrip(false, it)
                                },
                                onLongClick = {
                                    showDeleteDialog = true
                                },
                                downloadImage = image.downloadImage,
                                slideState = slideStates[trip.id] ?: SlideState.NONE,
                                updateSlideState = { tripId, newSlideState ->
                                    slideStates[showingTrips[tripId].id] = newSlideState
                                },
                                updateItemPosition = { currentIndex, destinationIndex ->
                                    updateTripItemOrder(false, currentIndex, destinationIndex)

                                    //all slideState to NONE
                                    slideStates.putAll(showingTrips.map { it.id }
                                        .associateWith { SlideState.NONE })
                                }
                            )
                        }
                    }

                    //each shared trip item ================================================================
                    if (showingSharedTrips.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.shared_trips),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
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
                                    tripsEdit.onDeleteTrip(sharedTrip)
                                    showDeleteDialog = false
                                    image.addDeletedImages(sharedTrip.getAllImagesPath())
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
                                    navigate.navigateToTrip(false, it)
                                },
                                onLongClick = {
                                    showDeleteDialog = true
                                },
                                downloadImage = image.downloadImage,
                                slideState = sharedTripsSlideStates[sharedTrip.id]
                                    ?: SlideState.NONE,
                                updateSlideState = { tripId, newSlideState ->
                                    sharedTripsSlideStates[showingTrips[tripId].id] = newSlideState
                                },
                                updateItemPosition = { currentIndex, destinationIndex ->
                                    updateTripItemOrder(true, currentIndex, destinationIndex)

                                    //all slideState to NONE
                                    sharedTripsSlideStates.putAll(showingTrips.map { it.id }
                                        .associateWith { SlideState.NONE })
                                }
                            )
                        }
                    }
                } else {
                    item {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            NoTripCard(!loadingTrips || !firstLaunch)
                        }
                    }
                }

                item {
                    NewTripButton(
                        visible = !loadingTrips && !isEditMode && showingTrips.size < 50,
                        onClick = {
                            setIsLoadingTrips(true)
                            navigate.navigateToTrip(true, null)
                        }
                    )
                }
            }

            LoadingTripsItem(
                    shown = loadingTrips && tripsIsEmpty,
            modifier = Modifier
                .padding(spacerValue, 16.dp, spacerValue, 0.dp)
                .padding(paddingValues)
            )
        }
    }
}









































@PreviewLightDark
@Composable
private fun TripsScreenPreview_Default(){
    SomewhereTheme {
        val context = LocalContext.current

        TripsScreen(
            spacerValue = 16.dp,
            dateTimeFormat = DateTimeFormat(),
            internetEnabled = true,
            useBottomNavBar = true,
            firstLaunch = false,
            firstLaunchToFalse = { },
            loadingTrips = false,
            setIsLoadingTrips = {},
            isEditMode = false,
            setEditMode = { },
            showExitDialog = false,
            showExitDialogToFalse = {},
            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
            tripsInfo = TripsInfo(
                showingTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "trip 1"
                    ),
                    Trip(
                        id = 0,
                        managerId = ""
                    )
                ),
                showingSharedTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "shared trip"
                    )
                ),
                glance = Glance(visible = true),
            ),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),
            updateTripItemOrder = { _, _, _->},
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_Edit(){
    SomewhereTheme {
        val context = LocalContext.current
        TripsScreen(
            spacerValue = 16.dp,
            dateTimeFormat = DateTimeFormat(),
            internetEnabled = true,
            useBottomNavBar = true,
            firstLaunch = false,
            firstLaunchToFalse = { },
            loadingTrips = false,
            setIsLoadingTrips = {},
            isEditMode = true,
            setEditMode = { },
            showExitDialog = false,
            showExitDialogToFalse = {},
            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
            tripsInfo = TripsInfo(
                showingTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "trip 1"
                    ),
                    Trip(
                        id = 0,
                        managerId = ""
                    )
                ),
                showingSharedTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "shared trip",
                        imagePathList = listOf("")
                    )
                )
            ),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),
            updateTripItemOrder = { _, _, _->},
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_OnClickCancel(){
    SomewhereTheme {
        val context = LocalContext.current
        TripsScreen(
            spacerValue = 16.dp,
            dateTimeFormat = DateTimeFormat(),
            internetEnabled = true,
            useBottomNavBar = true,
            firstLaunch = false,
            firstLaunchToFalse = { },
            loadingTrips = false,
            setIsLoadingTrips = {},
            isEditMode = true,
            setEditMode = { },
            showExitDialog = true,
            showExitDialogToFalse = {},
            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
            tripsInfo = TripsInfo(
                showingTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "trip 1"
                    ),
                    Trip(
                        id = 0,
                        managerId = ""
                    )
                ),
                showingSharedTrips = listOf(
                    Trip(
                        id = 0,
                        managerId = "",
                        titleText = "shared trip",
                        imagePathList = listOf("")
                    )
                )
            ),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),
            updateTripItemOrder = { _, _, _->},
        )
    }
}