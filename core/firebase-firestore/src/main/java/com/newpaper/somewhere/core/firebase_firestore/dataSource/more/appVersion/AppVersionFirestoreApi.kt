package com.newpaper.somewhere.core.firebase_firestore.dataSource.more.appVersion

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_APP_VERSION_TAG = "Firebase-Firestore-AppVersion"

private const val APP = "app"
private const val APP_INFO = "appInfo"
private const val VERSION_CODE = "versionCode"

class AppVersionFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore
): AppVersionRemoteDataSource {

    override suspend fun getLatestAppVersionCode(): Int?{

        val versionCode = CompletableDeferred<Int?>()

        firestoreDb.collection(APP).document(APP_INFO).get()
            .addOnSuccessListener { document ->
                Log.d(FIREBASE_FIRESTORE_APP_VERSION_TAG, "get latest app version code")
                versionCode.complete(document.getLong(VERSION_CODE)?.toInt())
            }
            .addOnFailureListener { e ->
                Log.e(FIREBASE_FIRESTORE_APP_VERSION_TAG, "get latest app version code fail - ", e)
                versionCode.complete(null)
            }

        return versionCode.await()
    }
}