package com.newpaper.somewhere.feature.trip.inviteFriend

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.trip.InviteFriendRepository
import com.newpaper.somewhere.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class InviteFriendUiState(
    val inviteButtonEnabled: Boolean = false,
    val friendEmailText: String = "",
    val isEditable: Boolean = false
)

@HiltViewModel
class InviteFriendViewModel @Inject constructor(
    private val inviteFriendRepository: InviteFriendRepository,
    private val getImageRepository: GetImageRepository
): ViewModel() {

    private val _inviteFriendUiState = MutableStateFlow(InviteFriendUiState())
    val inviteFriendUiState = _inviteFriendUiState.asStateFlow()

    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ) {
        getImageRepository.getImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }


    fun setInviteButtonEnabled(
        inviteFriendEnabled: Boolean
    ){
        _inviteFriendUiState.update {
            it.copy(inviteButtonEnabled = inviteFriendEnabled)
        }
    }

    fun setFriendEmailText(
        friendEmailText: String
    ){
        _inviteFriendUiState.update {
            it.copy(friendEmailText = friendEmailText)
        }
    }

    fun setIsEditable(
        isAllowEdit: Boolean
    ) {
        _inviteFriendUiState.update {
            it.copy(isEditable = isAllowEdit)
        }
    }

    fun getInvitedFriends(
        internetEnabled: Boolean,
        tripManagerId: String,
        tripId: Int,

        onSuccess: (friendList: List<UserData>) -> Unit,
        onError: () -> Unit
    ){
        inviteFriendRepository.getInvitedFriends(
            internetEnabled = internetEnabled,
            tripManagerId = tripManagerId,
            tripId = tripId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    suspend fun checkAndAddSharingTripFriend(
        tripId: Int,
        myEmail: String,
        myUserId: String,
        friendUserEmail: String,
        editable: Boolean,
        onSuccess: () -> Unit,
        onErrorSnackbar: () -> Unit,
        onMyEmailSnackbar: () -> Unit,
        onInvalidEmailSnackbar: () -> Unit,
        onNoUserSnackbar: () -> Unit
    ){
        inviteFriendRepository.checkAndAddSharingTripFriend(
            tripId = tripId,
            myEmail = myEmail,
            myUserId = myUserId,
            friendUserEmail = friendUserEmail,
            editable = editable,
            onSuccess = onSuccess,
            onErrorSnackbar = onErrorSnackbar,
            onMyEmailSnackbar = onMyEmailSnackbar,
            onInvalidEmailSnackbar = onInvalidEmailSnackbar,
            onNoUserSnackbar = onNoUserSnackbar
        )
    }
}