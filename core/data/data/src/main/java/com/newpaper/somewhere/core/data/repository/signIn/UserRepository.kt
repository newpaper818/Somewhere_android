package com.newpaper.somewhere.core.data.repository.signIn

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn.SignInRemoteDataSource
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import com.newpaper.somewhere.core.model.enums.getProviderIdFromString
import kotlinx.coroutines.CompletableDeferred
import java.util.Locale
import javax.inject.Inject

private const val USER_REPOSITORY_TAG = "User-Repository"

class UserRepository @Inject constructor(
    private val commonRemoteDataSource: CommonRemoteDataSource, //firebase-firestore
    private val signInRemoteDataSource: SignInRemoteDataSource, //firebase-firestore
    private val userRemoteDatasource: UserRemoteDataSource      //firebase-authentication
) {

    suspend fun signInLaunchGoogleLauncher(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        signInError : () -> Unit
    ){
        val signInIntentSender = userRemoteDatasource.getGoogleSignInIntentSender()

        if (signInIntentSender == null) {
            signInError()
        }

        launcher.launch(
            IntentSenderRequest.Builder(
                signInIntentSender ?: return
            ).build()
        )
    }

    suspend fun signInWithGoogleIntent(
        intent: Intent
    ): UserData? {
        return userRemoteDatasource.signInWithGoogleIntent(intent)
    }


    suspend fun signInWithApple(
        activity: Activity,
        updateIsSigningInToFalse: () -> Unit,
        showErrorSnackbar: () -> Unit
    ): FirebaseUser? {
        val user = CompletableDeferred<FirebaseUser?>()

        val provider = OAuthProvider.newBuilder(ProviderId.APPLE.id)
        val language = Locale.getDefault().language
        provider.addCustomParameter("locale", language)

        val auth = FirebaseAuth.getInstance()
        val pending = auth.pendingAuthResult

        if (pending != null) {
            updateIsSigningInToFalse()
            user.complete(null)
        }
        else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    // Sign-in successful!
                    user.complete(authResult.user)
                }
                .addOnFailureListener { e ->
                    if ("canceled by the user" !in e.toString()){
                        Log.d(USER_REPOSITORY_TAG, "not cancel by user")
                        showErrorSnackbar()
                    }

                    updateIsSigningInToFalse()
                    Log.w(USER_REPOSITORY_TAG, "activitySignIn:onFailure", e)
                    user.complete(null)
                }
        }

        return user.await()
    }


    suspend fun updateUserDataFromRemote(
        userData: UserData,
        setIsSigningIn: (Boolean) -> Unit,
        onDone: (userData: UserData) -> Unit,
        showErrorSnackbar: () -> Unit
    ) {
        Log.d(USER_REPOSITORY_TAG, "updateUserDataFromSignInResult - ${userData.userId} ${userData.userName} ${userData.email}")

        //is userData exit in remote(firestore)?
        val userExitInRemote = commonRemoteDataSource.checkUserExist(userData.userId)

        if (userExitInRemote == null){
            setIsSigningIn(false)
            showErrorSnackbar()
        }
        else {
            var newUserData: UserData? = null

            //if exit user in firestore -> get user data from firestore
            if (userExitInRemote == true){
                newUserData = commonRemoteDataSource.getUserInfo(
                    userId = userData.userId,
                    providerIds = userData.providerIds
                )
            }
            //if not exit -> register user to firestore
            else if (userExitInRemote == false){
                signInRemoteDataSource.registerUser(userData)
                newUserData = userData
            }

            //update userViewModel
            if (newUserData != null) {
                onDone(newUserData)
            }
            else{
                showErrorSnackbar()
                setIsSigningIn(false)
            }
        }
    }






    suspend fun getSignedInUser(

    ): UserData? {
        val firebaseUser = userRemoteDatasource.getCurrentUser()

        //if null user return null
        if (firebaseUser == null){
            Log.d(USER_REPOSITORY_TAG, "getSignedInUser - user null")
            return null
        }
        else {
            return commonRemoteDataSource.getUserInfo(
                userId = firebaseUser.uid,
                providerIds = firebaseUser.providerData.mapNotNull { getProviderIdFromString(it.providerId) }
            )
        }
    }


    suspend fun checkUserExist(
        userId: String
    ): Boolean? {
        return commonRemoteDataSource.checkUserExist(
            userId = userId
        )
    }

    suspend fun getUserInfo(
        userId: String,
        providerIds: List<ProviderId>
    ): UserData? {
        return commonRemoteDataSource.getUserInfo(
            userId = userId,
            providerIds = providerIds
        )
    }

    suspend fun registerUser(
        userData: UserData
    ): Boolean {
        return signInRemoteDataSource.registerUser(userData)
    }
















    suspend fun signOut(
        providerIdList: List<ProviderId>,
        signOutResult: (isSignOutSuccess: Boolean) -> Unit
    ){
        userRemoteDatasource.signOut(
            providerIdList = providerIdList,
            signOutResult = signOutResult

        )
    }
}