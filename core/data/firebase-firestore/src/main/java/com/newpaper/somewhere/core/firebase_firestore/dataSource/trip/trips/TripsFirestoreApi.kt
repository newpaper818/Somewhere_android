package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trips

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import com.newpaper.somewhere.core.firebase_common.EDITABLE
import com.newpaper.somewhere.core.firebase_common.FRIEND_ID
import com.newpaper.somewhere.core.firebase_common.MANAGER_ID
import com.newpaper.somewhere.core.firebase_common.SHARED_TRIPS
import com.newpaper.somewhere.core.firebase_common.SHARING_TO
import com.newpaper.somewhere.core.firebase_common.TRIP
import com.newpaper.somewhere.core.firebase_common.TRIPS
import com.newpaper.somewhere.core.firebase_common.TRIP_ID
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.model.TripFirestore
import com.newpaper.somewhere.core.firebase_firestore.model.toTripFirestore
import com.newpaper.somewhere.core.firebase_functions.dataSource.RecursiveDeleteRemoteDataSource
import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_TRIPS_TAG = "Firebase-Firestore-Trips"

class TripsFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val commonApi: CommonRemoteDataSource,
    private val recursiveDeleteApi: RecursiveDeleteRemoteDataSource
): TripsRemoteDataSource {
    override suspend fun getMyTrips(
        internetEnabled: Boolean,
        userId: String
    ): List<Trip> {
        val source = if (internetEnabled) Source.DEFAULT else Source.CACHE
        val tripList = CompletableDeferred<List<Trip>>()

        Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "get my trips - userId: $userId ")

        firestoreDb.collection(USERS).document(userId)
            .collection(TRIPS)
            .orderBy("startDate", Query.Direction.DESCENDING)
            .get(source)
            .addOnSuccessListener { trips ->
                val newTripList = trips.map { it.toObject<TripFirestore>().toTrip(
                    managerId = userId,
                    editable = true,
                    dateList = listOf()
                ) }
                Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "get my trips order by id success")
                tripList.complete(newTripList)
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_TRIPS_TAG, "get my trips order by id fail - ", e)
                tripList.complete(emptyList())
            }

        return tripList.await()
    }

    override suspend fun getSharedTrips(
        internetEnabled: Boolean,
        appUserId: String,
    ): List<Trip> {
        val tripList: MutableList<Trip?> = mutableListOf()

        val sharedTripList = getSharedTrips(appUserId)

        sharedTripList.forEach { sharedTrip ->
            val tripId = sharedTrip[TRIP_ID] as Long
            val managerId = sharedTrip[MANAGER_ID] as String

            val trip = CompletableDeferred<Trip?>()

            getSharedTripInfo(
                internetEnabled = internetEnabled,
                appUserId = appUserId,
                managerId = managerId,
                tripId = tripId.toInt(),
                onSuccess = { tripData ->
                    trip.complete(tripData)
                }
            )
            tripList.add(trip.await())
        }

        return tripList.filterNotNull().sortedByDescending { it.startDate }
    }

    override suspend fun saveTrips(
        appUserId: String?,
        myTripList: List<Trip>,
        deletedTripsIdList: List<Int>,
        deletedSharedTripList: List<Trip>
    ): Boolean {

        if (appUserId == null)
            return false

        if (commonApi.checkUserExist(appUserId) != true)
            return false

        val saveSuccess = CompletableDeferred<Boolean>()

        firestoreDb.runBatch { batch ->

            //save my trips
            myTripList.forEach { trip ->
                saveTripData(firestoreDb, batch, appUserId, trip)
            }

        }.addOnCompleteListener {
            Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "save trips")
            saveSuccess.complete(true)
        }.addOnFailureListener { e ->
            Log.e(FIREBASE_FIRESTORE_TRIPS_TAG, "save trips fail", e)
            saveSuccess.complete(false)
        }

        deletedTripsIdList.forEach { tripId ->
            CoroutineScope(Dispatchers.IO).launch {
                val deleteFriendResult = commonApi.deleteInvitedFriendsFromTrip(
                    tripId = tripId,
                    tripManagerId = appUserId
                )
                Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "save trips - delete friend result: $deleteFriendResult")

                if (deleteFriendResult)
                    recursiveDeleteApi.deleteTrip(
                        tripManagerId = appUserId,
                        tripId = tripId
                    )
            }

        }

        deletedSharedTripList.forEach { sharedTrip ->
            Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "save trips - deleted shared trip friend")
            commonApi.deleteFriendFromTrip(
                tripId = sharedTrip.id,
                tripManagerId = sharedTrip.managerId,
                friendUserId = appUserId,
                onSuccess = {

                },
                onError = {

                }
            )
        }

        return saveSuccess.await()
    }

    override suspend fun updateSharedTripsOrder(
        appUserId: String?,
        sharedTripList: List<Trip>
    ): Boolean {
        val result = CompletableDeferred<Boolean>()

        if (appUserId == null)
            result.complete(false)

        if (appUserId != null && commonApi.checkUserExist(appUserId) != true)
            result.complete(false)

        //sharedTripList id      : 1 5 2 4
        //existed sharedTrips id : 2 4 5 7
        //-> new shared trips id : 5 2 4 7

        if (appUserId != null) {
            val sharedTripsTemp: List<Map<String, Any?>> = sharedTripList.map {
                mapOf(
                    TRIP_ID to it.id,
                    MANAGER_ID to it.managerId
                )
            }

            val appUserRef = firestoreDb.collection(USERS).document(appUserId)

            firestoreDb.runTransaction { transaction ->
                val snapshot = transaction.get(appUserRef)

                val existingSharedTrips =
                    snapshot.get(SHARED_TRIPS) as? MutableList<Map<String, Any?>>
                        ?: emptyList<Map<String, Any>>()

                val newSharedTrips: MutableList<Map<String, Any?>> = sharedTripsTemp.toMutableList()

                newSharedTrips.removeAll { it in existingSharedTrips }

                val additionalTrips = existingSharedTrips.filter {
                    (it[TRIP_ID] as Long).toInt() !in
                            sharedTripsTemp.map { item -> item[TRIP_ID] as Int }
                }
                newSharedTrips.addAll(additionalTrips)

                transaction.update(appUserRef, SHARED_TRIPS, newSharedTrips)
            }
                .addOnSuccessListener { result1 ->
                    Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "update shared trips order: $result1")
                    result.complete(true)
                }.addOnFailureListener { e ->
                    Log.e(FIREBASE_FIRESTORE_TRIPS_TAG, "update shared trips order fail - ", e)
                    result.complete(false)
                }
        }

        return result.await()
    }































    private suspend fun getSharedTrips (
        appUserId: String,
    ): List<Map<String, Any>> {
        val sharedTripList = CompletableDeferred<List<Map<String, Any>>>()

        firestoreDb.collection(USERS).document(appUserId)
            .get()
            .addOnSuccessListener { document ->
                //[<tripId, userId>, <tripId, userId>]
                Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "get shared trips")
                val sharedTrips = document.get(SHARED_TRIPS) as? List<Map<String, Any>> ?: listOf()
                sharedTripList.complete(sharedTrips)
            }
            .addOnFailureListener{exception ->
                Log.e(FIREBASE_FIRESTORE_TRIPS_TAG, "get shared trips fail - ", exception)
            }

        return sharedTripList.await()
    }

    private fun getSharedTripInfo(
        internetEnabled: Boolean,
        appUserId: String,
        managerId: String,
        tripId: Int,
        onSuccess: (Trip?) -> Unit
    ) {
        val source = if (internetEnabled) Source.DEFAULT else Source.CACHE

        firestoreDb.collection(USERS).document(managerId)
            .collection(TRIPS).document("${TRIP}${tripId}")
            .get(source)
            .addOnSuccessListener { document ->

                var editable: Boolean? = null
                val sharingTo = document.get(SHARING_TO) as? List<Map<String, Any>> ?: listOf()
                sharingTo.forEach {
                    if (it[FRIEND_ID] == appUserId){
                        editable = it[EDITABLE] as Boolean
                    }
                }

                if (editable != null) {
                    onSuccess(document.toObject<TripFirestore>()?.toTrip(
                        managerId = managerId,
                        editable = editable!!,
                        dateList = listOf()
                    ))
                }
                else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener{ exception ->
                Log.e(FIREBASE_FIRESTORE_TRIPS_TAG, "get shared trip info fail - ", exception)
                onSuccess(null)
            }
    }

    private fun saveTripData(
        db: FirebaseFirestore,
        batch: WriteBatch,
        userId: String,
        trip: Trip
    ){
        //(default)/users/{userId}/trips/trip{trip.id}
        val tripRef = db.collection(USERS).document(userId)
            .collection(TRIPS).document("$TRIP${trip.id}")

        val tripData = trip.toTripFirestore()
        Log.d(FIREBASE_FIRESTORE_TRIPS_TAG, "save trip data - tripData: $tripData")

        batch.set(tripRef, tripData, SetOptions.merge())
    }
}