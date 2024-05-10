package com.newpaper.somewhere.core.data.repository.signIn

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.newpaper.somewhere.core.firebase_authentication.dataSource.UserRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.common.CommonRemoteDataSource
import com.newpaper.somewhere.core.firebase_firestore.dataSource.signIn.SignInRemoteDataSource
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import javax.inject.Inject

private const val SIGN_IN_REPOSITORY_TAG = "SignIn-Repository"

class SignInRepository @Inject constructor(
    private val commonRemoteDataSource: CommonRemoteDataSource, //firebase-firestore
    private val signInRemoteDataSource: SignInRemoteDataSource, //firebase-firestore
    private val userRemoteDatasource: UserRemoteDataSource      //firebase-authentication
) {
    suspend fun registerUser(
        userData: UserData
    ){
        signInRemoteDataSource.registerUser(
            userData = userData
        )
    }

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


    suspend fun updateUserDataFromRemote(
        userData: UserData,
        setIsSigningIn: (Boolean) -> Unit,
        showErrorSnackbar: () -> Unit
    ) {
        Log.d(SIGN_IN_REPOSITORY_TAG, "updateUserDataFromSignInResult - ${userData.userId} ${userData.userName} ${userData.email}")

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
            if (newUserData != null){
                //FIXME set start destination
                //FIXME set user
                //FIXME navigate screen


//                appViewModel.updateCurrentMainNavDestination(MyTripsMainDestination)
//                updateUserData(newUserData)
//
//                navController.navigate(MainScreenDestination.route) {
//                    popUpTo(SignInScreenDestination.route) { inclusive = true }
//                }
            }
            else{
                showErrorSnackbar()
                setIsSigningIn(false)
            }
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
}