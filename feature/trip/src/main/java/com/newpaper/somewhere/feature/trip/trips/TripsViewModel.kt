package com.newpaper.somewhere.feature.trip.trips

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.trip.TripsRepository
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class Trips(
    val trips: List<Trip>? = null,
    val tempTrips: List<Trip>? = null,
    val sharedTrips: List<Trip>? = null,
    val tempSharedTrips: List<Trip>? = null
)

data class Glance(
    val visible: Boolean = false,
    val trip: Trip? = null,
    val date: Date? = null,
    val spot: Spot? = null
)

data class TripsUiState(
    val trips: Trips = Trips(),
    val glance: Glance = Glance()
)


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripsRepository: TripsRepository
): ViewModel()  {
    private val _tripsUiState: MutableStateFlow<TripsUiState> =
        MutableStateFlow(
            TripsUiState(
                trips = Trips(
                    trips = emptyList(),
                    tempTrips = emptyList()
                )
            )
        )

    val tripsUiState = _tripsUiState.asStateFlow()

    suspend fun updateTrips(
        internetEnabled: Boolean,
        appUserId: String
    ){
        val newTrips = tripsRepository.getMyTrips(internetEnabled, appUserId)
        val newSharedTrips = tripsRepository.getSharedTrips(internetEnabled, appUserId)

        //set orderId of shared trip
        newSharedTrips.forEachIndexed { index, sharedTrip ->
            sharedTrip.orderId = index
        }

        //update newTripList's dateList from tripUiState.value.tripList's dateList
        //because newTripList's dateList is empty list (get trip data except dateList)

        // -> at app open and user enter TripScreen, user see empty dateList,
        //    after load dateList, dateList will be show to user and dateList will save in viewModel (even user go back to MyTripsScreen)
        //    when user enter TripScreen(again), old dateList will show (not empty list)
        //    after load dateList, user can see new dateList

        // all trip data(not really all, user have to go to TripScreen) will be saved before execute app
        for (trip in newTrips) {
            val matchingOldTrip = tripsUiState.value.trips.trips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }

        //sharedTrip
        for (trip in newSharedTrips) {
            val matchingOldTrip = tripsUiState.value.trips.sharedTrips?.find { it.id == trip.id }

            if (matchingOldTrip != null){
                trip.dateList = matchingOldTrip.dateList
            }
        }


        _tripsUiState.update {
            it.copy(
                trips = it.trips.copy(
                    trips = newTrips,
                    tempTrips = newTrips,
                    sharedTrips = newSharedTrips,
                    tempSharedTrips = newSharedTrips
                )
            )
        }
    }
}