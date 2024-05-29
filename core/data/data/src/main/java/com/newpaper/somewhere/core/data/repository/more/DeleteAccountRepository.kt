package com.newpaper.somewhere.core.data.repository.more

import com.newpaper.somewhere.core.firebase_firestore.dataSource.more.deleteAccount.DeleteAccountRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import javax.inject.Inject

class DeleteAccountRepository @Inject constructor(
    private val deleteAccountRemoteDataSource: DeleteAccountRemoteDataSource,
){

    
    suspend fun getSharedTrips(
        appUserId: String,
    ): List<Trip> {
        return deleteAccountRemoteDataSource.getSharedTrips(appUserId)
    }
    
    suspend fun deleteAccount(
        appUserId: String?,
        sharedTripList: List<Trip>,
        deleteResult: (Boolean) -> Unit,
        increaseDeleteAccountProgress: () -> Unit
    ){
        deleteAccountRemoteDataSource.deleteUserData(
            appUserId = appUserId,
            sharedTripList = sharedTripList,
            deleteResult = deleteResult,
            increaseDeleteAccountProgress = increaseDeleteAccountProgress
        )
    }
}