package com.newpaper.somewhere.core.firebase_functions.dataSource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.newpaper.somewhere.core.firebase_common.TRIP
import com.newpaper.somewhere.core.firebase_common.TRIPS
import com.newpaper.somewhere.core.firebase_common.USERS
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

private const val FIREBASE_FUNCTIONS_TAG = "Firebase-Functions"

class RecursiveDeleteFunctionsApi @Inject constructor(

): RecursiveDeleteRemoteDataSource {

    override suspend fun deleteTrip(
        tripManagerId: String,
        tripId: Int
    ): Boolean{
        val deleteSuccess = CompletableDeferred<Boolean>()

        val deleteTripFunction = Firebase.functions("asia-northeast3")

        val path = "$USERS/$tripManagerId/$TRIPS/$TRIP$tripId"

        Log.d(FIREBASE_FUNCTIONS_TAG, "delete trip - path: $path")

        deleteTripFunction
            .getHttpsCallable("deleteTrip")
            .call(hashMapOf("path" to path))
            .addOnSuccessListener {
                deleteSuccess.complete(true)
                Log.d(FIREBASE_FUNCTIONS_TAG, "delete trip - tripId: $tripId")
            }.addOnFailureListener {e ->
                deleteSuccess.complete(false)
                Log.e(FIREBASE_FUNCTIONS_TAG, "delete trip fail - tripId: $tripId: ", e)
            }

        return deleteSuccess.await()
    }

    override suspend fun deleteUser(
        appUserId: String
    ): Boolean {
        val deleteSuccess = CompletableDeferred<Boolean>()

        val deleteUserFunction = Firebase.functions("asia-northeast3")

        val path = "$USERS/$appUserId"

        Log.d(FIREBASE_FUNCTIONS_TAG, "delete user - path: $path")

        deleteUserFunction
            .getHttpsCallable("deleteUser")
            .call(hashMapOf("path" to path))
            .addOnSuccessListener {
                Log.d(FIREBASE_FUNCTIONS_TAG, "delete user - appUserId: $appUserId")
                deleteSuccess.complete(true)
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FUNCTIONS_TAG, "delete user fail - appUserId: $appUserId", e)
                deleteSuccess.complete(false)
            }

        return deleteSuccess.await()
    }

}