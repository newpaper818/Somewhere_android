package com.newpaper.somewhere.feature.trip

import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class TripInfo(
    val trips: List<Trip>? = null,
    val tempTrips: List<Trip>? = null,

    val sharedTrips: List<Trip>? = null,
    val tempSharedTrips: List<Trip>? = null,

    val trip: Trip? = null,
    val tempTrip: Trip? = null,

    val dateIndex: Int? = null,
    val spotIndex: Int? = null,
)

data class CommonTripUiState(
    val isEditMode: Boolean = false,

    val tripInfo: TripInfo = TripInfo(),
    val isNewTrip: Boolean = false,

    val isShowingDialog: Boolean = false, //is any dialog showing

    val addedImages: List<String> = listOf(),
    val deletedImages: List<String> = listOf(),
)

@Singleton
class CommonTripUiStateRepository @Inject constructor() {
    val _commonTripUiState: MutableStateFlow<CommonTripUiState> =
        MutableStateFlow(
            CommonTripUiState()
        )
    val commonTripUiState = _commonTripUiState.asStateFlow()
}