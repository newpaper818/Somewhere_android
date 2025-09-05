package com.newpaper.somewhere.feature.trip.invitedFriend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.button.AddFriendButton
import com.newpaper.somewhere.core.designsystem.component.button.UpgradeToSomewhereProButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getMaxInviteFriends
import com.newpaper.somewhere.core.utils.convert.setSharingTo
import com.newpaper.somewhere.core.utils.itemMaxWidth
import com.newpaper.somewhere.feature.dialog.deleteOrNot.DeleteFriendDialog
import com.newpaper.somewhere.feature.dialog.deleteOrNot.GetOutSharedTripDialog
import com.newpaper.somewhere.feature.dialog.setPermission.SetPermissionDialog
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.invitedFriend.component.AppUserCard
import com.newpaper.somewhere.feature.trip.invitedFriend.component.FriendList
import com.newpaper.somewhere.feature.trip.invitedFriend.component.LoadingCard
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun InvitedFriendsRoute(
    spacerValue: Dp,
    useBlurEffect: Boolean,
    appUserData: UserData,
    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,
    onDeleteSharedTrip: (trip: Trip) -> Unit,

    navigateUp: () -> Unit,
    navigateToInviteFriend: () -> Unit,
    navigateToSubscription: () -> Unit,
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
            SetPermissionDialog(
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
        useBlurEffect = useBlurEffect,
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
        navigateToSubscription = navigateToSubscription,
        modifier = modifier
    )


}

@Composable
private fun InvitedFriendsScreen(
    spacerValue: Dp,
    useBlurEffect: Boolean,

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
    navigateToSubscription: () -> Unit,

    modifier: Modifier = Modifier
) {
    val topAppBarHazeState = if(useBlurEffect) rememberHazeState() else null

    Scaffold(
        modifier = modifier.imePadding(),
        contentWindowInsets = WindowInsets(bottom = 0),

        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                internetEnabled = internetEnabled,
                title = stringResource(id = R.string.trip_mates),
                navigationIcon = TopAppBarIcon.back,
                onClickNavigationIcon = { navigateUp() },
                hazeState = topAppBarHazeState
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp).navigationBarsPadding(),
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(spacerValue, 8.dp + paddingValues.calculateTopPadding(), spacerValue, 200.dp),
                modifier = if (topAppBarHazeState != null) Modifier.fillMaxSize()
                                .hazeSource(state = topAppBarHazeState).background(MaterialTheme.colorScheme.background)
                            else Modifier.fillMaxSize()
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

                item {
                    Box {
                        //loading screen
                        LoadingCard(isLoading = loading)

                        Column {
                            MySpacerColumn(height = 16.dp)


                            //me
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

                            //friends
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



                item {
                    Column(
                        modifier = Modifier.heightIn(min = 200.dp)
                    ) {
                        AnimatedVisibility(
                            visible = !appUserData.isUsingSomewherePro
                                    && appUserIsTripManager
                                    && friendList.size >= getMaxInviteFriends(false),
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(500))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MySpacerColumn(16.dp)

                                Text(
                                    text = stringResource(R.string.upgrade_to_somewhere_pro_to_invite_more_friends),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                MySpacerColumn(4.dp)

                                //upgrade to Somewhere Pro button
                                UpgradeToSomewhereProButton(onClick = navigateToSubscription)
                            }
                        }



                        AnimatedVisibility(
                            visible = appUserData.isUsingSomewherePro
                                    && appUserIsTripManager
                                    && friendList.size >= getMaxInviteFriends(true),
                            enter = fadeIn(tween(500)),
                            exit = fadeOut(tween(500))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.maximum_number_of_invited_friends_reached),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }





            //add friend button
            Column(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding()
            ) {
                AnimatedVisibility(
                    visible = !loading && appUserIsTripManager && friendList.size < getMaxInviteFriends(appUserData.isUsingSomewherePro),
                    enter = slideInVertically(
                        animationSpec = tween(500),
                        initialOffsetY = { (it * 2.5f).toInt() }),
                    exit = slideOutVertically(
                        animationSpec = tween(500),
                        targetOffsetY = { (it * 2.5f).toInt() })
                ) {
                    AddFriendButton(
                        onClick = navigateToInviteFriend,
                        enabled = !loading && appUserIsTripManager && internetEnabled && friendList.size < getMaxInviteFriends(appUserData.isUsingSomewherePro)
                    )
                }
            }
        }
    }
}








