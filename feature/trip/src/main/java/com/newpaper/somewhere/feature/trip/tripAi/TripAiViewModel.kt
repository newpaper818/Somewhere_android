package com.newpaper.somewhere.feature.trip.tripAi

import android.util.Log
import androidx.lifecycle.ViewModel
import com.newpaper.somewhere.core.data.repository.AiRepository
import com.newpaper.somewhere.core.data.repository.SerializationRepository
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Spot
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.getTripId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.Inject


data class TripAiUiState(
    val test: Boolean = false,
    val trip: Trip? = null
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

    fun setAiCreatedTrip(
        trip: Trip?
    ){
        _tripAiUiState.update {
            it.copy(trip = trip)
        }
    }

    suspend fun getAiCreatedRawTrip(

    ): Trip? {
        val aiCreatedTrip = CompletableDeferred<Trip?>()

        val aiCreatedTripJson = aiRepository.getAiCreatedTrip(
            city = "Seoul, Korea",
            tripDate = "2024-10-29 ~ 2024-11-02",
            tripWith = "friend",
            tripType = "none",
            language = Locale.getDefault().language
        )

        //convert JSON -> Trip
        if (aiCreatedTripJson != null) {
            val trip = serializationRepository.jsonToTrip(aiCreatedTripJson)

            Log.d("gemini", "$trip")

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

            val newSpotList = mutableListOf<Spot>()
            date.spotList.forEachIndexed { spotIndex, spot ->

                val newSpot = spot.copy(
                    id = spotIndex,
                    index = spotIndex,
                    iconText = spotIndex + 1,
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