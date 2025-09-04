package com.newpaper.somewhere.core.google_map_places.dataSource

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
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

        delay(600)

        val request = FindAutocompletePredictionsRequest
            .builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                //up to 10
                val subList = response.autocompletePredictions.slice(0..Integer.min(
                    9,
                    response.autocompletePredictions.size - 1
                )
                )

                locationList.complete(
                    subList.mapIndexed { index, it ->
                        LocationInfo(
                            title = it.getPrimaryText(null).toString(),
                            address = it.getSecondaryText(null).toString(),
                            placeId = it.placeId,
                            mapMarkerIndex = ('A' + index).toString()
                        )
                    }
                )

            }
            .addOnFailureListener {
                Log.e(GOOGLE_MAP_PLACES_TAG, it.stackTraceToString())
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
            Log.e(GOOGLE_MAP_PLACES_TAG, it.stackTraceToString())
            continuation.resume(null)
        }
    }

    override suspend fun getPlacesInfo(
        places: Set<String>
    ): Set<Pair<String, Place>>? {
        checkPlacesClientAndInit()

        val placeInfoList: List<Pair<String, Place>> = places.mapNotNull {
            searchPlace(it)
        }

//        placeInfoList.forEach{ place ->
//            Log.d("gemini", "name: ${place.displayName}, id: ${place.id}")
//        }

        Log.d("gemini", "getPlacesInfo: ${placeInfoList.size}")

        return if (placeInfoList.isEmpty())
            null
        else
            placeInfoList.toSet()
    }

    private suspend fun searchPlace(
        query: String
    ): Pair<String, Place>?{
        val placeInfo = CompletableDeferred<Pair<String, Place>?>()

        val placeFields: List<Place.Field> = arrayListOf(
            Place.Field.ID,
            Place.Field.TYPES,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
        )

        val request = SearchByTextRequest
            .builder(query, placeFields)
            .setMaxResultCount(1)
            .build()

        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                val place = response.places.firstOrNull()
                if (place != null)
                    placeInfo.complete(Pair(query, place))
                else
                    placeInfo.complete(null)
            }
            .addOnFailureListener {
                Log.e(GOOGLE_MAP_PLACES_TAG, it.stackTraceToString())
                placeInfo.complete(null)
            }

        return placeInfo.await()
    }




    private fun checkPlacesClientAndInit() {
        if (!Places.isInitialized()
//            || !::placesClient.isInitialized
        ) {
            Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.GOOGLE_MAPS_API_KEY)
            placesClient = Places.createClient(context)
        }
    }
}