package com.newpaper.somewhere.feature.trip.invitedFriend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.MAX_FRIEND_CNT
import com.newpaper.somewhere.core.utils.convert.setSharingTo
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteFriendDialog
import com.newpaper.somewhere.feature.dialog.deleteOrNot.GetOutSharedTripDialog
import com.newpaper.somewhere.feature.dialog.setEditable.SetEditableDialog
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.invitedFriend.component.AppUserCard
import com.newpaper.somewhere.feature.trip.invitedFriend.component.FriendList
import com.newpaper.somewhere.feature.trip.invitedFriend.component.LoadingCard
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import kotlinx.coroutines.launch

@Composable
fun InvitedFriendsRoute(
    spacerValue: Dp,
    appUserData: UserData,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,
    onDeleteSharedTrip: (trip: Trip) -> Unit,

    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,
    updateTripState: (toTempTrip: Boolean, trip: Trip) -> Unit,

    modifier: Modifier = Modifier,
    invitedFriendViewModel: InvitedFriendViewModel = hiltViewModel()
){
    val invitedFriendUiState by invitedFriendViewModel.invitedFriendUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val deleteFriendErrorText = stringResource(id = R.string.snackbar_delete_friend_error)
    val setEditableErrorText = stringResource(id = R.string.snackbar_set_editable_error)

    val snackBarHostState = remember { SnackbarHostState() }


    //get friends data
    LaunchedEffect(Unit){
        invitedFriendViewModel.getInvitedFriends(
            internetEnabled = internetEnabled,
            tripManagerId = trip.managerId,
            tripId = trip.id,
            onSuccess = { invitedFriendList ->
                //exclude app user
                val newInvitedFriendList = invitedFriendList.filter { it.userId != appUserData.userId }

                //update trip state
                trip.setSharingTo(
                    updateTripState = updateTripState,
                    userDataList = newInvitedFriendList
                )

                invitedFriendViewModel.setFriendList(newInvitedFriendList)
                invitedFriendViewModel.setLoading(false)
            },
            onError = {
                //show snackbar message
                invitedFriendViewModel.setLoading(false)
            }
        )
    }




    if (invitedFriendUiState.showDeleteFriendDialog){
        DeleteFriendDialog(
            onDismissRequest = {
                invitedFriendViewModel.setShowDeleteFriendDialog(false)
            },
            onClickDelete = {
                invitedFriendViewModel.setShowDeleteFriendDialog(false)

                //delete friend from firestore db
                invitedFriendViewModel.deleteFriendFromTrip(
                    tripId = trip.id,
                    tripManagerId = appUserData.userId,
                    friendUserId = invitedFriendUiState.selectedFriendUserData?.userId ?: "",
                    onSuccess = {

                        //update friend list
                        val newFriendList = invitedFriendUiState.friendList.toMutableList()
                        newFriendList.remove(invitedFriendUiState.selectedFriendUserData)
                        invitedFriendViewModel.setFriendList(newFriendList)

                        //update trip state
                        trip.setSharingTo(
                            updateTripState = updateTripState,
                            userDataList = newFriendList
                        )

                        invitedFriendViewModel.setSelectedFriendUserData(null)
                    },
                    onError = {
                        invitedFriendViewModel.setSelectedFriendUserData(null)

                        //show error snackbar
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = deleteFriendErrorText
                            )
                        }
                    }
                )
            }
        )
    }

    if (invitedFriendUiState.showSetEditableDialog){
        if (invitedFriendUiState.selectedFriendUserData != null) {
            SetEditableDialog(
                internetEnabled = internetEnabled,
                friendData = invitedFriendUiState.selectedFriendUserData!!,
                downloadImage = invitedFriendViewModel::getImage,
                onDismissRequest = {
                    invitedFriendViewModel.setShowSetEditableDialog(false)
                    invitedFriendViewModel.setSelectedFriendUserData(null)
                },
                onClickOk = { it ->
                    invitedFriendViewModel.setShowSetEditableDialog(false)

                    val newFriendUserData = invitedFriendUiState.selectedFriendUserData!!.copy(allowEdit = it)

                    invitedFriendViewModel.setFriendList(
                        invitedFriendUiState.friendList.map { friendItem ->
                            if (friendItem == invitedFriendUiState.selectedFriendUserData) newFriendUserData
                            else friendItem
                        }
                    )

                    //save to firestore db
                    invitedFriendViewModel.setFriendPermission(
                        tripId = trip.id,
                        myUserId = appUserData.userId,
                        friendUserData = newFriendUserData,
                        onSuccess = {
                            //do nothing
                        },
                        onError = {
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar(
                                    message = setEditableErrorText
                                )
                            }

                        }
                    )

                    invitedFriendViewModel.setSelectedFriendUserData(null)
                }
            )
        }
        else {
            invitedFriendViewModel.setShowSetEditableDialog(false)
        }
    }

    if (invitedFriendUiState.showGetOutSharedTripDialog) {
        GetOutSharedTripDialog(
            onDismissRequest = {
                invitedFriendViewModel.setShowGetOutSharedTripDialog(false)
            },
            onClickGetOut = {
                invitedFriendViewModel.setShowGetOutSharedTripDialog(false)

                invitedFriendViewModel.deleteFriendFromTrip(
                    tripId = trip.id,
                    tripManagerId = trip.managerId,
                    friendUserId = appUserData.userId,
                    onSuccess = {
                        onDeleteSharedTrip(trip)
                    },
                    onError = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = "error to get out trip"
                            )
                        }
                    }
                )
            }
        )
    }




    InvitedFriendsScreen(
        spacerValue = spacerValue,
        snackBarHostState = snackBarHostState,
        appUserIsTripManager = appUserData.userId == trip.managerId,
        appUserData = appUserData,
        internetEnabled = internetEnabled,
        trip = trip,
        dateTimeFormat = dateTimeFormat,
        friendList = invitedFriendUiState.friendList,
        loading = invitedFriendUiState.loading,
        setShowGetOutSharedTripDialog = invitedFriendViewModel::setShowGetOutSharedTripDialog,
        setShowSetEditableDialog = invitedFriendViewModel::setShowSetEditableDialog,
        setShowDeleteFriendDialog = invitedFriendViewModel::setShowDeleteFriendDialog,
        setSelectedFriendUserData = invitedFriendViewModel::setSelectedFriendUserData,
        downloadImage = invitedFriendViewModel::getImage,
        navigateUp = navigateUp,
        navigateToInviteFriend = navigateToInviteFriend,
        modifier = modifier
    )


}

