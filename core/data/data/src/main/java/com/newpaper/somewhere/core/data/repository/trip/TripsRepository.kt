package com.newpaper.somewhere.core.data.repository.trip

import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips.TripsRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import javax.inject.Inject


class TripsRepository @Inject constructor(
    private val tripsRemoteDataSource: TripsRemoteDataSource
) {
    suspend fun getMyTrips(
        internetEnabled: Boolean,
        userId: String,
        orderByLatest: Boolean
    ): List<Trip> {
        return tripsRemoteDataSource.getMyTrips(
            internetEnabled = internetEnabled,
            userId = userId,
            orderByLatest = orderByLatest
        )
    }

    suspend fun getSharedTrips(
        internetEnabled: Boolean,
        appUserId: String,
        orderByLatest: Boolean
    ): List<Trip> {
        return tripsRemoteDataSource.getSharedTrips(
            internetEnabled = internetEnabled,
            appUserId = appUserId,
            orderByLatest = orderByLatest
        )
    }

    suspend fun saveTrips(
        appUserId: String?,
        myTrips: List<Trip>,
        deletedTripsIds: List<Int>,
        deletedSharedTrips: List<Trip>
    ): Boolean {
        return tripsRemoteDataSource.saveTrips(
            appUserId = appUserId,
            myTripList = myTrips,
            deletedTripsIdList = deletedTripsIds,
            deletedSharedTripList = deletedSharedTrips
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