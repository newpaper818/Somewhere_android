package com.newpaper.somewhere.core.firebase_functions.dataSource

interface RecursiveDeleteRemoteDataSource {

    suspend fun deleteTrip (
        tripManagerId: String,
        tripId: Int
    ): Boolean


    suspend fun deleteUser (
        appUserId: String
    ): Boolean
}