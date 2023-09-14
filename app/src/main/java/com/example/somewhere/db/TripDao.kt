package com.example.somewhere.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.somewhere.model.Trip
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TripDao {

    @Query("SELECT * from trips ORDER BY orderId ASC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * from trips WHERE id = :id")
    fun getTrip(id: Int): Flow<Trip>

    @Query("SELECT * from trips WHERE firstCreatedTime = :firstCreatedTime")
    fun getTrip(firstCreatedTime: LocalDateTime): Flow<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: Trip)

    @Update
    suspend fun update(trip: Trip)

    @Delete
    suspend fun delete(trip: Trip)
}
