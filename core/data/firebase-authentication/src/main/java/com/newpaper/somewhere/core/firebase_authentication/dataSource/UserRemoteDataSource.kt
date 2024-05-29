package com.newpaper.somewhere.core.firebase_authentication.dataSource

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import com.google.firebase.auth.FirebaseUser
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ProviderId

interface UserRemoteDataSource {
    fun getCurrentUser(): FirebaseUser?

    suspend fun signInWithGoogleIntent(
        intent: Intent,
    ): UserData?


    suspend fun getGoogleSignInIntentSender(): IntentSender?


    suspend fun signInWithApple(
        activity: Activity,
        updateIsSigningInToFalse: () -> Unit,
        showErrorSnackbar: () -> Unit
    ): FirebaseUser?


    suspend fun signOut(
        providerIdList: List<ProviderId>,
        signOutResult: (isSignOutSuccess: Boolean) -> Unit
    )

    fun reAuthenticateGoogleUser(
        intent: Intent,
        reAuthResult: (Boolean, Exception?) -> Unit
    )

    fun reAuthenticateAppleUser(
        activity: Activity,
        onSuccess: () -> Unit,
        onFail: (exception: Exception) -> Unit
    )

    fun deleteAuthUser(
        deleteSuccess: (Boolean) -> Unit
    )
}