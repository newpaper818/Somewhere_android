package com.newpaper.somewhere.core.data.repository.trip

import com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trip.TripRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val tripRemoteDataSource: TripRemoteDataSource
) {

    suspend fun getTrip(
        internetEnabled: Boolean,
        appUserId: String,
        tripWithEmptyDateList: Trip,
    ): Trip? {
        return tripRemoteDataSource.getTrip(
                internetEnabled = internetEnabled,
                tripManagerId = tripWithEmptyDateList.managerId,
                appUserId = appUserId,
                tripId = tripWithEmptyDateList.id,
                editable = tripWithEmptyDateList.editable
            )
    }

}