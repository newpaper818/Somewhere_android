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
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.component.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.component.GoogleBannerAd
import com.newpaper.somewhere.feature.trip.trips.component.LoadingTripsItem
import com.newpaper.somewhere.feature.trip.trips.component.NoTripCard
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

internal val tripCardHeightDp = 120.dp


@Composable
fun TripsRoute(
    appUserId: String,
    internetEnabled: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,
    isEditMode: Boolean,
    setEditMode: (editMode: Boolean?) -> Unit,

    spacerValue: Dp,

    lazyListState: LazyListState,

    dateTimeFormat: DateTimeFormat,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,

    modifier: Modifier = Modifier,
    tripsViewModel: TripsViewModel = hiltViewModel()
){
    val context = LocalContext.current

    //get trips
    LaunchedEffect(Unit) {
        tripsViewModel.setLoadingTrips(true)

        tripsViewModel.updateTrips(
            internetEnabled = internetEnabled,
            appUserId = appUserId
        )
        tripsViewModel.setLoadingTrips(false)

        tripsViewModel.updateGlance(
            internetEnabled = internetEnabled,
            appUserId = appUserId
        )

        firstLaunchToFalse()
    }

    val adView = AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = if (BuildConfig.DEBUG) AD_UNIT_ID_TEST
                    else AD_UNIT_ID

        loadAd(AdRequest.Builder().build())
    }

    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()
    val originalTrips = tripsUiState.trips.trips ?: listOf()
    val tempTrips = tripsUiState.trips.tempTrips ?: listOf()

    val originalSharedTrips = tripsUiState.trips.sharedTrips ?: listOf()
    val tempSharedTrips = tripsUiState.trips.tempSharedTrips ?: listOf()

    val showingTrips =
        if (isEditMode) tempTrips
        else            originalTrips

    val showingSharedTrips =
        if (isEditMode) tempSharedTrips
        else            originalSharedTrips

    val coroutineScope = rememberCoroutineScope()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBackButtonClick = {
        if (originalTrips != tempTrips || originalSharedTrips != tempSharedTrips)
            showExitDialog = true
        else
            setEditMode(false)
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onBackButtonClick()
        }



    TripsScreen(
        firstLaunch = firstLaunch,
        spacerValue = spacerValue,
        isEditMode = isEditMode,
        lazyListState = lazyListState,
        dateTimeFormat = dateTimeFormat,
        adView = adView,
        internetEnabled = internetEnabled,
        loadingTrips = tripsUiState.loadingTrips,
        showExitDialog = showExitDialog,
        showExitDialogToFalse = { showExitDialog = false },

        showingTrips = showingTrips,
        showingSharedTrips = showingSharedTrips,
        glance = tripsUiState.glance,
        onBackButtonClick = onBackButtonClick,
        setEditMode = setEditMode,
        onDeleteTrip = {
            tripsViewModel.deleteTrip(
                trip = it,
                appUserId = appUserId
            )
       },
        addDeletedImages = { tripsViewModel.addDeletedImages(it) },
        organizeAddedDeletedImages = { tripsViewModel.organizeAddedDeletedImages(
            tripManagerId = appUserId,
            isClickSave = it,
            isInTripsScreen = true
        ) },
        saveTrips = {
            coroutineScope.launch {
                tripsViewModel.saveTrips(
                    appUserId = appUserId,
                    editModeToFalse = { setEditMode(false) }
                )
            }
        },
        unSaveTempTrips = { tripsViewModel.unSaveTempTrips() },
        navigateToTrip = navigateToTrip,
        navigateToGlanceSpot = { navigateToGlanceSpot(tripsUiState.glance) },
        updateItemOrder = tripsViewModel::reorderTempTrips,
        downloadImage = tripsViewModel::getImage,
        setIsLoadingTrips = {
            tripsViewModel.setLoadingTrips(it)
        },
        modifier = modifier
    )
}




