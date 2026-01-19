package com.newpaper.somewhere.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.PurchasesUpdatedListener
import com.newpaper.somewhere.core.data.repository.PreferencesRepository
import com.newpaper.somewhere.core.data.repository.more.SubscriptionRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.Theme
import com.newpaper.somewhere.core.model.data.UserData
import com.newpaper.somewhere.core.model.enums.ScreenDestination
import com.newpaper.somewhere.navigationUi.TopLevelDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val APP_VIEWMODEL_TAG = "App-ViewModel"

data class AppPreferencesState(
    val theme: Theme = Theme(),
    val dateTimeFormat: DateTimeFormat = DateTimeFormat()
)

data class DestinationState(
    val startScreenDestination: ScreenDestination? = null, //if not null, splash screen will be finish
    val moreDetailStartScreenDestination: ScreenDestination = ScreenDestination.SET_DATE_TIME_FORMAT,
    val currentTopLevelDestination: TopLevelDestination = TopLevelDestination.TRIPS,
    val currentScreenDestination: ScreenDestination = ScreenDestination.SIGN_IN
)

data class AppUiState(
    val appUserData: UserData? = null,
    val appPreferences: AppPreferencesState = AppPreferencesState(),
    val screenDestination: DestinationState = DestinationState(),

//    val showSplashScreen: Boolean = true,
    val firstLaunch: Boolean = true,
//    val isEditMode: Boolean = false
)



@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {
    private val _appUiState = MutableStateFlow(AppUiState())
    val appUiState = _appUiState.asStateFlow()

    //==============================================================================================
    //update app setting values - at app start =====================================================
    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAppPreferencesValue()
        }
    }

    suspend fun getAppPreferencesValue(){
        preferencesRepository.getAppPreferencesValue { theme, dateTimeFormat ->
            _appUiState.update {
                it.copy(
                    appPreferences = it.appPreferences.copy(
                        theme = theme,
                        dateTimeFormat = dateTimeFormat
                    )
                )
            }
        }
    }









    //==============================================================================================
    //set uiState value ============================================================================
    fun firstLaunchToFalse() {
        _appUiState.update {
            it.copy(firstLaunch = false)
        }
    }






    //==============================================================================================
    //at sign in screen ============================================================================
    fun initAppUiState(

    ){
        _appUiState.update {
            it.copy(
                appUserData = null,
                firstLaunch = true
            )
        }
    }









    //==============================================================================================
    //at app start splash screen ===================================================================
    fun initUserAndUpdateStartDestination (

    ){
//        val startTime = android.os.SystemClock.elapsedRealtime()
        if (appUiState.value.screenDestination.startScreenDestination != null)
            return

        Log.d("MainActivity1", "[1] intiUserAndUpdateStartDestination start")

        initSignedInUser(
            onDone = { userDataIsNull ->
                updateCurrentScreenDestination(userDataIsNull)
//                val endTime = android.os.SystemClock.elapsedRealtime()
                Log.d("MainActivity1", "                              [1] intiUserAndUpdateStartDestination done")
            }
        )
    }

    private fun initSignedInUser(
        onDone: (userDataIsNull: Boolean) -> Unit
    ){
        Log.d("MainActivity1", "[2] initSignedInUser start")

        viewModelScope.launch {
//            val time = measureNanoTime {
            var userData = userRepository.getSignedInUser()

            //get user is using somewhere pro
            if (userData != null) {
                val isUsingSomewherePro = getIsUsingSomewherePro()
                userData = userData.copy(isUsingSomewherePro = isUsingSomewherePro)
            }

            _appUiState.update {
                it.copy(appUserData = userData)
            }

            Log.d("MainActivity1", "                              [2] initSignedInUser - user: ${userData?.userId}")

            onDone(userData == null || userData.userId == "")
//            }
//            Log.d("MainActivity1", "[2] ${time*0.000000001} - initSignedInUser")
        }
    }

    private suspend fun getIsUsingSomewherePro(

    ): Boolean {
        val purchasedResult = CompletableDeferred<Boolean>()

        val purchasesUpdatedListener =
            PurchasesUpdatedListener { _, _ -> }

        subscriptionRepository.billingClientStartConnection(
            purchasesUpdatedListener = purchasesUpdatedListener,
            onPurchased = { purchased ->
                purchasedResult.complete(purchased ?: false)
            },
            onFormattedPrice = {_,_ -> },
            onError = {
                purchasedResult.complete(false)
            }
        )

        return purchasedResult.await()
    }










    //==============================================================================================
    //update screen destination ====================================================================
    fun updateCurrentScreenDestination(
        userDataIsNull: Boolean
    ){
        val startScreenDestination =
            if (userDataIsNull) ScreenDestination.SIGN_IN
            else ScreenDestination.TRIPS

        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    startScreenDestination = startScreenDestination
                )
            )
        }
        Log.d("MainActivity1", "[3] update screen destination: $startScreenDestination")
    }

    fun updateMoreDetailCurrentScreenDestination(
        screenDestination: ScreenDestination
    ){
        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    moreDetailStartScreenDestination = screenDestination
                )
            )
        }
    }

    fun updateCurrentTopLevelDestination(topLevelDestination: TopLevelDestination){
        _appUiState.update {
            it.copy(
                screenDestination = it.screenDestination.copy(
                    currentTopLevelDestination = topLevelDestination
                )
            )
        }
    }

    fun updateCurrentScreenDestination(
        screenDestination: ScreenDestination
    ) {

        when (screenDestination) {
            ScreenDestination.TRIPS -> {
                _appUiState.update {
                    it.copy(
                        screenDestination = it.screenDestination.copy(
                            currentTopLevelDestination = TopLevelDestination.TRIPS,
                            currentScreenDestination = screenDestination
                        )
                    )
                }
            }
            ScreenDestination.PROFILE -> {
                _appUiState.update {
                    it.copy(
                        screenDestination = it.screenDestination.copy(
                            currentTopLevelDestination = TopLevelDestination.PROFILE,
                            currentScreenDestination = screenDestination
                        )
                    )
                }
            }
            ScreenDestination.MORE -> {
                _appUiState.update {
                    it.copy(
                        screenDestination = it.screenDestination.copy(
                            currentTopLevelDestination = TopLevelDestination.MORE,
                            currentScreenDestination = screenDestination
                        )
                    )
                }
            }
            else -> {
                _appUiState.update {
                    it.copy(
                        screenDestination = it.screenDestination.copy(
                            currentScreenDestination = screenDestination
                        )
                    )
                }
            }
        }
    }




    //==============================================================================================
    //update user data =============================================================================
    fun updateUserData(
        userData: UserData?
    ) {
        _appUiState.update {
            it.copy(
                appUserData = userData
            )
        }
    }
}