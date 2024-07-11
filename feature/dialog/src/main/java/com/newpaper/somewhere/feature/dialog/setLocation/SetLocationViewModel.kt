package com.newpaper.somewhere.feature.dialog.setLocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.newpaper.somewhere.core.data.repository.PlacesRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.data.LocationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchLocation(
    val searchText: String = "",

    val searchLocationList: List<LocationInfo> = listOf(),
    val isLoadingSearchPlaces: Boolean = false,
    val isLoading: Boolean = false
)

data class SpotLocation(
    val firstLocation: LatLng = LatLng(0.0,0.0),
    val newLocation: LatLng = LatLng(0.0,0.0),
    val newZoomLevel: Float = 0f
)

data class SetLocationUiState(
    val searchLocation: SearchLocation = SearchLocation(),
    val spotLocation: SpotLocation = SpotLocation(),

    val keyboardIsShown: Boolean = false,
)

@HiltViewModel
class SetLocationViewModel @Inject constructor(
//    private val placesRepository: PlacesRepository
//    private val userRepository: UserRepository
): ViewModel() {
    private val _setLocationUiState = MutableStateFlow(SetLocationUiState())
    val setLocationUiState = _setLocationUiState.asStateFlow()

    private var job: Job? = null

    fun initSpotLocation(
        firstLocation: LatLng,
        zoomLevel: Float
    ){
        _setLocationUiState.update {
            it.copy(
                spotLocation = it.spotLocation.copy(
                    firstLocation = firstLocation,
                    newLocation = firstLocation,
                    newZoomLevel = zoomLevel
                )
            )
        }
    }

    fun setNewLocation(
        newLocation: LatLng
    ){
        _setLocationUiState.update {
            it.copy(
                spotLocation = it.spotLocation.copy(
                    newLocation = newLocation
                )
            )
        }
    }

    fun setNewZoomLevel(
        newZoomLevel: Float
    ){
        _setLocationUiState.update {
            it.copy(
                spotLocation = it.spotLocation.copy(
                    newZoomLevel = newZoomLevel
                )
            )
        }
    }

    fun setSearchText(
        searchText: String
    ){
        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(
                    searchText = searchText
                )
            )
        }
    }

    fun setIsLoadingSearchPlaces(
        isLoadingSearchPlaces: Boolean
    ){
        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(
                    isLoadingSearchPlaces = isLoadingSearchPlaces
                )
            )
        }
    }

    fun setIsLoading(
        isLoading: Boolean
    ){
        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(
                    isLoading = isLoading
                )
            )
        }
    }

    fun clearSearchLocationList(

    ){
        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(
                    searchLocationList = listOf()
                )
            )
        }
    }






    suspend fun searchPlaces(query: String) {
//        job?.cancel()

        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(searchLocationList = listOf()),
            )
        }

//        job = viewModelScope.launch {
//            val newSearchLocationList = placesRepository.searchPlaces(query)
//            if (newSearchLocationList != null){
//                _setLocationUiState.update {
//                    it.copy(
//                        searchLocation = it.searchLocation.copy(
//                            searchLocationList = it.searchLocation.searchLocationList + newSearchLocationList
//                        ),
//                    )
//                }
//            }
//        }
    }

    suspend fun updateAllLatLng(){
        while (_setLocationUiState.value.searchLocation.isLoadingSearchPlaces){
            delay(100)
        }

        for (index: Int in 0 until _setLocationUiState.value.searchLocation.searchLocationList.size){
            val locationInfo = _setLocationUiState.value.searchLocation.searchLocationList.getOrNull(index)
            if (locationInfo != null){
//                if (locationInfo.location == null) {
//                    locationInfo.location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId)
//                }
            }
        }
    }

    suspend fun updateOneLatLng(locationInfo: LocationInfo){
//        locationInfo.location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId)
    }
}