package com.example.somewhere.db

import com.example.somewhere.model.Trip
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TripRepository {

    fun getAllTripsStream(): Flow<List<Trip>>

    fun getTripStream(id: Int): Flow<Trip?>

    fun getTripStream(firstCreatedTime: LocalDateTime): Flow<Trip?>

    suspend fun insertTrip(trip: Trip)

    suspend fun deleteTrip(trip: Trip)

    suspend fun updateTrip(trip: Trip)

}
