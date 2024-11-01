package com.newpaper.somewhere.core.firebase_authentication.dataSource

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId
import com.newpaper.somewhere.core.model.enums.getProviderIdFromString
import com.newpaper.somewhere.core.utils.OAUTH_WEB_CLIENT_ID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

private const val FIREBASE_AUTHENTICATION_TAG = "Firebase-Authentication"

class UserAuthenticationApi @Inject constructor(
//    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val googleOneTapClient: SignInClient
): UserRemoteDataSource {
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun signInWithGoogleIntent(
        intent: Intent,
    ): UserData? {
        val credential = googleOneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            Log.d(FIREBASE_AUTHENTICATION_TAG, "sign in with Google intent - user data: ${user?.uid} ${user?.email} ${user?.displayName} ${user?.photoUrl}")


            if (user == null){
                null
            }
            else {
                UserData(
                    userId = user.uid,
                    userName = user.displayName,
                    email = user.email,
                    profileImagePath = user.photoUrl.toString(),
                    providerIds = user.providerData.mapNotNull { getProviderIdFromString(it.providerId) },
                )
            }

        } catch(e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    override suspend fun getGoogleSignInIntentSender(): IntentSender? {
        val result = try {
            googleOneTapClient.beginSignIn(
                buildGoogleSignInRequest()
            ).await()
        } catch(e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }

        return result?.pendingIntent?.intentSender
    }

    override suspend fun signInWithApple(
        activity: Activity,
        updateIsSigningInToFalse: () -> Unit,
        showErrorSnackbar: () -> Unit
    ): FirebaseUser?{
        val user = CompletableDeferred<FirebaseUser?>()

//        val provider = OAuthProvider.newBuilder(ProviderId.APPLE.id)
//        val language = Locale.getDefault().language
//        provider.addCustomParameter("locale", language)
//
//        val auth = FirebaseAuth.getInstance()
//        val pending = auth.pendingAuthResult
//
//        if (pending != null) {
//            updateIsSigningInToFalse()
//            user.complete(null)
//        }
//        else {
//            auth.startActivityForSignInWithProvider(activity, provider.build())
//                .addOnSuccessListener { authResult ->
//                    // Sign-in successful!
//                    user.complete(authResult.user)
//                }
//                .addOnFailureListener { e ->
//                    if ("canceled by the user" !in e.toString()){
//                        Log.d(FIREBASE_AUTHENTICATION_TAG, "sign in with Apple fail - not cancel by user")
//                        showErrorSnackbar()
//                    }
//
//                    updateIsSigningInToFalse()
//                    Log.w(FIREBASE_AUTHENTICATION_TAG, "sign in with Apple fail - activitySignIn:onFailure", e)
//                    user.complete(null)
//                }
//        }

        return user.await()
    }

    override suspend fun signOut(
        providerIdList: List<ProviderId>,
        signOutResult: (isSignOutSuccess: Boolean) -> Unit
    ){
        try {
            if (ProviderId.GOOGLE in providerIdList)
                googleOneTapClient.signOut().await()

            auth.signOut()

            signOutResult(true)
            Log.d(FIREBASE_AUTHENTICATION_TAG, "sign out")
        } catch(e: Exception) {
            Log.d(FIREBASE_AUTHENTICATION_TAG, "sign out fail")

            signOutResult(false)
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override fun reAuthenticateGoogleUser(
        intent: Intent,
        reAuthResult: (Boolean, Exception?) -> Unit
    ){
        val user = auth.currentUser

        if (user != null) {

            val credential = googleOneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

            //re authenticate
            user.reauthenticate(googleCredentials)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(FIREBASE_AUTHENTICATION_TAG, "re authenticate Google user ")
                        reAuthResult(true, null)
                    } else {
                        Log.e(FIREBASE_AUTHENTICATION_TAG, "re authenticate Google user fail - ", task.exception)
                        reAuthResult(false, task.exception)
                    }
                }
        }
    }

    override fun reAuthenticateAppleUser(
        activity: Activity,
        onSuccess: () -> Unit,
        onFail: (exception: Exception) -> Unit
    ){
//        val user = auth.currentUser
//        val provider = OAuthProvider.newBuilder(ProviderId.APPLE.id)
//        val language = Locale.getDefault().language
//        provider.addCustomParameter("locale", language)
//
//        if (user != null) {
//            user.startActivityForReauthenticateWithProvider(activity, provider.build())
//                .addOnSuccessListener {
//                    onSuccess()
//                    // User is re-authenticated with fresh tokens and
//                    // should be able to perform sensitive operations
//                    // like account deletion and email or password
//                    // update.
//                }
//                .addOnFailureListener { e ->
//                    Log.e(FIREBASE_AUTHENTICATION_TAG, "re authenticate Apple user fail - ", e)
//                    onFail(e)
//                    // Handle failure.
//                }
//        }

    }

    //DELETE USER ==================================================================================
    override fun deleteAuthUser(
        deleteSuccess: (Boolean) -> Unit
    ){
        val user = auth.currentUser

        if (user != null){

            //delete user
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Log.d(FIREBASE_AUTHENTICATION_TAG, "delete user")
                        deleteSuccess(true)
                    }
                    else {
                        Log.e(FIREBASE_AUTHENTICATION_TAG, "delete user fail - ", task.exception)
                        deleteSuccess(false)
                    }
                }
        }
        else {
            Log.d(FIREBASE_AUTHENTICATION_TAG, "delete user fail - user not exit")
            deleteSuccess(false)
        }
    }








    
    
    
    
    
    
    
    
    
    





    private fun buildGoogleSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(OAUTH_WEB_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}