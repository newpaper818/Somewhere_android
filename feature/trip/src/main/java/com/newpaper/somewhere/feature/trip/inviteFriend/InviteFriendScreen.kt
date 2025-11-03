package com.newpaper.somewhere.feature.trip.inviteFriend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getMaxInviteFriends
import com.newpaper.somewhere.core.utils.convert.setSharingTo
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.inviteFriend.component.EmailPage
import com.newpaper.somewhere.feature.trip.inviteFriend.component.FriendInfoWithInviteCard
import com.newpaper.somewhere.feature.trip.inviteFriend.component.QrCodePage
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InviteFriendRoute(
    spacerValue: Dp,
    appUserData: UserData,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,

    navigateUp: () -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    modifier: Modifier = Modifier,
    inviteFriendViewModel: InviteFriendViewModel = hiltViewModel()
){
    val inviteFriendUiState by inviteFriendViewModel.inviteFriendUiState.collectAsState()

    //invite button enabled
    LaunchedEffect(Unit) {
        inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
    }

    LaunchedEffect(internetEnabled, inviteFriendUiState.inviteButtonEnabled){
        if (!inviteFriendUiState.inviteButtonEnabled){
            //block click for 2 sec if user click invite button
            delay(2000)
            inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
        } else {
            inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
        }
    }

    //qr scan ready
    LaunchedEffect(inviteFriendUiState.searchFriendAvailable) {
        delay(2000)
        if (!inviteFriendUiState.isInviteWithQr
            || !inviteFriendUiState.searchFriendAvailable && !inviteFriendUiState.friendInfoWithInviteCardVisible){
            inviteFriendViewModel.setSearchFriendAvailable(true)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val invalidQrCodeText = stringResource(id = R.string.snackbar_invalid_qr_code)
    val searchFriendErrorText = stringResource(id = R.string.snackbar_search_friend_error)
    val inviteFriendErrorText = stringResource(id = R.string.snackbar_invite_friend_error)
    val friendIsAppUserText = stringResource(id = R.string.snackbar_friend_is_me)
    val invalidEmailText = stringResource(id = R.string.snackbar_invalid_email)
    val userNotFoundText = stringResource(id = R.string.snackbar_user_not_found)
    val shareTripSuccessText = stringResource(id = R.string.snackbar_invite_friend_success)

    val haptic = LocalHapticFeedback.current

    InviteFriendScreen(
        spacerValue = spacerValue,
        internetEnabled = internetEnabled,
        dateTimeFormat = dateTimeFormat,
        snackBarHostState = snackBarHostState,
        inviteFriendUiState = inviteFriendUiState,
        trip = trip,
        getFriendUserIdFromScannedValue = inviteFriendViewModel::getFriendUserIdFromScannedValue,
        setIsInviteWithQr = {
            inviteFriendViewModel.setIsInviteWithQr(it)
            inviteFriendViewModel.setFriendInfoWithInviteCardVisible(false)
            inviteFriendViewModel.setSearchFriendAvailable(true)
        },
        setFriendEmailText = inviteFriendViewModel::setFriendEmailText,
        setIsAllowEdit = inviteFriendViewModel::setIsAllowEdit,

        getFriendUserData = { searchByUserId, friendUserIdOrEmail ->
            if (inviteFriendUiState.searchFriendAvailable) {

                //haptic
                if (searchByUserId)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                inviteFriendViewModel.setSearchFriendAvailable(false)

                coroutineScope.launch {
                    if (friendUserIdOrEmail == null){
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = invalidQrCodeText)
                        }
                    }
                    else{
                        inviteFriendViewModel.getFriendUserData(
                            searchByUserId = searchByUserId,
                            friendUserIdOrEmail = friendUserIdOrEmail,
                            appUserData = appUserData,
                            onErrorSnackbar = {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(message = searchFriendErrorText)
                                }
                            },
                            onFriendNotFoundSnackbar = {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(message = userNotFoundText)
                                }
                            },
                            onFriendIsAppUserSnackbar = {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(message = friendIsAppUserText)
                                }
                            },
                            onInvalidEmailSnackbar = {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(message = invalidEmailText)
                                }
                            }
                        )
                    }
                }
            }
        },
        onClickFriendCardCancel = {
            inviteFriendViewModel.setFriendInfoWithInviteCardVisible(false)
            inviteFriendViewModel.setSearchFriendAvailable(true)
        },
        onClickFriendCardInvite = {
            inviteFriendViewModel.setInviteButtonEnabled(false)

            if (inviteFriendUiState.friendUserData != null) {
                coroutineScope.launch {
                    inviteFriendViewModel.inviteFriend(
                        tripId = trip.id,
                        appUserId = appUserData.userId,
                        friendUserId = inviteFriendUiState.friendUserData!!.userId,
                        editable = inviteFriendUiState.isEditable,
                        onSuccess = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = shareTripSuccessText
                                )

                                inviteFriendViewModel.getInvitedFriends(
                                    internetEnabled = internetEnabled,
                                    tripManagerId = trip.managerId,
                                    tripId = trip.id,
                                    onSuccess = { invitedFriendList ->
                                        //exclude app user
                                        val newInvitedFriendList =
                                            invitedFriendList.filter { it.userId != appUserData.userId }

                                        //update trip state
                                        trip.setSharingTo(
                                            updateTripState = updateTripState,
                                            userDataList = newInvitedFriendList
                                        )

                                        inviteFriendViewModel.setFriendInfoWithInviteCardVisible(false)
                                        inviteFriendViewModel.setSearchFriendAvailable(true)

                                        if (newInvitedFriendList.size >= getMaxInviteFriends(appUserData.isUsingSomewherePro))
                                            coroutineScope.launch(Dispatchers.Main) {
                                                navigateUp()
                                            }
                                    },
                                    onError = { }
                                )
                            }
                        },
                        onErrorSnackbar = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(message = inviteFriendErrorText)
                            }
                        }
                    )
                }
            }
        },
        downloadImage = inviteFriendViewModel::getImage,
        navigateUp = navigateUp,
        modifier = modifier
    )
}





