package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend

import com.newpaper.somewhere.core.model.data.UserData

interface InviteFriendRemoteDataSource {

    /**
     * get trip's invited friends
     */
    fun getInvitedFriends(
        internetEnabled: Boolean,
        tripManagerId: String,
        tripId: Int,

        onSuccess: (friendList: List<UserData>) -> Unit,
        onError: () -> Unit
    )

    suspend fun getFriendUserDataFromEmail(
        friendUserEmail: String,
    ): Pair<UserData?, Boolean>

    suspend fun getFriendUserDataFromUserId(
        friendUserUserId: String,
    ): Pair<UserData?, Boolean>

    /**
     * add sharing trip friend
     */
    suspend fun inviteFriend(
        tripId: Int,
        appUserId: String,
        friendUserId: String,
        editable: Boolean,
        onSuccess: () -> Unit,
        onErrorSnackbar: () -> Unit
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