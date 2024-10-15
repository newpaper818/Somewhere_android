package com.newpaper.somewhere.feature.trip.tripAi

import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.AiRepository
import com.newpaper.somewhere.core.data.repository.SerializationRepository
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.getTripId
import com.newpaper.somewhere.feature.trip.tripAi.model.TripType
import com.newpaper.somewhere.feature.trip.tripAi.model.TripWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.Inject

enum class TripAiPhase{
    TRIP_TO, TRIP_DATE, TRIP_WITH, TRIP_TYPE, CREATE_TRIP
}

data class TripAiUiState(
    val tripAiPhase: TripAiPhase = TripAiPhase.TRIP_TO,

    val tripTo: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val tripWith: TripWith? = null,
    val tripTypes: Set<TripType> = setOf(),

    val showExitDialog: Boolean = false
)

@HiltViewModel
class TripAiViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val serializationRepository: SerializationRepository
): ViewModel() {
    private val _tripAiUiState: MutableStateFlow<TripAiUiState> =
        MutableStateFlow(
            TripAiUiState()
        )

    val tripAiUiState = _tripAiUiState.asStateFlow()

    fun setPhase(tripAiPhase: TripAiPhase){
        _tripAiUiState.update {
            it.copy(tripAiPhase = tripAiPhase)
        }
    }

    fun setTripTo(tripTo: String?){
        _tripAiUiState.update {
            it.copy(tripTo = tripTo)
        }
    }

    fun setStartDate(startDate: LocalDate?){
        _tripAiUiState.update {
            it.copy(startDate = startDate)
        }
    }

    fun setEndDate(endDate: LocalDate?){
        _tripAiUiState.update {
            it.copy(endDate = endDate)
        }
    }

    fun setTripWith(tripWith: TripWith?){
        _tripAiUiState.update {
            it.copy(tripWith = tripWith)
        }
    }

    fun setTripTypes(tripTypes: Set<TripType>){
        _tripAiUiState.update {
            it.copy(tripTypes = tripTypes)
        }
    }

    fun setShowExitDialog(showExitDialog: Boolean){
        _tripAiUiState.update {
            it.copy(showExitDialog = showExitDialog)
        }
    }

    fun onClickTripType(
        tripType: TripType
    ){
        val mutableSet = tripAiUiState.value.tripTypes.toMutableSet()

        if (tripAiUiState.value.tripTypes.contains(tripType))
            mutableSet.remove(tripType)
        else
            mutableSet.add(tripType)

        setTripTypes(mutableSet)
    }




    suspend fun getAiCreatedRawTrip(

    ): Trip? {
        if (
            tripAiUiState.value.tripTo == null
            || tripAiUiState.value.startDate == null
            || tripAiUiState.value.endDate == null
            || tripAiUiState.value.tripWith == null
            || tripAiUiState.value.tripTypes.isEmpty()
        ){
            return null
        }

        val aiCreatedTrip = CompletableDeferred<Trip?>()

        val aiCreatedTripJson = aiRepository.getAiCreatedTrip(
            city = tripAiUiState.value.tripTo!!,
            startDate = tripAiUiState.value.startDate!!,
            endDate = tripAiUiState.value.endDate!!,
            tripWith = tripAiUiState.value.tripWith.toString(),
            tripType = tripAiUiState.value.tripTypes.toString(),
            language = Locale.getDefault().displayLanguage
        )

        //convert JSON -> Trip
        if (aiCreatedTripJson != null) {
            val trip = serializationRepository.jsonToTrip(aiCreatedTripJson)

            aiCreatedTrip.complete(trip)
        }
        else aiCreatedTrip.complete(null)

        return aiCreatedTrip.await()
    }

    fun addIdTimeInAiCreatedRawTrip(
        aiCreatedRawTrip: Trip,
        tripManagerId: String,
        newOrderId: Int
    ): Trip {
        val nowTime = ZonedDateTime.now()

        val newTripId = getTripId(
            managerId = tripManagerId,
            firstCreatedTripTime = nowTime
        )

        val newDateList = mutableListOf<Date>()
        aiCreatedRawTrip.dateList.forEachIndexed { dateIndex, date ->

            var iconTextNum = 1
            val newSpotList = mutableListOf<Spot>()

            date.spotList.forEachIndexed { spotIndex, spot ->

                val newSpot = spot.copy(
                    id = ZonedDateTime.now().hashCode() * 31 + dateIndex * 17 + spotIndex,
                    index = spotIndex,
                    iconText = if (spot.spotType.group != SpotTypeGroup.MOVE) iconTextNum++
                                else iconTextNum,
                    memoText = if (spot.memoText == "") null else spot.memoText
                )
                newSpotList.add(newSpot)
            }

            val newDate = date.copy(
                id = dateIndex,
                index = dateIndex,
                memoText = if (date.memoText == "") null else date.memoText,
                spotList = newSpotList
            )
            newDateList.add(newDate)
        }

        val newTrip = aiCreatedRawTrip.copy(
            id = newTripId,
            orderId = newOrderId,
            managerId = tripManagerId,
            editable = true,
            dateList = newDateList,
            memoText = if (aiCreatedRawTrip.memoText == "") null else aiCreatedRawTrip.memoText,

            firstCreatedTime = nowTime,
            lastModifiedTime = nowTime
        )

        return newTrip
    }
}