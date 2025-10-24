package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips

import com.newpaper.somewhere.core.model.tripData.Trip

interface TripsRemoteDataSource {

    /**
     * get trips(without date list) from remote db
     */
    suspend fun getMyTrips(
        internetEnabled: Boolean,
        userId: String,
        orderByLatest: Boolean
    ): List<Trip>

    /**
     * get shared trips(without date list) from remote db
     */
    suspend fun getSharedTrips(
        internetEnabled: Boolean,
        appUserId: String,
        orderByLatest: Boolean
    ): List<Trip>


    /**
     * Save [Trip]s to remote db.
     * Use when click save button on myTripsScreen.
     *
     * @param appUserId unique user ID (from Firebase Authentication uid). If null, don't save.
     */
    suspend fun saveTrips(
        appUserId: String?,
        myTripList: List<Trip>,
        deletedTripsIdList: List<Int>,
        deletedSharedTripList: List<Trip>
    ): Boolean

    /**
     * update shared trips order
     */
    suspend fun updateSharedTripsOrder(
        appUserId: String?,
        sharedTripList: List<Trip>
    ): Boolean
}