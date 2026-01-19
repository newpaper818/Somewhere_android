package com.newpaper.somewhere.feature.trip.trips

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.button.NewTripExtendedFAB
import com.newpaper.somewhere.core.designsystem.component.button.UpgradeToSomewhereProButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.TripsDisplayMode
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.model.tripData.TripsGroup
import com.newpaper.somewhere.core.ui.GoogleBannerAd
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID
import com.newpaper.somewhere.core.utils.BANNER_AD_UNIT_ID_TEST
import com.newpaper.somewhere.core.utils.convert.getAllImagesPath
import com.newpaper.somewhere.core.utils.convert.getMaxTrips
import com.newpaper.somewhere.core.utils.enterVerticallyDelayForMaxTrips
import com.newpaper.somewhere.core.utils.exitVertically
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteOrLeaveTripDialog
import com.newpaper.somewhere.feature.dialog.deleteOrNot.TwoButtonsDialog
import com.newpaper.somewhere.feature.dialog.newTripType.TripCreationOptionsDialog
import com.newpaper.somewhere.feature.trip.BuildConfig
import com.newpaper.somewhere.feature.trip.CommonTripViewModel
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.component.GlanceSpot
import com.newpaper.somewhere.feature.trip.trips.component.LoadingTripsItem
import com.newpaper.somewhere.feature.trip.trips.component.NoTripCard
import com.newpaper.somewhere.feature.trip.trips.component.TripFilterChipsWithSortOrderButton
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

internal val tripCardHeightDp = 120.dp


