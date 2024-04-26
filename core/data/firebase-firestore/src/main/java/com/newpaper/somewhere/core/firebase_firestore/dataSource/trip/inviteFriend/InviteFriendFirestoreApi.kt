package com.newpaper.somewhere.core.firebase_firestore.dataSource.trip.inviteFriend

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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
import com.newpaper.somewhere.core.model.data.UserData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_INVITE_FRIEND_TAG = "Firebase-Firestore-InviteFriend"


class InviteFriendFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore
): InviteFriendRemoteDataSource {

    override fun getInvitedFriends(
        internetEnabled: Boolean,
        managerId: String,
        tripId: Int,

        onSuccess: (friendList: List<UserData>) -> Unit,
        onError: () -> Unit
    ) {
        val source = if (internetEnabled) Source.DEFAULT else Source.CACHE

        firestoreDb.collection(USERS).document(managerId)
            .collection(TRIPS).document("${TRIP}${tripId}")
            .get(source)
            .addOnSuccessListener {document ->
                Log.d(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "get invited friend")

                val sharingToList = document.get(SHARING_TO) as? List<Map<String, Any>> ?: listOf()

                val sharingToListWithManager = sharingToList.toMutableList()
                sharingToListWithManager.add(0, mapOf(EDITABLE to true, FRIEND_ID to managerId))

                CoroutineScope(Dispatchers.IO).launch {
                    val friendList = getUserAndConvertToUserDataList(sharingToListWithManager, source)
                    onSuccess(friendList)
                }

            }
            .addOnFailureListener {e ->
                Log.e(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "get invited friend fail - ", e)
                onError()
            }
    }

    override suspend fun checkAndAddSharingTripFriend(
        tripId: Int,
        myEmail: String,
        myUserId: String,
        friendUserEmail: String,
        editable: Boolean,
        onSuccess: () -> Unit,
        onErrorSnackbar: () -> Unit,
        onMyEmailSnackbar: () -> Unit,
        onInvalidEmailSnackbar: () -> Unit,
        onNoUserSnackbar: () -> Unit
    ) {

        if (myEmail == friendUserEmail){
            onMyEmailSnackbar()
            return
        }

        if (friendUserEmail.isEmpty() || "@" !in friendUserEmail){
            onInvalidEmailSnackbar()
            return
        }

        val (friendUserId, success) = getUserIDFromEmail(friendUserEmail)
        if (friendUserId != null && success){
            val addSuccess = addSharingTripFriend(
                tripId = tripId,
                managerId = myUserId,
                friendId = friendUserId,
                editable = editable
            )

            return if (addSuccess){
                //add complete
                onSuccess()
            } else {
                //error - error do it later
                onErrorSnackbar()
            }
        }
        else if (friendUserId == null && success){
            //user not exit
            onNoUserSnackbar()
        }
        else {
            //error - error do it later
            onErrorSnackbar()
        }
    }

    override fun setFriendPermission(
        tripId: Int,
        myUserId: String,
        friendUserData: UserData,

        onSuccess: () -> Unit,
        onError: () -> Unit
    ){
        var haveToWrite = true

        val ref = firestoreDb.collection(USERS).document(myUserId)
            .collection(TRIPS).document("${TRIP}${tripId}")

        firestoreDb.runTransaction{ transaction ->
            val snapshot = transaction.get(ref)

            val existingSharingTo = snapshot.get(SHARING_TO) as? MutableList<*> ?: emptyList<Any?>()
            val newSharingTo = existingSharingTo.map {
                if (
                    it == mapOf(
                        FRIEND_ID to friendUserData.userId,
                        EDITABLE to friendUserData.allowEdit
                    )
                ) {
                    haveToWrite = false
                    it
                }

                else if (
                    it == mapOf(
                        FRIEND_ID to friendUserData.userId,
                        EDITABLE to !friendUserData.allowEdit
                    )
                ) {
                    haveToWrite = true
                    mapOf(
                        FRIEND_ID to friendUserData.userId,
                        EDITABLE to friendUserData.allowEdit
                    )
                }

                else it
            }

            if (haveToWrite){
                transaction.update(ref, SHARING_TO, newSharingTo)
            }
        }
            .addOnSuccessListener { result ->
                Log.d(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "set friend permission: $result")
                onSuccess()
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "set friend permission fail - ", e)
                onError()
            }
    }























    private suspend fun getUserAndConvertToUserDataList(
        friendMapList: List<Map<String, Any>>,
        source: Source
    ): List<UserData> {
        val friendList: MutableList<UserData> = mutableListOf()

        friendMapList.forEach {
            val editable = it[EDITABLE] as Boolean
            val friendId = it[FRIEND_ID] as String

            val friendData = CompletableDeferred<UserData>()

            firestoreDb.collection(USERS).document(friendId)
                .get(source)
                .addOnSuccessListener {document ->
                    friendData.complete(UserData(
                        userId = friendId,
                        userName = document.getString(USER_NAME),
                        email = document.getString(EMAIL),
                        profileImage = document.getString(PROFILE_IMAGE_URL),
                        providerIdList = listOf(),
                        allowEdit = editable
                    ))
                }
                .addOnFailureListener {
                    friendData.complete(
                        UserData(
                            userId = friendId,
                            userName = null,
                            email = null,
                            profileImage = null,
                            providerIdList = listOf(),
                            allowEdit = editable
                        )
                    )
                }

            friendList.add(friendData.await())
        }

        return friendList
    }

    private suspend fun getUserIDFromEmail(
        userEmail: String
    ):Pair<String?, Boolean> {
        val userId = CompletableDeferred<Pair<String?, Boolean>>()

        firestoreDb.collection(USERS)
            .whereEqualTo(EMAIL, userEmail).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0){
                    for (document in documents){
                        userId.complete(Pair(document.id, true))
                        break
                    }
                }
                else
                    userId.complete(Pair(null, true))
            }
            .addOnFailureListener{e ->
                Log.e(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "get userId from email fail - ", e)
                userId.complete(Pair(null, false))
            }

        return userId.await()
    }

    private suspend fun addSharingTripFriend(
        tripId: Int,
        managerId: String,
        friendId: String,
        editable: Boolean
    ): Boolean {

        val addSuccess = CompletableDeferred<Boolean>()

        val myTripRef = firestoreDb.collection(USERS).document(managerId)
            .collection(TRIPS).document("${TRIP}${tripId}")
        val friendUserRef = firestoreDb.collection(USERS).document(friendId)


        firestoreDb.runTransaction { transaction ->

            val mySnapshot = transaction.get(myTripRef)
            val friendSnapshot = transaction.get(friendUserRef)

            //update my trip{tripId} sharingTo
            val existingSharingTo = mySnapshot.get(SHARING_TO) as? List<Map<String, Any>> ?: emptyList()
            val newData = mapOf(FRIEND_ID to friendId, EDITABLE to editable)
            val oppositeNewData = mapOf(FRIEND_ID to friendId, EDITABLE to !editable)

            var addNewData = true

            var newSharingTo = existingSharingTo.map {
                when (it) {
                    newData -> addNewData = false
                    oppositeNewData -> {
                        addNewData = false
                        newData
                    }
                    else -> it
                }
            }

            if (addNewData)
                newSharingTo = newSharingTo + newData

            if (newSharingTo != existingSharingTo && newSharingTo != listOf<Any?>(Unit)) {
                transaction.update(myTripRef, SHARING_TO, newSharingTo)
            }

            //update friend sharedTrips
            val existingSharedTrips = friendSnapshot.get(SHARED_TRIPS) as? List<Map<String, Any>> ?: emptyList()
            val friendNewData = mapOf<String, Any>(TRIP_ID to tripId, MANAGER_ID to managerId)

            var addFriendNewData = true
            for (item in existingSharedTrips){
                if (item.toString() == friendNewData.toString()){
                    //do nothing
                    addFriendNewData = false
                    break
                }
            }

            if (addFriendNewData){
                val newSharedTrips = existingSharedTrips + friendNewData
                transaction.update(friendUserRef, SHARED_TRIPS, newSharedTrips)
            }
        }
            .addOnSuccessListener { result ->
                Log.d(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "add sharing trip friend: $result")
                addSuccess.complete(true)
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_INVITE_FRIEND_TAG, "add sharing trip friend fail - ", e)
                addSuccess.complete(false)
            }

        return addSuccess.await()
    }
}