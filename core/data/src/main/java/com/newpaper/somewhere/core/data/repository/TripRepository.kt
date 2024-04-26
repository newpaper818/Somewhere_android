package com.newpaper.somewhere.core.data.repository

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

internal class NetworkTripRepository @Inject constructor(
    private val firestore: Firestore,
    private val tripRemoteDataSource: TripRemoteDataSource
): TripRepository{

    override val tripData: Flow<Trip>
        get() = TODO()

    override val tempTripData: Flow<Trip>
        get() = TODO("Not yet implemented")

    override fun saveToOriginalTrip() {
        TODO("Not yet implemented")
    }

    override fun setTripTitle(title: String) {
        TODO("Not yet implemented")
    }

    override fun setMemo(memo: String) {
        TODO("Not yet implemented")
    }
}