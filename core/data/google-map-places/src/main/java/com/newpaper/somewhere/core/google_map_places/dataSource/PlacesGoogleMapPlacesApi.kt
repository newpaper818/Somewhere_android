package com.newpaper.somewhere.core.google_map_places.dataSource

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.kotlin.place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.newpaper.somewhere.core.data.google_map_places.BuildConfig
import com.newpaper.somewhere.core.model.data.LocationInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val GOOGLE_MAP_PLACES_TAG = "Google-Map-Places"

class PlacesGoogleMapPlacesApi @Inject constructor(
    @ApplicationContext private val context: Context,
    private var placesClient: PlacesClient
): PlacesRemoteDataSource {

    override suspend fun searchPlaces(
        query: String
    ): List<LocationInfo>? {
        val locationList = CompletableDeferred<List<LocationInfo>?>()

        checkPlacesClientAndInit()

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
                Log.e(this.toString(), it.stackTraceToString())
                locationList.complete(null)
            }

        return locationList.await()
    }

    override suspend fun getLatLngFromPlaceId(
        placeId: String
    ): LatLng? = suspendCoroutine { continuation ->

        checkPlacesClientAndInit()

        placesClient.fetchPlace(
            FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LOCATION))
        ).addOnSuccessListener {
            continuation.resume(it.place.location)
        }.addOnFailureListener {
            Log.e(this.toString(), it.stackTraceToString())
            continuation.resume(null)
        }
    }

    override suspend fun getPlacesInfo(
        places: List<String>
    ): List<Place>? {
        checkPlacesClientAndInit()

        val placeInfoList = places.mapNotNull {
            searchPlace(it)
        }

        placeInfoList.forEach{ place ->
            Log.d("gemini", "name: ${place.displayName}, location: ${place.location}")
        }

        return placeInfoList.ifEmpty { null }
    }

    private suspend fun searchPlace(
        query: String
    ): Place?{
        val placeInfo = CompletableDeferred<Place?>()

        val placeFields: List<Place.Field> = arrayListOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.GOOGLE_MAPS_URI,
            Place.Field.WEBSITE_URI,
            Place.Field.TYPES,
            Place.Field.RATING,
            Place.Field.OPENING_HOURS
        )

        val request = SearchByTextRequest.builder(query, placeFields)
            .setMaxResultCount(3)
            .build()

        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                placeInfo.complete(response.places.firstOrNull())
            }
            .addOnFailureListener {
                Log.e(this.toString(), it.stackTraceToString())
                placeInfo.complete(null)
            }

        return placeInfo.await()
    }




    private fun checkPlacesClientAndInit() {
        if (!Places.isInitialized()
//            || !::placesClient.isInitialized
        ) {
            Places.initialize(context, BuildConfig.GOOGLE_MAPS_API_KEY)
            placesClient = Places.createClient(context)
        }
    }
}