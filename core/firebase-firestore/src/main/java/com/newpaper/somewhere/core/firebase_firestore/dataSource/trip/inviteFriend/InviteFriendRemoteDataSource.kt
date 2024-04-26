package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend

import com.newpaper.somewhere.core.model.data.UserData

interface InviteFriendRemoteDataSource {

    /**
     * get trip's invited friends
     */
    fun getInvitedFriends(
        internetEnabled: Boolean,
        managerId: String,
        tripId: Int,

        onSuccess: (friendList: List<UserData>) -> Unit,
        onError: () -> Unit
    )

    /**
     * add sharing trip friend
     */
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
    )

    /**
     * set friend's permission (editable ore view only)
     */
    fun setFriendPermission(
        tripId: Int,
        myUserId: String,
        friendUserData: UserData,

        onSuccess: () -> Unit,
        onError: () -> Unit
    )
}