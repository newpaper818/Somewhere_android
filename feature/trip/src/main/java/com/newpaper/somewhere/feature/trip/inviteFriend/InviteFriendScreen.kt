package com.newpaper.somewhere.feature.trip.inviteFriend

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.button.InviteButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.MyTextField
import com.newpaper.somewhere.core.ui.card.ShareAppCard
import com.newpaper.somewhere.core.ui.selectSwitch.AllowEditViewSelectSwitch
import com.newpaper.somewhere.core.utils.PLAY_STORE_URL
import com.newpaper.somewhere.core.utils.convert.setSharingTo
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.trips.component.TripItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

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

    LaunchedEffect(Unit) {
        inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
    }

    LaunchedEffect(internetEnabled, inviteFriendUiState.inviteButtonEnabled){
        if (!inviteFriendUiState.inviteButtonEnabled){
            //block click for 1.5 sec if user click invite button
            delay(1500)
            inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
        } else {
            inviteFriendViewModel.setInviteButtonEnabled(internetEnabled)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val shareTripErrorText = stringResource(id = R.string.snackbar_invite_friend_error)
    val sameEmailText = stringResource(id = R.string.snackbar_same_email)
    val invalidEmailText = stringResource(id = R.string.snackbar_invalid_email)
    val noUserText = stringResource(id = R.string.snackbar_no_user)
    val shareTripSuccessText = stringResource(id = R.string.snackbar_invite_friend_success)

    val focusManager = LocalFocusManager.current


    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, PLAY_STORE_URL)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    InviteFriendScreen(
        spacerValue = spacerValue,
        snackBarHostState = snackBarHostState,
        internetEnabled = internetEnabled,
        dateTimeFormat = dateTimeFormat,
        trip = trip,
        inviteButtonEnabled = inviteFriendUiState.inviteButtonEnabled,
        friendEmailText = inviteFriendUiState.friendEmailText,
        setFriendEmailText = inviteFriendViewModel::setFriendEmailText,
        isEditable = inviteFriendUiState.isEditable,
        setIsEditable = inviteFriendViewModel::setIsEditable,
        onClickInviteButton = {
            inviteFriendViewModel.setInviteButtonEnabled(false)
            coroutineScope.launch {
                inviteFriendViewModel.checkAndAddSharingTripFriend(
                    tripId = trip.id,
                    myEmail = appUserData.email ?: "",
                    myUserId = appUserData.userId,
                    friendUserEmail = inviteFriendUiState.friendEmailText.replace(" ", ""),
                    editable = inviteFriendUiState.isEditable,
                    onSuccess = {
                        focusManager.clearFocus()
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
                                    val newInvitedFriendList = invitedFriendList.filter { it.userId != appUserData.userId }

                                    //update trip state
                                    trip.setSharingTo(
                                        updateTripState = updateTripState,
                                        userDataList = newInvitedFriendList
                                    )
                                },
                                onError = { }
                            )
                        }
                    },
                    onErrorSnackbar = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = shareTripErrorText)
                        }
                    },
                    onMyEmailSnackbar = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = sameEmailText)
                        }
                    },
                    onInvalidEmailSnackbar = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = invalidEmailText)
                        }
                    },
                    onNoUserSnackbar = {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = noUserText)
                        }
                    }
                )
            }
        },
        onClickShareApp = {
            context.startActivity(shareIntent)
        },
        downloadImage = inviteFriendViewModel::getImage,
        navigateUp = navigateUp,
        modifier = modifier
    )
}

@Composable
private fun InviteFriendScreen(
    spacerValue: Dp,
    snackBarHostState: SnackbarHostState,

    internetEnabled: Boolean,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,

    inviteButtonEnabled: Boolean,

    friendEmailText: String,
    setFriendEmailText: (String) -> Unit,

    isEditable: Boolean,
    setIsEditable: (Boolean) -> Unit,

    onClickInviteButton: () -> Unit,

    onClickShareApp: () -> Unit,

    downloadImage: (imagePath: String, tripManagerId: String, (Boolean) -> Unit) -> Unit,
    navigateUp: () -> Unit,

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
                title = stringResource(id = R.string.invite_friend),
                navigationIcon = TopAppBarIcon.back,
                onClickNavigationIcon = { navigateUp() }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.width(500.dp)
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
                    modifier = Modifier.widthIn(max = itemMaxWidthSmall)
                )
            }

            item {
                //email text field
                EmailTextField(
                    emailText = friendEmailText,
                    onEmailTextChange = setFriendEmailText
                )
            }

            item {
                //view only / allow edit
                AllowEditViewSelectSwitch(
                    isEditable = isEditable,
                    setIsAllowEdit = setIsEditable
                )
            }

            item {
                //invite button
                InviteButton(
                    enabled = inviteButtonEnabled,
                    onClick = onClickInviteButton
                )
            }

            item{
                MySpacerColumn(height = 16.dp)
                ShareAppCard(
                    onClickShareApp = onClickShareApp
                )
            }
        }
    }
}




@Composable
private fun EmailTextField(
    emailText: String,
    onEmailTextChange: (newEmailText: String) -> Unit
){
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier.widthIn(max = itemMaxWidthSmall)
    ) {

        Text(
            text = stringResource(id = R.string.friend_to_invite),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier
                .widthIn(max = itemMaxWidthSmall)
                .padding(start = 16.dp)
        )

        MySpacerColumn(height = 8.dp)

        //text field
        MyCard(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(48.dp).padding(start = 16.dp)
            ) {
                MyTextField(
                    modifier = Modifier.weight(1f),
                    inputText = if (emailText == "") null else emailText,
                    inputTextStyle = MaterialTheme.typography.bodyLarge,
                    placeholderText = "somewhere@example.com",
                    placeholderTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    onValueChange = {
                        val newText = it.substring(0, min(60, it.length))
                        onEmailTextChange(newText)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    textFieldModifier = Modifier.focusRequester(focusRequester)
                )

                //if texting show x icon
                if (emailText != "")
                    IconButton(
                        onClick = { onEmailTextChange("") }
                    ) {
                        DisplayIcon(icon = MyIcons.clearInputText)
                    }
            }
        }
    }
}