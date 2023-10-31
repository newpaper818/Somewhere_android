package com.newpaper.somewhere.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.newpaper.somewhere.BuildConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Integer.min
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class LocationInfo(
    val title: String,
    val address: String,
    val placeId: String,
    var location: LatLng? = null
)

class SetLocationViewModel(
    context: Context
): ViewModel(){

    val searchLocationList = mutableStateListOf<LocationInfo>()
    var isLoadingSearchPlaces by mutableStateOf(false)

    var isLoading by mutableStateOf(false)

    private lateinit var placesClient: PlacesClient
    private var job: Job? = null

    init {
        searchLocationList.clear()

        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY)
            placesClient = Places.createClient(context)
        }
    }

    fun searchPlaces(query: String) {

        job?.cancel()
        searchLocationList.clear()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest.builder().setQuery(query).build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    //up to 10
                    val subList = response.autocompletePredictions.slice(0..min(9, response.autocompletePredictions.size - 1))

                    searchLocationList += subList.map {
                        LocationInfo(
                            title = it.getPrimaryText(null).toString(),
                            address = it.getSecondaryText(null).toString(),
                            placeId = it.placeId,
                        )

                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }

    suspend fun updateAllLatLng(){
        while (isLoadingSearchPlaces){
            delay(100)
        }

        for (oneLocation in searchLocationList){
            if (oneLocation.location == null) {
                oneLocation.location = getLatLngFromPlaceId(oneLocation.placeId)
            }
        }
    }

    suspend fun updateOneLatLng(locationInfo: LocationInfo){
        locationInfo.location = getLatLngFromPlaceId(locationInfo.placeId)
    }

    private suspend fun getLatLngFromPlaceId(placeId: String): LatLng? = suspendCoroutine { continuation ->
        placesClient.fetchPlace(
            FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))
        ).addOnSuccessListener {
            continuation.resume(it.place.latLng)
        }.addOnFailureListener {
            Log.e(this.toString(), it.stackTraceToString())
            continuation.resume(null)
        }
    }
}