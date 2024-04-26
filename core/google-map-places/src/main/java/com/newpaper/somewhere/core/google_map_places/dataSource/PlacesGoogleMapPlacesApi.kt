package com.newpaper.somewhere.core.google_map_places.dataSource

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.newpaper.somewhere.core.model.data.LocationInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val GOOGLE_MAP_PLACES_TAG = "Google-Map-Places"

class PlacesGoogleMapPlacesApi @Inject constructor(

): PlacesRemoteDataSource {

    private lateinit var placesClient: PlacesClient

    override suspend fun searchPlaces(
        context: Context,
        query: String
    ): List<LocationInfo>? {
        val locationList = CompletableDeferred<List<LocationInfo>?>()

        checkPlacesClientAndInit(context)

        delay(300)

        val request = FindAutocompletePredictionsRequest.builder().setQuery(query).build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                //up to 10
                val subList = response.autocompletePredictions.slice(0..Integer.min(
                    9,
                    response.autocompletePredictions.size - 1
                )
                )

                locationList.complete(
                    subList.map {
                        LocationInfo(
                            title = it.getPrimaryText(null).toString(),
                            address = it.getSecondaryText(null).toString(),
                            placeId = it.placeId,
                        )
                    }
                )

            }
            .addOnFailureListener {
                it.printStackTrace()
                println(it.cause)
                println(it.message)
                locationList.complete(null)
            }

        return locationList.await()
    }






















    private fun checkPlacesClientAndInit(context: Context){
        if (!Places.isInitialized() || !::placesClient.isInitialized) {
            Places.initialize(context, MAPS_API_KEY)
            placesClient = Places.createClient(context)
        }
    }
}