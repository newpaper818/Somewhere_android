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
    //friend user info with invite card
    val friendInfoWithInviteCardVisible: Boolean = false,
    val friendUserData: UserData? = null,
    val isEditable: Boolean = false, //true: allow edit / false: view only
    val inviteButtonEnabled: Boolean = false,

    //qr or email
    val isInviteWithQr: Boolean = true, //false: invite with email

    //search friend available
    val searchFriendAvailable: Boolean = true,

    //email text
    val friendEmailText: String = "",
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

    fun setIsAllowEdit(
        isAllowEdit: Boolean
    ) {
        _inviteFriendUiState.update {
            it.copy(isEditable = isAllowEdit)
        }
    }

    fun setIsInviteWithQr(
        inviteWithQr: Boolean
    ) {
        _inviteFriendUiState.update {
            it.copy(
                isInviteWithQr = inviteWithQr
            )
        }
    }

    fun setFriendUserData(
        friendInfo: UserData
    ){
        _inviteFriendUiState.update {
            it.copy(friendUserData = friendInfo)
        }
    }

    fun setFriendInfoWithInviteCardVisible(
        isVisible: Boolean
    ) {
        _inviteFriendUiState.update {
            it.copy(friendInfoWithInviteCardVisible = isVisible)
        }
    }

    fun setSearchFriendAvailable(
        searchFriendAvailable: Boolean
    ) {
        _inviteFriendUiState.update {
            it.copy(searchFriendAvailable = searchFriendAvailable)
        }
    }



    fun getFriendUserIdFromScannedValue(
        scannedValue: String
    ): String? {
        //somewhere_{userId}

        if (!scannedValue.startsWith("somewhere_")){
            return null
        }
        else {
            val splitScannedValue = scannedValue.split("_")

            return if(splitScannedValue.size == 2 && splitScannedValue[1] != "")
                splitScannedValue[1]
            else
                null
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


    suspend fun getFriendUserData(
        searchByUserId: Boolean, //or email
        friendUserIdOrEmail: String,
        appUserData: UserData,

        onErrorSnackbar: () -> Unit,
        onFriendNotFoundSnackbar: () -> Unit,
        onFriendIsAppUserSnackbar: () -> Unit,
        onInvalidEmailSnackbar: () -> Unit,
    ){
        //empty or not email
        if (!searchByUserId && (friendUserIdOrEmail.isEmpty() || "@" !in friendUserIdOrEmail)){
            onInvalidEmailSnackbar()
            return
        }

        //friend userId or email is app user
        if (searchByUserId && appUserData.userId == friendUserIdOrEmail
            || !searchByUserId && appUserData.email == friendUserIdOrEmail
        ){
            onFriendIsAppUserSnackbar()
            return
        }

        //search friend
        val (friendUserData, success) =
            if (searchByUserId){
                inviteFriendRepository.getFriendUserFromUserId(friendUserIdOrEmail)
            }
            else {
                inviteFriendRepository.getFriendUserFromEmail(friendUserIdOrEmail)
            }

        if (friendUserData != null && success){
            //success to get friend UserData
            setFriendUserData(friendUserData)
            setFriendInfoWithInviteCardVisible(true)
        }
        else if (friendUserData == null && success){
            //friend not found
            onFriendNotFoundSnackbar()
        }
        else {
            //error
            onErrorSnackbar()
        }
    }

    //invite friend to trip
    suspend fun inviteFriend(
        tripId: Int,
        appUserId: String,
        friendUserId: String,
        editable: Boolean,
        onSuccess: () -> Unit,
        onErrorSnackbar: () -> Unit
    ){
        inviteFriendRepository.inviteFriend(
            tripId = tripId,
            appUserId = appUserId,
            friendUserId = friendUserId,
            editable = editable,
            onSuccess = onSuccess,
            onErrorSnackbar = onErrorSnackbar
        )
    }
}