package com.newpaper.somewhere.core.data.repository

import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips.TripsRemoteDataSource
import javax.inject.Inject




class TripsRepository @Inject constructor(
    private val tripsRemoteDataSource: TripsRemoteDataSource
) {
    suspend fun getMyTrips(
        internetEnabled: Boolean,
        userId: String
    ): List<Trip>? {
        return tripsRemoteDataSource.getMyTripsOrderByOrderId(
            internetEnabled = internetEnabled,
            userId = userId
        )
    }
}