@Composable
private fun TripsScreen(
    firstLaunch: Boolean,

    spacerValue: Dp,
    isEditMode: Boolean,
    lazyListState: LazyListState,
    dateTimeFormat: DateTimeFormat,

    adView: AdView,

    internetEnabled: Boolean,
    loadingTrips: Boolean,
    showExitDialog: Boolean,
    showExitDialogToFalse: () -> Unit,

    showingTrips: List<Trip>,
    showingSharedTrips: List<Trip>,
    glance: Glance,

    onBackButtonClick: () -> Unit,
    setEditMode: (editMode: Boolean?) -> Unit,

    onDeleteTrip: (trip: Trip) -> Unit,

    addDeletedImages: (imageFiles: List<String>) -> Unit,
    organizeAddedDeletedImages: (isClickSave: Boolean) -> Unit,

    saveTrips: () -> Unit,
    unSaveTempTrips: () -> Unit,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip?) -> Unit,
    navigateToGlanceSpot: () -> Unit,

    updateItemOrder: (isSharedTrips: Boolean, currentIndex: Int, destinationIndex: Int) -> Unit,

    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,

    setIsLoadingTrips: (loadingTrips: Boolean) -> Unit,

    modifier: Modifier = Modifier
){

    val density = LocalDensity.current
    var lazyColumnHeightDp by rememberSaveable { mutableIntStateOf(0) }

    val slideStates = remember { mutableStateMapOf(
        *showingTrips.map { it.id to SlideState.NONE }.toTypedArray()
    ) }

    val sharedTripsSlideStates = remember { mutableStateMapOf(
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
                    setEditMode(null)
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
                trip = glance.trip ?: Trip(id = 0, managerId = ""), //if glanceVisible is true, glanceTrip, Date, Spot is not null
                date = glance.date ?: Date(date = LocalDate.now()),
                spot = glance.spot ?: Spot(id= 0, date = LocalDate.now()),
                onClick = {
                    //go to spot screen
                    navigateToGlanceSpot()
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
                onDismissRequest = showExitDialogToFalse,
                onDeleteClick = {
                    showExitDialogToFalse()
                    setEditMode(false)
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
            item {
                GoogleBannerAd(
                    internetEnabled = internetEnabled,
                    adView = adView
                )
            }

            if (showingTrips.isNotEmpty() || showingSharedTrips.isNotEmpty()) {

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
                                onDeleteTrip(trip)
                                showDeleteDialog = false
                                addDeletedImages(trip.getAllImagesPath())
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
                            },
                            downloadImage = downloadImage,
                            slideState = slideStates[trip.id] ?: SlideState.NONE,
                            updateSlideState = { tripId, newSlideState ->
                                slideStates[showingTrips[tripId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                updateItemOrder(false, currentIndex, destinationIndex)

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
                                onDeleteTrip(sharedTrip)
                                showDeleteDialog = false
                                addDeletedImages(sharedTrip.getAllImagesPath())
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
                            },
                            downloadImage = downloadImage,
                            slideState = sharedTripsSlideStates[sharedTrip.id] ?: SlideState.NONE,
                            updateSlideState = { tripId, newSlideState ->
                                sharedTripsSlideStates[showingTrips[tripId].id] = newSlideState
                            },
                            updateItemPosition = { currentIndex, destinationIndex ->
                                updateItemOrder(true, currentIndex, destinationIndex)

                                //all slideState to NONE
                                sharedTripsSlideStates.putAll(showingTrips.map { it.id }
                                    .associateWith { SlideState.NONE })
                            }
                        )
                    }
                }
            }
            else {
                if (!loadingTrips || !firstLaunch) {
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









































@PreviewLightDark
@Composable
private fun TripsScreenPreview_Default(){
    SomewhereTheme {
        val context = LocalContext.current

        TripsScreen(
            firstLaunch = false,
            spacerValue = 16.dp,
            isEditMode = false,
            lazyListState = LazyListState(),
            dateTimeFormat = DateTimeFormat(),
            adView =  AdView(context).apply {},
            internetEnabled = true,
            loadingTrips = false,
            showExitDialog = false,
            showExitDialogToFalse = {},
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
            onBackButtonClick = { },
            setEditMode = { },
            onDeleteTrip = { },
            addDeletedImages = { },
            organizeAddedDeletedImages = { },
            navigateToGlanceSpot = { },
            saveTrips = { },
            unSaveTempTrips = { },
            navigateToTrip = {_,_->},
            updateItemOrder = { _, _,_->},
            downloadImage = {_,_,_ ->},
            setIsLoadingTrips = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_Edit(){
    SomewhereTheme {
        val context = LocalContext.current

        TripsScreen(
            firstLaunch = false,
            spacerValue = 16.dp,
            isEditMode = true,
            lazyListState = LazyListState(),
            dateTimeFormat = DateTimeFormat(),
            adView =  AdView(context).apply {},
            internetEnabled = true,
            loadingTrips = false,
            showExitDialog = false,
            showExitDialogToFalse = {},
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
            ),
            glance = Glance(),
            onBackButtonClick = { },
            setEditMode = { },
            onDeleteTrip = { },
            addDeletedImages = { },
            organizeAddedDeletedImages = { },
            navigateToGlanceSpot = { },
            saveTrips = { },
            unSaveTempTrips = { },
            navigateToTrip = {_,_->},
            updateItemOrder = { _, _,_->},
            downloadImage = {_,_,_ ->},
            setIsLoadingTrips = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_OnClickCancel(){
    SomewhereTheme {
        val context = LocalContext.current

        TripsScreen(
            firstLaunch = false,
            spacerValue = 16.dp,
            isEditMode = true,
            lazyListState = LazyListState(),
            dateTimeFormat = DateTimeFormat(),
            adView =  AdView(context).apply {},
            internetEnabled = true,
            loadingTrips = false,
            showExitDialog = true,
            showExitDialogToFalse = {},
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
            ),
            glance = Glance(),
            onBackButtonClick = { },
            setEditMode = { },
            onDeleteTrip = { },
            addDeletedImages = { },
            organizeAddedDeletedImages = { },
            navigateToGlanceSpot = { },
            saveTrips = { },
            unSaveTempTrips = { },
            navigateToTrip = {_,_->},
            updateItemOrder = { _, _,_->},
            downloadImage = {_,_,_ ->},
            setIsLoadingTrips = {}
        )
    }
}