@Composable
fun TripsRoute(
    commonTripViewModel: CommonTripViewModel,
    tripsViewModel: TripsViewModel,

    use2Panes: Boolean,
    spacerValue: Dp,
    appUserData: UserData,
    dateTimeFormat: DateTimeFormat,
    internetEnabled: Boolean,

    useBottomNavBar: Boolean,
    firstLaunch: Boolean,
    firstLaunchToFalse: () -> Unit,

    lazyListState: LazyListState,

    navigateToTrip: (isNewTrip: Boolean, trip: Trip) -> Unit,
    navigateToTripAi: () -> Unit,
    navigateToGlanceSpot: (glance: Glance) -> Unit,
    navigateToSubscription: () -> Unit,

    hazeState: HazeState?,

    modifier: Modifier = Modifier
){
    val context = LocalContext.current

    val tripsUiState by tripsViewModel.tripsUiState.collectAsStateWithLifecycle()
    val commonTripUiState by commonTripViewModel.commonTripUiState.collectAsStateWithLifecycle()

    val isEditMode = commonTripUiState.isEditMode

    val myTripsGroup = commonTripUiState.tripInfo.myTripsGroup ?: TripsGroup()
    val tempMyTripsGroup = commonTripUiState.tripInfo.tempMyTripsGroup ?: TripsGroup()

    val sharedTripsGroup = commonTripUiState.tripInfo.sharedTripsGroup ?: TripsGroup()
    val tempSharedTripsGroup = commonTripUiState.tripInfo.tempSharedTripsGroup ?: TripsGroup()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        commonTripViewModel.initDateSpotIndex()
    }

    LaunchedEffect(isEditMode) {
        if (isEditMode){
            delay(400)
            tripsViewModel.initGlanceInfo()
        }
        else {
            if (!firstLaunch) {
                //update glance
                val glanceTripWithEmptyDateList =
                    tripsViewModel.findCurrentDateTripAndUpdateGlanceTrip()

                if (glanceTripWithEmptyDateList != null) {

                    val glanceTrip = commonTripViewModel.updateTrip(
                        internetEnabled = internetEnabled,
                        appUserId = appUserData.userId,
                        tripWithEmptyDateList = glanceTripWithEmptyDateList
                    )

                    tripsViewModel.updateGlanceSpotInfo(
                        //update trip info (only at empty date list - load once)
                        glanceTrip = glanceTrip
                    )
                }
            }
        }
    }

    //get trips
    LaunchedEffect(Unit) {
        if (!isEditMode) {
            tripsViewModel.setLoadingTrips(true)

            //update trips
            tripsViewModel.updateTrips(
                internetEnabled = internetEnabled,
                appUserId = appUserData.userId,
                orderByLatest = tripsUiState.isTripsSortOrderByLatest
            )
            tripsViewModel.setLoadingTrips(false)

            //update glance
            val glanceTripWithEmptyDateList =
                tripsViewModel.findCurrentDateTripAndUpdateGlanceTrip()

            if (glanceTripWithEmptyDateList != null) {

                val glanceTrip = commonTripViewModel.updateTrip(
                    internetEnabled = internetEnabled,
                    appUserId = appUserData.userId,
                    tripWithEmptyDateList = glanceTripWithEmptyDateList
                )

                tripsViewModel.updateGlanceSpotInfo(
                    //update trip info (only at empty date list - load once)
                    glanceTrip = glanceTrip
                )
            }
        }
    }

    LaunchedEffect(tripsUiState.loadingTrips) {
        if (!tripsUiState.loadingTrips && firstLaunch) {
            delay(400)
            firstLaunchToFalse()
        }
    }

    val adSize = if (!use2Panes) AdSize.BANNER
                    else AdSize.FULL_BANNER

    val adView =
        if (!appUserData.isUsingSomewherePro) {
            AdView(context).apply {
                setAdSize(adSize)
                adUnitId = if (BuildConfig.DEBUG) BANNER_AD_UNIT_ID_TEST
                            else BANNER_AD_UNIT_ID

                loadAd(AdRequest.Builder().build())
            }
        }
        else null



    val showingTrips =
        when (tripsUiState.tripsDisplayMode){
            TripsDisplayMode.ALL ->     if (!isEditMode) myTripsGroup.all       else tempMyTripsGroup.all
            TripsDisplayMode.ACTIVE ->  if (!isEditMode) myTripsGroup.active    else tempMyTripsGroup.active
            TripsDisplayMode.PAST ->    if (!isEditMode) myTripsGroup.past      else tempMyTripsGroup.past
        }

    val showingSharedTrips =
        when (tripsUiState.tripsDisplayMode){
            TripsDisplayMode.ALL ->     if (!isEditMode) sharedTripsGroup.all       else tempSharedTripsGroup.all
            TripsDisplayMode.ACTIVE ->  if (!isEditMode) sharedTripsGroup.active    else tempSharedTripsGroup.active
            TripsDisplayMode.PAST ->    if (!isEditMode) sharedTripsGroup.past      else tempSharedTripsGroup.past
        }

    val onClickBackButton = {
        if (myTripsGroup != tempMyTripsGroup || sharedTripsGroup != tempSharedTripsGroup)
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
        appUserData = appUserData,
        tripsUiInfo = TripsUiInfo(
            use2Panes = use2Panes,
            spacerValue = spacerValue,
            dateTimeFormat = dateTimeFormat,
            internetEnabled = internetEnabled,
            useBottomNavBar = useBottomNavBar,
            firstLaunch = firstLaunch,
            _firstLaunchToFalse = firstLaunchToFalse,
            loadingTrips = tripsUiState.loadingTrips,
            _setIsLoadingTrips = tripsViewModel::setLoadingTrips,
            tripsDisplayMode = tripsUiState.tripsDisplayMode,
            _setTripsDisplayMode = tripsViewModel::setTripsDisplayMode,
            isTripsSortOrderByLatest = tripsUiState.isTripsSortOrderByLatest,
            _setIsTripsSortOrderByLatest = tripsViewModel::setIsTripsSortOrderByLatest,
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
            showTripCreationOptionsDialog = tripsUiState.showTripCreationOptionsDialog,
            showExitDialog = tripsUiState.showExitDialog,
            showDeleteDialog = tripsUiState.showDeleteDialog,
            _setShowTripCreationOptionsDialog = tripsViewModel::setShowTripCreationOptionsDialog,
            _showExitDialogToFalse = { tripsViewModel.setShowExitDialog(false) },
            _setShowDeleteDialog = tripsViewModel::setShowDeleteDialog,
            selectedTrip = tripsUiState.selectedTrip,
            _setSelectedTrip = tripsViewModel::setSelectedTrip,
        ),
        image = TripsImage(
            _downloadImage = commonTripViewModel::getImage,
            _addDeletedImages = { commonTripViewModel.addDeletedImages(it) },
            _organizeAddedDeletedImages = { commonTripViewModel.organizeAddedDeletedImages(
                tripManagerId = appUserData.userId,
                isClickSave = it,
                isInTripsScreen = true
            ) }
        ),
        tripsEdit = TripsEdit(
            _saveTrips = {
                coroutineScope.launch {
                    tripsViewModel.saveTrips(
                        appUserId = appUserData.userId
                    )
                }
            },
            _unSaveTempTrips = { commonTripViewModel.unSaveTempTrips() },
            _onDeleteTrip = {
                tripsViewModel.deleteTrip(
                    trip = it,
                    appUserId = appUserData.userId
                )
            }
        ),
        navigate = TripsNavigate(
            _onClickBackButton = onClickBackButton,
            _navigateToTrip = { isNewTrip, trip ->
                if (isNewTrip && trip == null) {
                    val newTrip = tripsViewModel.addAndGetNewTrip(appUserData.userId)
                    navigateToTrip(true, newTrip)
                }
                else
                    navigateToTrip(isNewTrip, trip!!)
            },
            _navigateToTripAi = { navigateToTripAi() },
            _navigateToGlanceSpot = { navigateToGlanceSpot(tripsUiState.glance) },
            _navigateToSubscription = { navigateToSubscription() }
        ),

        lazyListState = lazyListState,
        adView = adView,
        updateTripItemOrder = tripsViewModel::reorderTempTrips,
        hazeState = hazeState,
        modifier = modifier
    )
}









