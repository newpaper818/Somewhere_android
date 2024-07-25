package com.newpaper.somewhere.feature.trip.invitedFriend

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.trip.InviteFriendRepository
import com.newpaper.somewhere.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class InvitedFriendUiState(
    val loading: Boolean = true,

    val friendList: List<UserData> = listOf(),

    val selectedFriendUserData: UserData? = null,

    var showDeleteFriendDialog: Boolean = false,
    val showSetEditableDialog: Boolean = false,
    val showGetOutSharedTripDialog: Boolean = false
)

@HiltViewModel
    class InvitedFriendViewModel @Inject constructor(
        private val inviteFriendRepository: InviteFriendRepository,
        private val getImageRepository: GetImageRepository
): ViewModel() {
    private val _invitedFriendUiState = MutableStateFlow(InvitedFriendUiState())
    val invitedFriendUiState = _invitedFriendUiState.asStateFlow()


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





    fun setLoading(
        loading: Boolean
    ){
        _invitedFriendUiState.update {
            it.copy(loading = loading)
        }
    }

    fun setFriendList(
        friendList: List<UserData>
    ){
        _invitedFriendUiState.update {
            it.copy(friendList = friendList)
        }
    }

    fun setSelectedFriendUserData(
        selectedFriendUserData: UserData?
    ){
        _invitedFriendUiState.update {
            it.copy(selectedFriendUserData = selectedFriendUserData)
        }
    }

    fun setShowDeleteFriendDialog(
        showDeleteFriendDialog: Boolean
    ){
        _invitedFriendUiState.update {
            it.copy(showDeleteFriendDialog = showDeleteFriendDialog)
        }
    }

    fun setShowSetEditableDialog(
        showSetEditableDialog: Boolean
    ){
        _invitedFriendUiState.update {
            it.copy(showSetEditableDialog = showSetEditableDialog)
        }
    }

    fun setShowGetOutSharedTripDialog(
        showGetOutSharedTripDialog: Boolean
    ){
        _invitedFriendUiState.update {
            it.copy(showGetOutSharedTripDialog = showGetOutSharedTripDialog)
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






    fun deleteFriendFromTrip(
        tripId: Int,
        tripManagerId: String,
        friendUserId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        inviteFriendRepository.deleteFriendFromTrip(
            tripId = tripId,
            tripManagerId = tripManagerId,
            friendUserId = friendUserId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun setFriendPermission(
        tripId: Int,
        myUserId: String,
        friendUserData: UserData,

        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        inviteFriendRepository.setFriendPermission(
            tripId = tripId,
            myUserId = myUserId,
            friendUserData = friendUserData,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}