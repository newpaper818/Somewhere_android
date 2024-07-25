package com.newpaper.somewhere.core.data.repository.trip

import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend.InviteFriendRemoteDataSource
import com.newpaper.somewhere.core.model.data.UserData
import javax.inject.Inject

class InviteFriendRepository @Inject constructor(
    private val commonRemoteDataSource: CommonRemoteDataSource,
    private val inviteFriendRemoteDataSource: InviteFriendRemoteDataSource
) {
    fun deleteFriendFromTrip(
        tripId: Int,
        tripManagerId: String,
        friendUserId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        commonRemoteDataSource.deleteFriendFromTrip(
            tripId = tripId,
            tripManagerId = tripManagerId,
            friendUserId = friendUserId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    suspend fun deleteInvitedFriendsFromTrip(
        tripId: Int,
        tripManagerId: String
    ): Boolean {
        return commonRemoteDataSource.deleteInvitedFriendsFromTrip(
            tripId = tripId,
            tripManagerId = tripManagerId
        )
    }

    fun getInvitedFriends(
        internetEnabled: Boolean,
        tripManagerId: String,
        tripId: Int,

        onSuccess: (friendList: List<UserData>) -> Unit,
        onError: () -> Unit
    ){
        inviteFriendRemoteDataSource.getInvitedFriends(
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
        inviteFriendRemoteDataSource.checkAndAddSharingTripFriend(
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

    fun setFriendPermission(
        tripId: Int,
        myUserId: String,
        friendUserData: UserData,

        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        inviteFriendRemoteDataSource.setFriendPermission(
            tripId = tripId,
            myUserId = myUserId,
            friendUserData = friendUserData,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}