@Composable
private fun TripsScreen(
    appUserData: UserData,

    tripsUiInfo: TripsUiInfo,
    tripsData: TripsData,
    dialog: TripsDialog,
    image: TripsImage,
    tripsEdit: TripsEdit,
    navigate: TripsNavigate,

    lazyListState: LazyListState,
    adView: AdView?,
    updateTripItemOrder: (isSharedTrips: Boolean, currentIndex: Int, destinationIndex: Int) -> Unit,

    hazeState: HazeState?,

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


    val tripsIsEmpty = showingTrips.isEmpty() && showingSharedTrips.isEmpty()

    val itemModifier = Modifier
        .widthIn(max = itemMaxWidth)
        .padding(horizontal = spacerValue)

    val firstItemVisible by remember { derivedStateOf { lazyListState.firstVisibleItemIndex == 0 } }


    MyScaffold(
        modifier = modifier.imePadding(),

        //top app bar
        topBar = {
            SomewhereTopAppBar(
                internetEnabled = internetEnabled,
                title = if (!isEditMode) stringResource(id = R.string.trips)
                        else stringResource(id = R.string.edit_trips),

                actionIcon1 = TopAppBarIcon.edit,
                actionIcon1Onclick = { tripsUiInfo.setIsEditMode(true) },
                actionIcon1Visible = !isEditMode && !loadingTrips && !tripsIsEmpty,
                startPadding = spacerValue,
                hazeState = hazeState
            )
        },

        //fab
        floatingActionButton = {
            NewTripExtendedFAB(
                visible = !loadingTrips && !isEditMode
                        && showingTrips.size < getMaxTrips(appUserData.isUsingSomewherePro),
                onClick = {
                    //to trip creation options dialog
                    dialog.setShowTripCreationOptionsDialog(true)
                },
                expanded = firstItemVisible,
                useBottomNavBar = useBottomNavBar,
                glanceSpotShown = glance.visible,
                use2Panes = tripsUiInfo.use2Panes
            )
        },
        glanceSpot = {
            GlanceSpot(
                uesLongWidth = useBottomNavBar,
                visible = glance.visible && !isEditMode && !loadingTrips,
                dateTimeFormat = dateTimeFormat,
                trip = glance.trip ?: Trip(id = 0, managerId = ""), //if glanceVisible is true, glanceTrip, Date, Spot is not null
                date = glance.date ?: Date(date = LocalDate.now()),
                spot = glance.spot ?: Spot(id= 0, date = LocalDate.now()),
                onClick = {
                    //go to spot screen
                    navigate.navigateToGlanceSpot()
                },
                hazeState = hazeState
            )
        },

        //bottom save cancel button
        bottomSaveCancelBarVisible = isEditMode && !dialog.isShowingDialog,
        useBottomNavBar = useBottomNavBar,
        saveEnabled = internetEnabled,
        onClickCancel = { navigate.onClickBackButton() },
        onClickSave = {
            tripsEdit.saveTrips()

            image.organizeAddedDeletedImages(true)
        }
    ) { paddingValues ->

        //dialogs
        if (dialog.showTripCreationOptionsDialog){
            TripCreationOptionsDialog(
                onDismissRequest = { dialog.setShowTripCreationOptionsDialog(false) },
                onClick = { onClickManual ->  //T: manual / F: ai
                    when (onClickManual){
                        true -> {
                            tripsUiInfo.setIsLoadingTrips(true)
                            navigate.navigateToTrip(true, null)
                            dialog.setShowTripCreationOptionsDialog(false)
                        }
                        false -> {
                            tripsUiInfo.setIsLoadingTrips(true)
                            navigate.navigateToTripAi()
                            dialog.setShowTripCreationOptionsDialog(false)
                        }
                    }
                }
            )
        }

        if (dialog.showExitDialog){
            TwoButtonsDialog(
                bodyText = stringResource(id = R.string.dialog_body_are_you_sure_to_exit),
                positiveButtonText = stringResource(id = R.string.dialog_button_exit),
                onDismissRequest = { dialog.showExitDialogToFalse() },
                onClickPositive = {
                    dialog.showExitDialogToFalse()
                    tripsUiInfo.setIsEditMode(false)
                    tripsEdit.unSaveTempTrips()
                    image.organizeAddedDeletedImages(false)
                }
            )
        }

        if (dialog.showDeleteDialog && dialog.selectedTrip != null) {
            val isSharedTrip = dialog.selectedTrip.managerId != appUserData.userId

            DeleteOrLeaveTripDialog(
                deleteTrip = !isSharedTrip,
                sharingToIsEmpty = dialog.selectedTrip.sharingTo.isEmpty(),
                internetEnabled = internetEnabled,
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



        val stickyOffsetPx by remember(paddingValues.calculateTopPadding(), tripsUiInfo.tripsDisplayMode, tripsUiInfo.isTripsSortOrderByLatest) {
            derivedStateOf {
                val stickyItem = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.contentType == "sticky" }

                val itemOffset = stickyItem?.offset?.toFloat()

                if (itemOffset == null || itemOffset > 0) { 0 }
                else { (-itemOffset).toInt() }
            }
        }





        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {

            val lazyColumnModifier = modifier.fillMaxSize()


            //display trips list (my trips + shared trips)
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(0.dp, 16.dp + paddingValues.calculateTopPadding(), 0.dp, 400.dp),
                modifier = if (hazeState != null) lazyColumnModifier
                    .hazeSource(state = hazeState)
                    .background(MaterialTheme.colorScheme.background)
                            else lazyColumnModifier
            ) {
                if (adView != null) {
                    item {
                        GoogleBannerAd(
                            adView = adView,
                            useFullBanner = tripsUiInfo.use2Panes
                        )

                        MySpacerColumn(height = 8.dp)

                        AnimatedVisibility(
                            visible = !loadingTrips
                                    && !appUserData.isUsingSomewherePro
                                    && showingTrips.size >= getMaxTrips(false),
                            enter = enterVerticallyDelayForMaxTrips,
                            exit = exitVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.maximum_number_of_trips_reached),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                //upgrade to Somewhere Pro button
                                UpgradeToSomewhereProButton(onClick = { navigate.navigateToSubscription() })
                            }
                        }
                    }
                }

                stickyHeader(
                    contentType = "sticky"
                ){
                    TripFilterChipsWithSortOrderButton(
                        spacerValue = spacerValue,
                        tripsDisplayMode = tripsUiInfo.tripsDisplayMode,
                        onClickTripsDisplayMode = tripsUiInfo::setTripsDisplayMode,
                        isOrderByLatest = tripsUiInfo.isTripsSortOrderByLatest,
                        onClickSortOrder = { tripsUiInfo.setIsTripsSortOrderByLatest(!tripsUiInfo.isTripsSortOrderByLatest) },
                        modifier = Modifier
                            .widthIn(max = itemMaxWidth)
                            .offset{ IntOffset(0, stickyOffsetPx) }
                            .zIndex(1f)
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
                                modifier = itemModifier
                                    .height(34.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .wrapContentHeight(Alignment.Bottom)
                            )
                        }
                    }




                    items(
                        items = showingTrips,
                        key = { it.id }
                    ) { trip ->
                        TripItem(
                            trip = trip,
                            internetEnabled = internetEnabled,
                            dateTimeFormat = dateTimeFormat,
                            downloadImage = image::downloadImage,
                            modifier = itemModifier,
                            showDeleteIcon = isEditMode,
                            firstLaunch = firstLaunch,
                            onClick = if (!isEditMode) { {
                                    tripsUiInfo.setIsLoadingTrips(true)
                                    navigate.navigateToTrip(false, trip)
                                } }
                                else null,
                            onClickDelete = {
                                if (isEditMode){
                                    dialog.setSelectedTrip(trip)
                                    dialog.setShowDeleteDialog(true)
                                }
                            },
                        )
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
                                modifier = itemModifier
                                    .height(34.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .wrapContentHeight(Alignment.Bottom)
                            )
                        }
                    }

                    items(
                        items = showingSharedTrips,
                        key = { it.id }
                    ) { sharedTrip ->
                        TripItem(
                            trip = sharedTrip,
                            internetEnabled = internetEnabled,
                            dateTimeFormat = dateTimeFormat,
                            downloadImage = image::downloadImage,
                            modifier = itemModifier,
                            showDeleteIcon = isEditMode,
                            firstLaunch = firstLaunch,
                            onClick = if (!isEditMode) { {
                                    tripsUiInfo.setIsLoadingTrips(true)
                                    navigate.navigateToTrip(false, sharedTrip)
                                } }
                                else null,
                            onClickDelete = {
                                if (isEditMode){
                                    dialog.setSelectedTrip(sharedTrip)
                                    dialog.setShowDeleteDialog(true)
                                }
                            },
                        )

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
            }

            LoadingTripsItem(
                shown = loadingTrips && firstLaunch,
                showAds = adView != null,
                use2Panes = tripsUiInfo.use2Panes,
                modifier = Modifier
                    .padding(spacerValue, 0.dp, spacerValue, 0.dp)
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
            appUserData = UserData("", "", "", "", listOf(), false),
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
            hazeState = rememberHazeState()
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_Edit(){
    SomewhereTheme {
        val context = LocalContext.current
        TripsScreen(
            appUserData = UserData("", "", "", "", listOf(), false),
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
            updateTripItemOrder = { _, _, _->},
            hazeState = rememberHazeState()
        )
    }
}

@PreviewLightDark
@Composable
private fun TripsScreenPreview_OnClickCancel(){
    SomewhereTheme {
        val context = LocalContext.current
        TripsScreen(
            appUserData = UserData("", "", "", "", listOf(), false),
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
            updateTripItemOrder = { _, _, _->},
            hazeState = rememberHazeState()
        )
    }
}