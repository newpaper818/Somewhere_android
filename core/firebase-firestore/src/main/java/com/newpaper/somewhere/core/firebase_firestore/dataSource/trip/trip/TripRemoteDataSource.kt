package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trip

import com.newpaper.somewhere.core.model.tripData.Trip

interface TripRemoteDataSource {

    /**
     * get trip's all data
     */
    suspend fun getTripData(
        internetEnabled: Boolean,
        tripManagerId: String,
        appUserId: String,
        tripId: Int,
        editable: Boolean
    ): Trip?


    /**
     * Save [Trip] (include all dateList, spotList) to remote db.
     * Use when click save button on TripScreen, DateScreen.
     *
     * @param userId unique user ID (from Firebase Authentication uid). If null, don't save.
     * @param trip [Trip] that will saved to remote
     * @param tempTripDateListLastIndex temp [Trip] dateList's last index. use to delete un enabled [Date]s
     */
    suspend fun saveTripAndAllDates(
        userId: String?,
        trip: Trip,
        tempTripDateListLastIndex: Int? = null
    ):Boolean
}