package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.trip

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import com.newpaper.somewhere.core.firebase_common.DATE
import com.newpaper.somewhere.core.firebase_common.DATE_LIST
import com.newpaper.somewhere.core.firebase_common.TRIP
import com.newpaper.somewhere.core.firebase_common.TRIPS
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.model.DateFirestore
import com.newpaper.somewhere.core.firebase_firestore.model.TripFirestore
import com.newpaper.somewhere.core.firebase_firestore.model.toDateFirestore
import com.newpaper.somewhere.core.firebase_firestore.model.toTripFirestore
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.model.tripData.Trip
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_TRIP_TAG = "Firebase-Firestore-Trip"

class TripFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore,
    private val commonApi: CommonRemoteDataSource
): TripRemoteDataSource {

    /** get trip's all data (include date list and spot list) */
    override suspend fun getTrip(
        internetEnabled: Boolean,
        tripManagerId: String,
        appUserId: String,
        tripId: Int,
        editable: Boolean
    ): Trip? {
        val source = if (internetEnabled) Source.DEFAULT else Source.CACHE

        val trip = CompletableDeferred<Trip?>()

        //get trip's dateList
        firestoreDb.collection(USERS).document(tripManagerId)
            .collection(TRIPS).document("${TRIP}${tripId}")
            .collection(DATE_LIST)
            .get(source)
            .addOnSuccessListener {dates ->
                val newDateList = dates.map { it.toObject<DateFirestore>().toDate() }
                Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "get trip data - tripId: $tripId")

                //get trip
                firestoreDb.collection(USERS).document(tripManagerId)
                    .collection(TRIPS).document("${TRIP}${tripId}")
                    .get(source)
                    .addOnSuccessListener {document ->
                        val newTrip = document.toObject<TripFirestore>()?.toTrip(
                            managerId = tripManagerId,
                            appUserId = appUserId,
                            dateList = newDateList,
                            editable = editable
                        )
                        Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "get trip data - tripId: $tripId")
                        trip.complete(newTrip)
                    }
                    .addOnFailureListener {e ->
                        Log.e(FIREBASE_FIRESTORE_TRIP_TAG, "get trip data fail - ", e)
                        trip.complete(null)
                    }

            }
            .addOnFailureListener {e ->
                Log.e(FIREBASE_FIRESTORE_TRIP_TAG, "get trip data - get dates fail - ", e)
                trip.complete(null)
            }

        return trip.await()
    }

    override suspend fun saveTripAndAllDates(
        trip: Trip,
        tempTripDateListLastIndex: Int?
    ):Boolean {

        if (commonApi.checkUserExist(trip.managerId) != true)
            return false

        val saveSuccess = CompletableDeferred<Boolean>()

        firestoreDb.runBatch { batch ->

            //save trip
            saveTripData(firestoreDb, batch, trip)

            //save dateList and spotList
            val enabledDateList = trip.dateList.filter { it.enabled }

            enabledDateList.forEach { date ->
                saveDateData(firestoreDb, batch, trip.managerId, trip.id, date)
            }

            if (tempTripDateListLastIndex != null) {
                //delete un enabled Date
                for (dateIndex in enabledDateList.size..tempTripDateListLastIndex) {
                    deleteDateData(firestoreDb, batch, trip.managerId, trip.id, dateIndex)
                }
            }
        }
        .addOnCompleteListener {
            Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "save trip and all dates")
            saveSuccess.complete(true)
        }
        .addOnFailureListener { e ->
            Log.e(FIREBASE_FIRESTORE_TRIP_TAG, "save trip and all dates fail", e)
            saveSuccess.complete(false)
        }

        return saveSuccess.await()
    }

























    private fun saveTripData(
        db: FirebaseFirestore,
        batch: WriteBatch,
        trip: Trip
    ){
        //(default)/users/{userId}/trips/trip{trip.id}
        val tripRef = db.collection(USERS).document(trip.managerId)
            .collection(TRIPS).document("$TRIP${trip.id}")

        val tripData = trip.toTripFirestore()
        Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "save trip data - tripData: $tripData")

        batch.set(tripRef, tripData, SetOptions.merge())
    }

    private fun saveDateData(
        db: FirebaseFirestore,
        batch: WriteBatch,
        tripManagerId: String,
        tripId: Int,
        date: Date
    ){
        //(default)/users/{userId}/trips/trip{trip.id}/dateList/date{date.id}
        val dateRef = db.collection(USERS).document(tripManagerId)
            .collection(TRIPS).document("$TRIP$tripId")
            .collection(DATE_LIST).document("$DATE${date.index}")

        val dateData = date.toDateFirestore()

        batch.set(dateRef, dateData, SetOptions.merge())
        Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "save date data - date index: ${date.index}")

    }

    private fun deleteDateData(
        db: FirebaseFirestore,
        batch: WriteBatch,
        tripManagerId: String,
        tripId: Int,
        dateIndex: Int
    ){
        //(default)/users/{userId}/trips/trip{trip.id}/dateList/date{date.id}
        val dateRef = db.collection(USERS).document(tripManagerId)
            .collection(TRIPS).document("$TRIP$tripId")
            .collection(DATE_LIST).document("$DATE$dateIndex")

        batch.delete(dateRef)
        Log.d(FIREBASE_FIRESTORE_TRIP_TAG, "delete date data - date index: $dateIndex")
    }
}