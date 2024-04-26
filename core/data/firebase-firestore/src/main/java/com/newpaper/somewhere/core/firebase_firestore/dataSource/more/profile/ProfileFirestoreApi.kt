package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.profile

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.newpaper.somewhere.core.firebase_common.PROFILE_IMAGE_URL
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_common.USER_NAME
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_PROFILE_TAG = "Firebase-Firestore-Profile"

class ProfileFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore
): ProfileRemoteDataSource {

    override fun updateUserProfile(
        appUserId: String?,
        newUserName: String?,
        newProfileImage: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        if (appUserId != null) {
            val userRef = firestoreDb.collection(USERS).document(appUserId)

            val userData = hashMapOf(
                USER_NAME to newUserName,
                PROFILE_IMAGE_URL to newProfileImage
            )

            firestoreDb.runBatch { batch ->
                batch.set(userRef, userData, SetOptions.merge())
            }.addOnCompleteListener {
                Log.d(FIREBASE_FIRESTORE_PROFILE_TAG, "update user profile")
                onSuccess()
            }.addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_PROFILE_TAG, "update user profile fail", e)
                onFailure()
            }
        }
    }
}