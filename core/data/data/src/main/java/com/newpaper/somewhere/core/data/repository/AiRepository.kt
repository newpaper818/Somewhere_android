package com.newpaper.somewhere.core.data.repository

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.newpaper.gemini_ai.AiRemoteDataSource
import com.newpaper.somewhere.core.data.moshi.SerializationDataSource
import com.newpaper.somewhere.core.google_map_places.dataSource.PlacesRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val aiRemoteDataSource: AiRemoteDataSource,
    private val placesRemoteDataSource: PlacesRemoteDataSource,
    private val serializationDataSource: SerializationDataSource
) {
    suspend fun getAiCreatedTrip(
        city: String,
        startDate: LocalDate,
        endDate: LocalDate,
        tripWith: String,
        tripType: String,
        language: String
    ): Trip? {
        //get places from ai / return: [N seoul tower, lotte tower ...]
        val recommendPlaceSet = aiRemoteDataSource.getRecommendSpots(
            city = city,
            tripDays = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays().toInt() + 1,
            tripWith = tripWith,
            tripType = tripType
        )

        if (recommendPlaceSet == null)
            return null

        //get places info from Google places
        val places = placesRemoteDataSource.getPlacesInfo(recommendPlaceSet)

        if (places == null)
            return null

        //get trip plans from ai
        val aiCreatedTripJson = aiRemoteDataSource.getTripPlan(
            places = places,
            city = city,
            tripDate = "$startDate ~ $endDate",
            tripWith = tripWith,
            tripType = tripType,
            language = language
        )

        if (aiCreatedTripJson == null)
            return null

        //convert JSON -> Trip
        val aiCreatedTrip = serializationDataSource.jsonToTrip(aiCreatedTripJson)

        if (aiCreatedTrip == null)
            return null

        //add location
        val tripWithLocation = addLocation(
            trip = aiCreatedTrip,
            places = places
        )

        return tripWithLocation
    }

    private fun addLocation(
        trip: Trip,
        places: Set<Place>
    ): Trip {
        val dateList = trip.dateList

        val newDateList = mutableListOf<Date>()
        dateList.forEach { date ->
            val spotList = date.spotList
            val newSpotList = mutableListOf<Spot>()

            spotList.forEach{ spot ->
                val newSpot = spot.copy(
                    location = getLatLngFromPlaceId(
                        placeId = spot.googleMapsPlacesId,
                        places = places
                    ),
                    zoomLevel = 15f
                )
                newSpotList.add(newSpot)
            }

            val newDate = date.copy(
                spotList = newSpotList
            )
            newDateList.add(newDate)
        }

        val newTrip = trip.copy(
            dateList = newDateList
        )

        return newTrip
    }

    private fun getLatLngFromPlaceId(
        placeId: String?,
        places: Set<Place>
    ): LatLng? {
        val place = places.find {
            it.id == placeId
        }

        return place?.location
    }
}