package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.deleteAccount

import com.newpaper.somewhere.core.model.tripData.Trip

interface DeleteAccountRemoteDataSource {

    /**
     * Delete user data(account).
     * Include all Trips, Places, Images
     *
     * @param appUserId unique user ID (from Firebase Authentication uid). If null, don't delete.
     */
    suspend fun deleteUserData(
        appUserId: String?,
        sharedTripList: List<Trip>,
        deleteResult: (Boolean) -> Unit,
        increaseDeleteAccountProgress: () -> Unit
    )
}