package com.example.somewhere.db

import com.example.somewhere.model.Trip
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class OfflineTripRepository(private val tripDao: TripDao): TripRepository {
    override fun getAllTripsStream(): Flow<List<Trip>> = tripDao.getAllTrips()

    override fun getTripStream(id: Int): Flow<Trip?> = tripDao.getTrip(id)

    override fun getTripStream(time: LocalDateTime): Flow<Trip?> = tripDao.getTrip(time)

    override suspend fun insertTrip(trip: Trip) = tripDao.insert(trip)

    override suspend fun deleteTrip(trip: Trip) = tripDao.delete(trip)

    override suspend fun updateTrip(trip: Trip) = tripDao.update(trip)
}