@Composable
private fun InviteFriendScreen(
    spacerValue: Dp,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,

    snackBarHostState: SnackbarHostState,
    inviteFriendUiState: InviteFriendUiState,

    trip: Trip,

    getFriendUserIdFromScannedValue: (String) -> String?,
    setIsInviteWithQr: (Boolean) -> Unit,
    setFriendEmailText: (String) -> Unit,
    setIsAllowEdit: (Boolean) -> Unit,
    getFriendUserData: (searchByUserId: Boolean, friendUserIdOrEmail: String?) -> Unit,

    onClickFriendCardCancel: () -> Unit,
    onClickFriendCardInvite: () -> Unit,

    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,
    navigateUp: () -> Unit,

    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .imePadding()
            .displayCutoutPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                title = stringResource(id = R.string.invite_friend),
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = { navigateUp() }
            )
        },
        floatingActionButton = {
            FriendInfoWithInviteCard(
                visible = inviteFriendUiState.friendInfoWithInviteCardVisible,
                internetEnabled = internetEnabled,
                friendUserData = inviteFriendUiState.friendUserData ?: UserData("", "", "", "", listOf()),
                isEditable = inviteFriendUiState.isEditable,
                setIsAllowEdit = setIsAllowEdit,
                inviteButtonEnabled = inviteFriendUiState.inviteButtonEnabled,
                onClickCancel = onClickFriendCardCancel,
                onClickInvite = onClickFriendCardInvite,
                downloadImage = downloadImage
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp).navigationBarsPadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.small
                    )
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(spacerValue, 8.dp, spacerValue, 40.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {
            item {
                //trip image, title, date
                TripItem(
                    trip = trip,
                    internetEnabled = internetEnabled,
                    dateTimeFormat = dateTimeFormat,
                    downloadImage = downloadImage,
                    modifier = Modifier.widthIn(max = itemMaxWidthSmall)
                )
            }

            item {
                //qr or email
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {

                    AnimatedVisibility(
                        visible = inviteFriendUiState.isInviteWithQr,
                        enter = slideInHorizontally(
                            animationSpec = tween(500),
                            initialOffsetX = { -it*2 }
                        ) + fadeIn(tween(500)),
                        exit = slideOutHorizontally(
                            animationSpec = tween(500),
                            targetOffsetX = { -it*2 }
                        ) + fadeOut(tween(500))
                    ) {
                        QrCodePage(
                            internetEnabled = internetEnabled,
                            searchFriendAvailable = inviteFriendUiState.searchFriendAvailable,
                            onScanned = { scannedValue ->
                                val friendUserId: String? = getFriendUserIdFromScannedValue(scannedValue)
                                getFriendUserData(true, friendUserId)
                            },
                            onClickInviteWithEmailButton = { setIsInviteWithQr(false) },
                            modifier = Modifier.height(430.dp)
                        )
                    }
                    AnimatedVisibility(
                        visible = !inviteFriendUiState.isInviteWithQr,
                        enter = slideInHorizontally(
                            animationSpec = tween(500),
                            initialOffsetX = { it*2 }
                        ) + fadeIn(tween(500)),
                        exit = slideOutHorizontally(
                            animationSpec = tween(500),
                            targetOffsetX = { it*2 }
                        ) + fadeOut(tween(500))
                    ) {
                        EmailPage(
                            internetEnabled = internetEnabled,
                            searchFriendAvailable = inviteFriendUiState.searchFriendAvailable,
                            emailText = inviteFriendUiState.friendEmailText,
                            onEmailTextChange = setFriendEmailText,
                            onSearch = {
                                getFriendUserData(false, inviteFriendUiState.friendEmailText)
                            },
                            onClickInviteFriendWithQrCode = { setIsInviteWithQr(true) },
                            modifier = Modifier.height(430.dp)
                        )
                    }
                }
            }
        }
    }
}
