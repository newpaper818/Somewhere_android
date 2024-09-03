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

    suspend fun getFriendUserFromEmail(
        friendUserEmail: String
    ): Pair<UserData?, Boolean> {
        return inviteFriendRemoteDataSource.getFriendUserDataFromEmail(
            friendUserEmail = friendUserEmail
        )
    }

    suspend fun getFriendUserFromUserId(
        friendUserUserId: String
    ): Pair<UserData?, Boolean> {
        return inviteFriendRemoteDataSource.getFriendUserDataFromUserId(
            friendUserUserId = friendUserUserId
        )
    }

    suspend fun inviteFriend(
        tripId: Int,
        appUserId: String,
        friendUserId: String,
        editable: Boolean,
        onSuccess: () -> Unit,
        onErrorSnackbar: () -> Unit
    ){
        inviteFriendRemoteDataSource.inviteFriend(
            tripId = tripId,
            appUserId = appUserId,
            friendUserId = friendUserId,
            editable = editable,
            onSuccess = onSuccess,
            onErrorSnackbar = onErrorSnackbar
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