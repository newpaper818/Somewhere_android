package com.newpaper.somewhere.core.firebase_firestore.dataSource.common

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.newpaper.somewhere.core.firebase_common.EDITABLE
import com.newpaper.somewhere.core.firebase_common.EMAIL
import com.newpaper.somewhere.core.firebase_common.FRIEND_ID
import com.newpaper.somewhere.core.firebase_common.MANAGER_ID
import com.newpaper.somewhere.core.firebase_common.PROFILE_IMAGE_URL
import com.newpaper.somewhere.core.firebase_common.SHARED_TRIPS
import com.newpaper.somewhere.core.firebase_common.SHARING_TO
import com.newpaper.somewhere.core.firebase_common.TRIP
import com.newpaper.somewhere.core.firebase_common.TRIPS
import com.newpaper.somewhere.core.firebase_common.TRIP_ID
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_common.USER_NAME
import com.newpaper.somewhere.core.firebase_common.model.UserIdWithSharedTrips
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_COMMON_TAG = "Firebase-Firestore-Common"

class CommonFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore
): CommonRemoteDataSource {

    override suspend fun checkUserExist(
        userId: String
    ): Boolean? {
        val userExit = CompletableDeferred<Boolean?>()

        val userRef = firestoreDb.collection(USERS).document(userId)

        userRef.get()
            .addOnSuccessListener { document ->
                val userDocExit = document.exists()

                Log.d(FIREBASE_FIRESTORE_COMMON_TAG, "check user exist: $userDocExit")

                if (userDocExit)
                    userExit.complete(true)
                else
                    userExit.complete(false)
            }
            .addOnFailureListener { exception ->
                Log.e(FIREBASE_FIRESTORE_COMMON_TAG, "check user exit fail - ", exception)

                userExit.complete(null)
            }
        return userExit.await()
    }

    override fun deleteFriendFromTrip(
        tripId: Int,
        tripManagerId: String,
        friendUserId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        val mangerTripRef = firestoreDb.collection(USERS).document(tripManagerId)
            .collection(TRIPS).document("$TRIP${tripId}")
        val friendUserRef = firestoreDb.collection(USERS).document(friendUserId)

        firestoreDb.runTransaction { transaction ->

            val managerSnapshot = transaction.get(mangerTripRef)
            val friendSnapshot = transaction.get(friendUserRef)

            //update manager sharingTo
            val existingSharingTo = managerSnapshot.get(SHARING_TO) as? MutableList<*> ?: emptyList<Any?>()
            val newSharingTo = existingSharingTo.filter {
                it !in listOf(
                    mapOf(FRIEND_ID to friendUserId, EDITABLE to true),
                    mapOf(FRIEND_ID to friendUserId, EDITABLE to false)
                )
            }

            transaction.update(mangerTripRef, SHARING_TO, newSharingTo)

            //update friend sharedTrips
            val sharedTrips = friendSnapshot.get(SHARED_TRIPS) as? MutableList<Map<String, Any>> ?: mutableListOf()

            val removeItem = mapOf(TRIP_ID to tripId, MANAGER_ID to tripManagerId)
            sharedTrips.removeAll {
                it.toString() == removeItem.toString()
            }

            transaction.update(friendUserRef, SHARED_TRIPS, sharedTrips)
        }
            .addOnSuccessListener { result ->
                Log.d(FIREBASE_FIRESTORE_COMMON_TAG, "delete friend from trip: $result")
                onSuccess()
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_COMMON_TAG, "delete friend from trip failure - ", e)
                onError()
            }
    }

    override suspend fun deleteInvitedFriendsFromTrip(
        tripId: Int,
        managerId: String
    ): Boolean {
        val deleteResult = CompletableDeferred<Boolean>()

        val managerTripRef = firestoreDb.collection(USERS).document(managerId)
            .collection(TRIPS).document("${TRIP}${tripId}")

        firestoreDb.runTransaction { transaction ->

            //get all friends id from manager's sharingTo
            val managerSnapshot = transaction.get(managerTripRef)
            val sharingTo = managerSnapshot.get(SHARING_TO) as? List<Map<String, Any>> ?: emptyList()

            val friendIdListWithSharedTripsList: List<UserIdWithSharedTrips> = sharingTo.map {
                UserIdWithSharedTrips(
                    userId = it[FRIEND_ID].toString(),
                    sharedTrips = null
                )
            }

            //update each friend sharedTrips
            //read first and make new sharedTrips
            friendIdListWithSharedTripsList.forEach { item ->
                val friendUserRef = firestoreDb.collection(USERS).document(item.userId)
                val friendSnapshot = transaction.get(friendUserRef)
                val existingSharedTrips = friendSnapshot.get(SHARED_TRIPS) as? List<*> ?: emptyList<Any?>()

                val newSharedTrips = existingSharedTrips.filter {
                    it.toString() != mapOf(TRIP_ID to tripId, MANAGER_ID to managerId).toString() &&
                            it.toString() != mapOf(MANAGER_ID to managerId, TRIP_ID to tripId).toString()
                }

                item.sharedTrips = newSharedTrips
            }

            //write later
            friendIdListWithSharedTripsList.forEach { item ->
                val friendUserRef = firestoreDb.collection(USERS).document(item.userId)
                transaction.update(friendUserRef, SHARED_TRIPS, item.sharedTrips)
            }

        }
            .addOnSuccessListener { result ->
                Log.d(FIREBASE_FIRESTORE_COMMON_TAG, "delete invited friends from trip - $result")
                deleteResult.complete(true)
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_COMMON_TAG, "delete invited friends from trip fail - ", e)
                deleteResult.complete(false)
            }

        return deleteResult.await()
    }

    override suspend fun getUserInfo(
        userId: String,
        providerIds: List<ProviderId>
    ): UserData? {
        val userData = CompletableDeferred<UserData?>()

        firestoreDb.collection(USERS).document(userId).get()
            .addOnSuccessListener { document ->
                val userName = document.getString(USER_NAME)
                val email = document.getString(EMAIL)
                val profileImagePath = document.getString(PROFILE_IMAGE_URL)

                Log.d(FIREBASE_FIRESTORE_COMMON_TAG, "getUserInfo - user info: $userId $userName $email")

                userData.complete(
                    UserData(
                        userId = userId,
                        userName = userName,
                        email = email,
                        profileImagePath = profileImagePath,
                        providerIds = providerIds,
                    )
                )
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_COMMON_TAG, "get user info fail", e)
                userData.complete(null)
            }

        return userData.await()
    }
}