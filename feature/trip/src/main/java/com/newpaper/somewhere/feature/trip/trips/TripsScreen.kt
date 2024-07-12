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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

internal val tripCardHeightDp = 120.dp


@Composable
fun TripsRoute(
    commonTripViewModel: CommonTripViewModel,
    tripsViewModel: TripsViewModel,

    spacerValue: Dp,
    appUserId: String,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    useBottomNavBar: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,

    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    val tripsUiState by tripsViewModel.tripsUiState.collectAsState()
    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsState()

    val isEditMode = commonTripUiState.isEditMode

    val originalTrips = commonTripUiState.tripInfo.trips ?: listOf()
    val tempTrips = commonTripUiState.tripInfo.tempTrips ?: listOf()

    val originalSharedTrips = commonTripUiState.tripInfo.sharedTrips ?: listOf()
    val tempSharedTrips = commonTripUiState.tripInfo.tempSharedTrips ?: listOf()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        commonTripViewModel.initDateSpotIndex()
    }

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

    LaunchedEffect(tripsUiState.loadingTrips) {
        if (!tripsUiState.loadingTrips && firstLaunch) {
            delay(400)
            firstLaunchToFalse()
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

    val onClickBackButton = {
        if (originalTrips != tempTrips || originalSharedTrips != tempSharedTrips)
            tripsViewModel.setShowExitDialog(true)
        else
            commonTripViewModel.setIsEditMode(false)
    }

    //when back button click
    if (isEditMode)
        BackHandler {
            onClickBackButton()
        }



    TripsScreen(
        appUserId = appUserId,
        tripsUiInfo = TripsUiInfo(
            spacerValue = spacerValue,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            useBottomNavBar = useBottomNavBar,
            firstLaunch = firstLaunch,
            _firstLaunchToFalse = firstLaunchToFalse,
            loadingTrips = tripsUiState.loadingTrips,
            _setIsLoadingTrips = tripsViewModel::setLoadingTrips,
            isEditMode = isEditMode,
            _setIsEditMode = commonTripViewModel::setIsEditMode
        ),
        tripsData = TripsData(
            showingTrips = showingTrips,
            showingSharedTrips = showingSharedTrips,
            glance = tripsUiState.glance,
        ),
        dialog = TripsDialog(
            isShowingDialog = tripsUiState.isShowingDialog,
            showExitDialog = tripsUiState.showExitDialog,
            showDeleteDialog = tripsUiState.showDeleteDialog,
            _showExitDialogToFalse = { tripsViewModel.setShowExitDialog(false) },
            _setShowDeleteDialog = tripsViewModel::setShowDeleteDialog,
            selectedTrip = tripsUiState.selectedTrip,
            _setSelectedTrip = tripsViewModel::setSelectedTrip,
        ),
        image = TripsImage(
            _downloadImage = commonTripViewModel::getImage,
            _addDeletedImages = { commonTripViewModel.addDeletedImages(it) },
            _organizeAddedDeletedImages = { commonTripViewModel.organizeAddedDeletedImages(
                tripManagerId = appUserId,
                isClickSave = it,
                isInTripsScreen = true
            ) }
        ),
        tripsEdit = TripsEdit(
            _saveTrips = {
                coroutineScope.launch {
                    tripsViewModel.saveTrips(
                        appUserId = appUserId
                    )
                }
            },
            _unSaveTempTrips = { commonTripViewModel.unSaveTempTrips() },
            _onDeleteTrip = {
                tripsViewModel.deleteTrip(
                    trip = it,
                    appUserId = appUserId
                )
            }
        ),
        navigate = TripsNavigate(
            _onClickBackButton = onClickBackButton,
            _navigateToTrip = { isNewTrip, trip ->
                if (isNewTrip && trip == null) {
                    val newTrip = tripsViewModel.addAndGetNewTrip(appUserId)
                    navigateToTrip(true, newTrip)
                }
                else
                    navigateToTrip(isNewTrip, trip!!)
            },
            _navigateToGlanceSpot = { navigateToGlanceSpot(tripsUiState.glance) },
        ),

        lazyListState = lazyListState,
        adView = adView,
        updateTripItemOrder = tripsViewModel::reorderTempTrips,
        modifier = modifier
    )
}









@Composable
private fun TripsScreen(
    appUserId: String,

    tripsUiInfo: TripsUiInfo,
    tripsData: TripsData,
    dialog: TripsDialog,
    image: TripsImage,
    tripsEdit: TripsEdit,
    navigate: TripsNavigate,

    lazyListState: LazyListState,
    adView: AdView,
    updateTripItemOrder: (isSharedTrips: Boolean, currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier
){
    val spacerValue = tripsUiInfo.spacerValue
    val dateTimeFormat = tripsUiInfo.dateTimeFormat
    val internetEnabled = tripsUiInfo.internetEnabled
    val useBottomNavBar = tripsUiInfo.useBottomNavBar
    val firstLaunch = tripsUiInfo.firstLaunch
    val loadingTrips = tripsUiInfo.loadingTrips
    val isEditMode = tripsUiInfo.isEditMode

    val showingTrips = tripsData.showingTrips
    val showingSharedTrips = tripsData.showingSharedTrips
    val glance = tripsData.glance

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
                    tripsUiInfo.setIsEditMode(true)
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
        bottomSaveCancelBarVisible = isEditMode && !dialog.isShowingDialog,
        useBottomNavBar = useBottomNavBar,
        onClickCancel = { navigate.onClickBackButton() },
        onClickSave = {
            tripsEdit.saveTrips()

            image.organizeAddedDeletedImages(true)
        }
    ) { paddingValues ->

        //dialogs
        if(dialog.showExitDialog){
            DeleteOrNotDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                deleteButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { dialog.showExitDialogToFalse() },
                onClickDelete = {
                    dialog.showExitDialogToFalse()
                    tripsUiInfo.setIsEditMode(false)
                    tripsEdit.unSaveTempTrips()
                    image.organizeAddedDeletedImages(false)
                }
            )
        }

        if (dialog.showDeleteDialog && dialog.selectedTrip != null) {
            val isSharedTrip = dialog.selectedTrip.managerId != appUserId

            val titleText = if (isSharedTrip)
                stringResource(id = R.string.dialog_title_leave_shared_trip)
                else stringResource(id = R.string.dialog_title_delete_trip)

            val subBodyText = if (isSharedTrip || dialog.selectedTrip.sharingTo.isEmpty()) null
            else stringResource(id = R.string.dialog_sub_body_delete_trip)

            val deleteText = if (isSharedTrip) stringResource(id = R.string.dialog_button_leave)
                            else stringResource(id = R.string.dialog_button_delete)

            DeleteOrNotDialog(
                width = null,
                titleText = titleText,
                subBodyText = subBodyText,
                deleteButtonText = deleteText,
                bodyContent = {
                    TripItem(
                        trip = dialog.selectedTrip,
                        internetEnabled = internetEnabled,
                        dateTimeFormat = dateTimeFormat,
                        downloadImage = image::downloadImage,
                    )
                },
                onDismissRequest = { dialog.setShowDeleteDialog(false) },
                onClickDelete = {
                    tripsEdit.onDeleteTrip(dialog.selectedTrip)
                    dialog.setShowDeleteDialog(false)
                    image.addDeletedImages(dialog.selectedTrip.getAllImagesPath())
                    dialog.setSelectedTrip(null)
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

                        key(showingTrips) {
                            TripItem(
                                showDragIcon = isEditMode,
                                internetEnabled = internetEnabled,
                                dateTimeFormat = dateTimeFormat,
                                firstLaunch = firstLaunch,
                                trip = trip,
                                trips = showingTrips,
                                onClick = if (!isEditMode) { {
                                    tripsUiInfo.setIsLoadingTrips(true)
                                    navigate.navigateToTrip(false, trip)
                                } }
                                else null,
                                onLongClick = if (isEditMode) { {
                                    dialog.setSelectedTrip(trip)
                                    dialog.setShowDeleteDialog(true)
                                } }
                                else null,
                                downloadImage = image::downloadImage,
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
                        key(showingSharedTrips) {
                            TripItem(
                                showDragIcon = isEditMode,
                                internetEnabled = internetEnabled,
                                dateTimeFormat = dateTimeFormat,
                                firstLaunch = firstLaunch,
                                trip = sharedTrip,
                                trips = showingSharedTrips,
                                onClick = if (!isEditMode) { {
                                    tripsUiInfo.setIsLoadingTrips(true)
                                    navigate.navigateToTrip(false, sharedTrip)
                                } }
                                else null,
                                onLongClick = if (isEditMode) { {
                                    dialog.setSelectedTrip(sharedTrip)
                                    dialog.setShowDeleteDialog(true)
                                } }
                                else null,
                                downloadImage = image::downloadImage,
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
                            tripsUiInfo.setIsLoadingTrips(true)
                            navigate.navigateToTrip(true, null)
                        }
                    )
                }
            }

            LoadingTripsItem(
                shown = loadingTrips && firstLaunch,
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
            appUserId = "",
            tripsUiInfo = TripsUiInfo(
                firstLaunch = false,
            ),
            tripsData = TripsData(
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
            dialog = TripsDialog(),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),

            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
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
            appUserId = "",
            tripsUiInfo = TripsUiInfo(
                firstLaunch = false,
                isEditMode = true
            ),
            tripsData = TripsData(
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
            dialog = TripsDialog(),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),

            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
            updateTripItemOrder = { _, _, _->}
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_OnClickCancel(){
    SomewhereTheme {
        val context = LocalContext.current
        TripsScreen(
            appUserId = "",
            tripsUiInfo = TripsUiInfo(
                firstLaunch = false,
                isEditMode = true
            ),
            tripsData = TripsData(
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
            dialog = TripsDialog(
                showExitDialog = true
            ),
            image = TripsImage(),
            tripsEdit = TripsEdit(),
            navigate = TripsNavigate(),

            lazyListState = LazyListState(),
            adView =  AdView(context).apply {},
            updateTripItemOrder = { _, _, _->}
        )
    }
}