package com.newpaper.somewhere.feature.trip

import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.model.tripData.TripsGroup
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.collections.forEach

/**
 * Classify Trips to (all / active / past) trips
 * */
internal fun classifyAndConvertToTripsGroup(
    trips: List<Trip>,
): TripsGroup {

    val allTrips = trips
    val activeTrips = mutableListOf<Trip>()
    val pastTrips = mutableListOf<Trip>()

    val currentDate = ZonedDateTime.now().toLocalDate()

    trips.forEach { trip ->
        if (trip.endDate != null) {
            if (currentDate <= LocalDate.parse(trip.endDate))
                activeTrips.add(trip)
            else
                pastTrips.add(trip)
        }
    }

    return TripsGroup(allTrips, activeTrips, pastTrips)
}