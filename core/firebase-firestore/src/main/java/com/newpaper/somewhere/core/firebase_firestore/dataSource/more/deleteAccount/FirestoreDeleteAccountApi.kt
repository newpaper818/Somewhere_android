package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.deleteAccount

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.newpaper.somewhere.core.firebase_common.TRIPS
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_functions.dataSource.RecursiveDeleteRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG = "Firebase-Firestore-DeleteAccount"

class FirestoreDeleteAccountApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val commonApi: CommonRemoteDataSource,
    private val recursiveDeleteApi: RecursiveDeleteRemoteDataSource
): DeleteAccountRemoteDataSource {
    override suspend fun deleteUserData(
        appUserId: String?,
        sharedTripList: List<Trip>,
        deleteResult: (Boolean) -> Unit,
        increaseDeleteAccountProgress: () -> Unit
    ){
        if (appUserId != null) {
            //delete friend shared trip
            increaseDeleteAccountProgress() //1
            val deleteFriendsResult = deleteInvitedFriendsFromAllMyTrips(
                appUserId = appUserId
            )

            if (deleteFriendsResult) {
                //get out form all shared trips
                increaseDeleteAccountProgress() //2
                sharedTripList.forEach { sharedTrip ->
                    commonApi.deleteFriendFromTrip(
                        tripId = sharedTrip.id,
                        tripManagerId = sharedTrip.managerId ?: "",
                        friendUserId = appUserId,
                        onSuccess = {

                        },
                        onError = {
                            deleteResult(false)
                        }
                    )
                }

                //delete user using Firebase Functions
                increaseDeleteAccountProgress() //3

                deleteResult(recursiveDeleteApi.deleteUser(appUserId))
            }
            else {
                deleteResult(false)
            }
        }
    }


















    /**
     * delete all trip and all friend - when manager delete account
     */
    private suspend fun deleteInvitedFriendsFromAllMyTrips(
        appUserId: String
    ): Boolean{
        val functionResult = CompletableDeferred<Boolean>()

        var tripsSize = 0

        Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips - start")

        firestoreDb.collection(USERS).document(appUserId)
            .collection(TRIPS)
            .get()
            //get all trip id
            .addOnSuccessListener { trips ->
                tripsSize = trips.size()
                trips.forEach { trip ->

                    val tripId = trip.getDouble("id")?.toInt()
                    Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips - tripId: $tripId")

                    if (tripId != null){
                        //delete all friends from my trip
                        CoroutineScope(Dispatchers.IO).launch {
                            val deleteResult = commonApi.deleteInvitedFriendsFromTrip(
                                tripId = tripId,
                                managerId = appUserId
                            )

                            if (deleteResult) {
                                tripsSize--
                                Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips - remain: $tripsSize")
                            }
                            else {
                                Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips fail")
                                functionResult.complete(false)
                            }

                        }
//                        Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "deleting all friends from all trips / delete all friends from one trip")

                    }
                }
                Log.d(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips - remain trips = 0 return true")
                functionResult.complete(true)
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_DELETE_ACCOUNT_TAG, "delete invited friends from all my trips fail - ", e)
                functionResult.complete(false)
            }

        return functionResult.await()
    }


}