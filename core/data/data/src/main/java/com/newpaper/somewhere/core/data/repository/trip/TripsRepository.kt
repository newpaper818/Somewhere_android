package com.newpaper.somewhere.core.data.repository.trip

import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips.TripsRemoteDataSource
import javax.inject.Inject




class TripsRepository @Inject constructor(
    private val tripsRemoteDataSource: TripsRemoteDataSource
) {
    suspend fun getMyTrips(
        internetEnabled: Boolean,
        userId: String
    ): List<Trip> {
        return tripsRemoteDataSource.getMyTrips(
            internetEnabled = internetEnabled,
            userId = userId
        )
    }

    suspend fun getSharedTrips(
        internetEnabled: Boolean,
        appUserId: String,
    ): List<Trip> {
        return tripsRemoteDataSource.getSharedTrips(
            internetEnabled = internetEnabled,
            appUserId = appUserId
        )
    }

    suspend fun saveTrips(
        appUserId: String?,
        myTripList: List<Trip>,
        deletedTripsIdList: List<Int>,
        deletedSharedTripList: List<Trip>
    ): Boolean {
        return tripsRemoteDataSource.saveTrips(
            appUserId = appUserId,
            myTripList = myTripList,
            deletedTripsIdList = deletedTripsIdList,
            deletedSharedTripList = deletedSharedTripList
        )
    }

    suspend fun updateSharedTripsOrder(
        appUserId: String?,
        sharedTripList: List<Trip>
    ): Boolean {
        return tripsRemoteDataSource.updateSharedTripsOrder(
            appUserId = appUserId,
            sharedTripList = sharedTripList
        )
    }
}