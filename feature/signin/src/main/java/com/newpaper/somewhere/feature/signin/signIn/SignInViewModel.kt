package com.newpaper.somewhere.feature.signin.signIn

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.newpaper.somewhere.core.data.repository.image.ImageRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SIGN_IN_VIEWMODEL_TAG = "SignIn-ViewModel"

data class SignInUiState(
    val isDarkAppTheme: Boolean,
    val isSigningIn: Boolean = false,
    val signInButtonEnabled: Boolean = true
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
): ViewModel() {
    private val _signInUiState: MutableStateFlow<SignInUiState> =
        MutableStateFlow(
            SignInUiState(false)
        )

    val signInUiState = _signInUiState.asStateFlow()

    fun deleteAllLocalImages(
        context: Context
    ){
        imageRepository.deleteAllImagesFromInternalStorage(context)
    }

    fun setIsSigningIn(
        isSigningIn: Boolean
    ) {
        _signInUiState.update {
            it.copy(
                isSigningIn = isSigningIn
            )
        }
    }

    fun setSignInButtonEnabled(
        signInButtonEnabled: Boolean
    ) {
        _signInUiState.update {
            it.copy(
                signInButtonEnabled = signInButtonEnabled
            )
        }
    }

    suspend fun signInLaunchGoogleLauncher(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        showErrorSnackbar: () -> Unit
    ) {
        setIsSigningIn(true)
        userRepository.signInLaunchGoogleLauncher(
            launcher = launcher,
            signInError = {
                setIsSigningIn(false)
                showErrorSnackbar()
            }
        )
    }

    suspend fun signInWithGoogleResult(
        result: ActivityResult,
        onDone: (userData: UserData) -> Unit,
        showErrorSnackbar: () -> Unit
    ){
        if (result.resultCode == Activity.RESULT_OK){
            if (result.data == null){
                setIsSigningIn(false)
                return
            }

            //get signInResult from remote(firebase)
            val userData = userRepository.signInWithGoogleIntent(
                intent = result.data!!
            )

            Log.d(SIGN_IN_VIEWMODEL_TAG, "signInWithGoogleResult - userData : $userData")

            updateUserDataFromRemote(
                userData = userData,
                onDone = onDone,
                showErrorSnackbar = showErrorSnackbar
            )
        }
        else {
            setIsSigningIn(false)
        }
    }



    suspend fun signInWithApple(
        activity: Activity,
        showErrorSnackbar: () -> Unit
    ): FirebaseUser? {
        setIsSigningIn(true)
        return userRepository.signInWithApple(
            activity = activity,
            updateIsSigningInToFalse = {
                setIsSigningIn(false)
            },
            showErrorSnackbar = { showErrorSnackbar() }
        )
    }


    suspend fun updateUserDataFromRemote(
        userData: UserData?,
        onDone: (userData: UserData) -> Unit,
        showErrorSnackbar: () -> Unit
    ) {
        if (userData == null){
            setIsSigningIn(false)
            showErrorSnackbar()
        }
        else {
            //check user exit and
            //  if exit, get user data from firestore
            //  else, register user data to firestore
            userRepository.updateUserDataFromRemote(
                userData = userData,
                setIsSigningIn = { setIsSigningIn(it) },
                onDone = onDone,
                showErrorSnackbar = showErrorSnackbar
            )
        }
    }

}