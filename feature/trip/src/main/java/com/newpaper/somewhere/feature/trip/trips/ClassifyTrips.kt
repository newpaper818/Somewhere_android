package com.newpaper.somewhere.feature.trip.trips

import com.newpaper.somewhere.core.model.tripData.Trip
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Classify Trips to (all / active / past) trips
 * */
internal fun classifyTrips(
    trips: List<Trip>,
): Triple<List<Trip>, List<Trip>, List<Trip>> {

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

    return Triple(allTrips, activeTrips, pastTrips)
}