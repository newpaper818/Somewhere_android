package com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.newpaper.somewhere.core.firebase_common.EMAIL
import com.newpaper.somewhere.core.firebase_common.PROFILE_IMAGE_URL
import com.newpaper.somewhere.core.firebase_common.USERS
import com.newpaper.somewhere.core.firebase_common.USER_NAME
import com.newpaper.somewhere.core.model.data.UserData
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

private const val FIREBASE_FIRESTORE_SIGN_IN_TAG = "Firebase-Firestore-SignIn"


class SignInFirestoreApi @Inject constructor(
    private val firestoreDb: FirebaseFirestore
): SignInRemoteDataSource {


    override suspend fun registerUser(
        userData: UserData
    ): Boolean {
        val registerUserSuccess = CompletableDeferred<Boolean>()

        val userRef = firestoreDb.collection(USERS).document(userData.userId)
        Log.d(FIREBASE_FIRESTORE_SIGN_IN_TAG, "register user - user data name email: ${userData.userName} ${userData.email}")
        //if not exit ref, register
        userRef.get()
            .addOnSuccessListener { document ->

                //if user not exit
                if (!document.exists()){

                    //register user
                    firestoreDb.runBatch { batch ->
                        val userDataRegi = hashMapOf(
                            //FIXME!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            USER_NAME to userData.userName,
                            EMAIL to userData.email,
                            PROFILE_IMAGE_URL to userData.profileImagePath,
//                            SHARED_TRIPS to arrayListOf<DocumentReference>()
                        )
                        batch.set(userRef, userDataRegi, SetOptions.merge())

                    }.addOnCompleteListener {
                        Log.d(FIREBASE_FIRESTORE_SIGN_IN_TAG, "register user success")
                        registerUserSuccess.complete(true)
                    }.addOnFailureListener { e ->
                        Log.e(FIREBASE_FIRESTORE_SIGN_IN_TAG, "register user fail - ", e)
                        registerUserSuccess.complete(false)
                    }
                }

                //if user exit already
                else {
                    Log.d(FIREBASE_FIRESTORE_SIGN_IN_TAG, "register user - userId doc already exit")
                    registerUserSuccess.complete(true)
                }

            }
            .addOnFailureListener { exception ->
                Log.e(FIREBASE_FIRESTORE_SIGN_IN_TAG, "register user fail - ", exception)
                registerUserSuccess.complete(false)
            }

        return registerUserSuccess.await()
    }
}