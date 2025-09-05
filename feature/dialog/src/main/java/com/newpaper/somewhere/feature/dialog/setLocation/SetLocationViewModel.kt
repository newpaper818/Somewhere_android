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

    fun setSearchLocationList(
        searchLocationList: List<LocationInfo>
    ){
        _setLocationUiState.update {
            it.copy(
                searchLocation = it.searchLocation.copy(
                    searchLocationList = searchLocationList
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
                setSearchLocationList(_setLocationUiState.value.searchLocation.searchLocationList + newSearchLocationList)
            }
        }
    }

    suspend fun updateAllLatLng(
        onDone: (newSearchLocationList: List<LocationInfo>) -> Unit
    ){
        while (_setLocationUiState.value.searchLocation.isLoadingSearchPlaces){
            delay(100)
        }

        val searchLocationList = _setLocationUiState.value.searchLocation.searchLocationList
        val newSearchLocationList = mutableListOf<LocationInfo>()

        searchLocationList.forEach { locationInfo ->
            var newLocationInfo = locationInfo
            if (locationInfo.location == null) {
                newLocationInfo = locationInfo.copy(location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId))
            }
            newSearchLocationList.add(newLocationInfo)
        }

        setSearchLocationList(newSearchLocationList)

        onDone(newSearchLocationList)
    }

    suspend fun updateOneLatLng(
        locationInfo: LocationInfo,
        onDone: (newLocationInfo: LocationInfo) -> Unit
    ){
        if (locationInfo.location == null) {
            val newLocationInfo = locationInfo.copy(location = placesRepository.getLatLngFromPlaceId(locationInfo.placeId))
            val searchLocationList = _setLocationUiState.value.searchLocation.searchLocationList
            val index = searchLocationList.indexOf(locationInfo)
            val newSearchLocationList = searchLocationList.toMutableList()
            newSearchLocationList[index] = newLocationInfo
            setSearchLocationList(newSearchLocationList)
            onDone(newLocationInfo)
        }
        else{
            onDone(locationInfo)
        }
    }
}