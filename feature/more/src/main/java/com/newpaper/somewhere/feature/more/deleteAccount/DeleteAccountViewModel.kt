package com.newpaper.somewhere.feature.more.deleteAccount

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.image.GetImageRepository
import com.newpaper.somewhere.core.data.repository.more.DeleteAccountRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.enums.ProviderId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeleteAccountUiState(
    val authButtonEnabled: Boolean = true,
    val authingWith: ProviderId? = null,
    val isAuthing: Boolean = false,
    val isAuthDone: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deleteAccountProgress: Int = 0
)

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val getImageRepository: GetImageRepository,
    private val deleteAccountRepository: DeleteAccountRepository,
    private val userRepository: UserRepository,
): ViewModel() {
    private val _deleteAccountUiState = MutableStateFlow(DeleteAccountUiState())
    val deleteAccountUiState = _deleteAccountUiState.asStateFlow()



    //==============================================================================================
    //get image ====================================================================================
    fun getImage(
        imagePath: String,
        imageUserId: String,
        result: (Boolean) -> Unit
    ){
        getImageRepository.getImage(
            imagePath = imagePath,
            imageUserId = imageUserId,
            result = result
        )
    }



    //==============================================================================================
    //re authenticate ==============================================================================
    fun reAuthenticate(
        activity: Activity,
        providerId: ProviderId,
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        showReAuthErrorSnackbar: () -> Unit,
        showReAuthErrorUserNotMatchSnackbar: () -> Unit
    ){
        setAuthButtonEnabled(false)
        setAuthingWith(providerId)

        viewModelScope.launch {
            when (providerId){
                ProviderId.GOOGLE -> {
                    userRepository.reAuthLaunchGoogleLauncher(
                        launcher = launcher,
                        setIsAuthing = { setIsAuthing(it) },
                        showErrorSnackbar = showReAuthErrorSnackbar
                    )
                }
                ProviderId.APPLE -> {
                    userRepository.reAuthenticateAppleUser(
                        activity = activity,
                        setIsAuthing = { setIsAuthing(it) },
                        setIsAuthDone = { setIsAuthDone(it) },
                        showReAuthErrorSnackbar = showReAuthErrorSnackbar,
                        showReAuthErrorUserNotMatchSnackbar = showReAuthErrorUserNotMatchSnackbar
                    )
                }
            }
        }

    }

    fun reAuthGoogleResult(
        result: ActivityResult,
        showReAuthErrorSnackbar: () -> Unit,
        showReAuthErrorUserNotMatchSnackbar: () -> Unit
    ){
        userRepository.reAuthGoogleResult(
            result = result,
            setIsAuthing = { setIsAuthing(it) },
            setIsAuthDone = { setIsAuthDone(it) },
            showReAuthErrorSnackbar = showReAuthErrorSnackbar,
            showReAuthErrorUserNotMatchSnackbar = showReAuthErrorUserNotMatchSnackbar
        )
    }



    //==============================================================================================
    //delete account ===============================================================================
    /**
     * ⚠️ DELETE ACCOUNT ⚠️
     */
    fun deleteAccount(
        appUserId: String,
        deleteResult: (Boolean) -> Unit,
    ){
        viewModelScope.launch {
            setIsDeletingAccount(true)

            val sharedTrips = deleteAccountRepository.getSharedTrips(appUserId)

            deleteAccountRepository.deleteAccount(
                appUserId = appUserId,
                sharedTripList = sharedTrips,
                deleteResult = deleteResult,
                increaseDeleteAccountProgress = { increaseDeleteAccountProgress() } //1 ~ 3
            )
        }
    }

    fun deleteAuthUser(
        deleteSuccess: (Boolean) -> Unit
    ){
        userRepository.deleteAuthUser(
            deleteSuccess = deleteSuccess
        )
    }



    //==============================================================================================
    //sign out =====================================================================================
    suspend fun signOut(
        providerIdList: List<ProviderId>,
        signOutResult: (isSignOutSuccess: Boolean) -> Unit
    ){
        userRepository.signOut(
            providerIdList = providerIdList,
            signOutResult = signOutResult
        )
    }









    //==============================================================================================
    //set UiState ==================================================================================
    fun setAuthButtonEnabled(
        authButtonEnabled: Boolean
    ){
        _deleteAccountUiState.update {
            it.copy(
                authButtonEnabled = authButtonEnabled
            )
        }
    }

    fun setAuthingWith(
        authingWith: ProviderId?
    ){
        _deleteAccountUiState.update {
            it.copy(
                authingWith = authingWith
            )
        }
    }

    private fun setIsAuthing(
        isAuthing: Boolean
    ){
        _deleteAccountUiState.update {
            it.copy(
                isAuthing = isAuthing
            )
        }
    }

    private fun setIsAuthDone(
        isAuthDone: Boolean
    ){
        _deleteAccountUiState.update {
            it.copy(
                isAuthDone = isAuthDone
            )
        }
    }

    fun setIsDeletingAccount(
        isDeletingAccount: Boolean
    ){
        _deleteAccountUiState.update {
            it.copy(
                isDeletingAccount = isDeletingAccount
            )
        }
    }

    fun increaseDeleteAccountProgress(

    ){
        _deleteAccountUiState.update {
            it.copy(
                deleteAccountProgress = it.deleteAccountProgress + 1
            )
        }
    }

}