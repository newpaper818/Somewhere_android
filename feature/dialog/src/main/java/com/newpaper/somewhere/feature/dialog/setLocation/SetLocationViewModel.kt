package com.newpaper.somewhere.feature.dialog.setLocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.PlacesRepository
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

data class SetLocationUiState(
    val searchLocation: SearchLocation = SearchLocation(),

    val googleMapsPlacesId: String? = null,

    val userTexting: Boolean = false,
    val clickedSearch: Boolean = false,

    val showDropdownMenu: Boolean = false,
    val showOtherDateSpotMarkers: Boolean = false
)

@HiltViewModel
class SetLocationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
): ViewModel() {
    private val _setLocationUiState = MutableStateFlow(SetLocationUiState())
    val setLocationUiState = _setLocationUiState.asStateFlow()

    private var job: Job? = null

    fun init(){
        _setLocationUiState.update {
            it.copy(
                searchLocation = SearchLocation()
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

    fun setGoogleMapsPlacesId(
        googleMapsPlacesId: String?
    ){
        _setLocationUiState.update {
            it.copy(googleMapsPlacesId = googleMapsPlacesId)
        }
    }


    fun setUserTexting(
        userTexting: Boolean
    ){
        _setLocationUiState.update {
            it.copy(userTexting = userTexting)
        }
    }

    fun setShowDropdownMenu(
        showDropdownMenu: Boolean
    ) {
        _setLocationUiState.update {
            it.copy(showDropdownMenu = showDropdownMenu)
        }
    }

    fun setShowOtherDateSpotMarkers(
        showOtherDateSpotMarkers: Boolean
    ){
        _setLocationUiState.update {
            it.copy(showOtherDateSpotMarkers = showOtherDateSpotMarkers)
        }
    }





    suspend fun searchPlaces(query: String) {
        job?.cancel()

        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(searchLocationList = listOf()),
            )
        }

        job = viewModelScope.launch {
            val newSearchLocationList = placesRepository.searchPlaces(query)
            if (newSearchLocationList != null){
                _setLocationUiState.update {
                    it.copy(
                        searchLocation = it.searchLocation.copy(
                            searchLocationList = it.searchLocation.searchLocationList + newSearchLocationList
                        ),
                    )
                }
            }
        }
    }

    suspend fun updateAllLatLng(){
        while (_setLocationUiState.value.searchLocation.isLoadingSearchPlaces){
            delay(100)
        }

        for (index: Int in 0 until _setLocationUiState.value.searchLocation.searchLocationList.size){
            val locationInfo = _setLocationUiState.value.searchLocation.searchLocationList.getOrNull(index)
            if (locationInfo != null){
                if (locationInfo.location == null) {
                    locationInfo.location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId)
                }
            }
        }
    }

    suspend fun updateOneLatLng(locationInfo: LocationInfo){
        locationInfo.location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId)
    }
}