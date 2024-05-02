package com.newpaper.somewhere.core.data.repository.trip

import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TripRepository {

    val tripData: Flow<Trip>
    val tempTripData: Flow<Trip>

    fun saveToOriginalTrip()


    fun setTripTitle(title: String)

    fun setMemo(memo: String)



}