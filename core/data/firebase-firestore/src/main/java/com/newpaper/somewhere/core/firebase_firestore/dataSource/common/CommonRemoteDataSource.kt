package com.newpaper.somewhere.core.firebase_firestore.dataSource.common

import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId

interface CommonRemoteDataSource {

    /**
     * check user exit in remote
     * @return true for user exit / false for not exit / null for fail
     */
    suspend fun checkUserExist(
        userId: String,
    ): Boolean?

    /**
     * delete one trip's one friend - when manager delete friend / when friend delete friend self
     */
    fun deleteFriendFromTrip(
        tripId: Int,
        tripManagerId: String,
        friendUserId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )

    /**
     * delete one trip's all friend - when manager delete trip
     */
    suspend fun deleteInvitedFriendsFromTrip(
        tripId: Int,
        tripManagerId: String
    ): Boolean

    /**
     * get user information
     * @return user data / null for fail
     */
    suspend fun getUserInfo(
        userId: String,
        providerIds: List<ProviderId>
    ): UserData?
}