@Composable
private fun InvitedFriendsScreen(
    spacerValue: Dp,
    snackBarHostState: SnackbarHostState,

    appUserIsTripManager: Boolean,
    appUserData: UserData,
    internetEnabled: Boolean,

    trip: Trip,

    dateTimeFormat: DateTimeFormat,

    friendList: List<UserData>,
    loading: Boolean,

    setShowGetOutSharedTripDialog: (Boolean) -> Unit,
    setShowSetEditableDialog: (Boolean) -> Unit,
    setShowDeleteFriendDialog: (Boolean) -> Unit,
    setSelectedFriendUserData: (UserData?) -> Unit,

    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,
    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,

    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier
            .imePadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                internetEnabled = internetEnabled,
                title = stringResource(id = R.string.trip_mates),
                navigationIcon = TopAppBarIcon.back,
                onClickNavigationIcon = { navigateUp() },

                actionIcon1 = if (appUserIsTripManager && friendList.size < MAX_FRIEND_CNT && internetEnabled) TopAppBarIcon.inviteFriend else null,
                actionIcon1Onclick = navigateToInviteFriend
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(spacerValue, 8.dp, spacerValue, 200.dp),
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
                    modifier = Modifier.widthIn(max = itemMaxWidth)
                )
            }

            item{
                Box{
                    //loading screen
                    LoadingCard(
                        isLoading = loading
                    )

                    Column {
                        MySpacerColumn(height = 16.dp)
                        AppUserCard(
                            isLoading = loading,
                            appUserData = appUserData,
                            isManager = appUserIsTripManager,
                            internetEnabled = internetEnabled,
                            downloadImage = downloadImage,
                            onClickGetOut = {
                                setShowGetOutSharedTripDialog(true)
                            }
                        )

                        MySpacerColumn(height = 16.dp)
                        FriendList(
                            isLoading = loading,
                            managerId = trip.managerId,
                            friendList = friendList,
                            showEditButton = appUserIsTripManager,
                            internetEnabled = internetEnabled,
                            downloadImage = downloadImage,
                            onClickEditable = {
                                setShowSetEditableDialog(true)
                                setSelectedFriendUserData(it)
                            },
                            onClickDeleteFriend = {
                                setShowDeleteFriendDialog(true)
                                setSelectedFriendUserData(it)
                            }
                        )
                    }
                }
            }
        }
    }
}








