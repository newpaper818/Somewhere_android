package com.newpaper.somewhere.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.PurchasesUpdatedListener
import com.newpaper.somewhere.core.data.repository.PreferencesRepository
import com.newpaper.somewhere.core.data.repository.image.CommonImageRepository
import com.newpaper.somewhere.core.data.repository.more.SubscriptionRepository
import com.newpaper.somewhere.core.data.repository.signIn.UserRepository
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.data.GUEST_USERDATA
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
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.measureTime

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

sealed class RemoteFetchResult {
    data class Success(val userData: UserData) : RemoteFetchResult()
    object SessionExpired : RemoteFetchResult()
    object Error : RemoteFetchResult()
}



@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val commonImageRepository: CommonImageRepository
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
//            val time = measureTime {
            // 1. Get cached user and last updated time
            val (cachedUserData, lastUpdatedTime) = userRepository.getCachedUser()
            val now = Instant.now()

            val isCacheValid = if (cachedUserData != null && lastUpdatedTime != null) {
                try {
                    val lastUpdatedDateTime = Instant.parse(lastUpdatedTime)
                    val diffInDays = Duration.between(lastUpdatedDateTime, now).toDays()
                    diffInDays <= 15 // about 15 days (15 days 23h 59s -> return 15)
                } catch (_: Exception) {
                    false
                }
            } else {
                false
            }

            if (isCacheValid) {
                Log.d("MainActivity1", "                              [2] initSignedInUser - using valid cache")
                // Use cache and dismiss splash immediately
                _appUiState.update { it.copy(appUserData = cachedUserData ?: GUEST_USERDATA) }
                onDone(false)

                // Then update in background
                launch(Dispatchers.IO) {
                    refreshUserData()
                }
            } else {
                Log.d("MainActivity1", "                              [2] initSignedInUser - cache invalid or missing, fetching remote")
                // Perform full remote fetch
                when (val result = performFullRemoteFetch()) {
                    is RemoteFetchResult.Success -> {
                        _appUiState.update { it.copy(appUserData = result.userData) }
                        Log.d("MainActivity1", "                              [2] initSignedInUser - user: ${result.userData.userId}")
                        onDone(false)
                    }
                    is RemoteFetchResult.SessionExpired -> {
                        _appUiState.update { it.copy(appUserData = GUEST_USERDATA) }
                        Log.d("MainActivity1", "                              [2] initSignedInUser - session expired")
                        onDone(true)
                    }
                    is RemoteFetchResult.Error -> {
                        // In case of error during initial fetch without cache, we might need to stay on sign-in or retry
                        _appUiState.update { it.copy(appUserData = GUEST_USERDATA) }
                        Log.d("MainActivity1", "                              [2] initSignedInUser - fetch error")
                        onDone(true)
                    }
                }
            }
//            }
//            Log.d("MainActivity1", "[2] $time - initSignedInUser")
        }
    }

    private suspend fun performFullRemoteFetch(): RemoteFetchResult {
        return try {
            val userData = userRepository.getSignedInUser()

            if (userData != null) {
                val isUsingSomewherePro = getIsUsingSomewherePro()
                val updatedUserData = userData.copy(isUsingSomewherePro = isUsingSomewherePro)

                // Save to cache
                userRepository.saveUserToCache(updatedUserData, Instant.now().toString())
                RemoteFetchResult.Success(updatedUserData)
            }
            else {
                RemoteFetchResult.SessionExpired
            }
        }
        catch (e: Exception) {
            Log.e("MainActivity1", "performFullRemoteFetch error", e)
            RemoteFetchResult.Error
        }
    }

    private suspend fun refreshUserData() {
        when (val result = performFullRemoteFetch()) {
            is RemoteFetchResult.Success -> {
                Log.d("MainActivity1", "refreshUserData - user updated from remote / appUiState update")
                _appUiState.update { it.copy(appUserData = result.userData) }
            }
            is RemoteFetchResult.SessionExpired -> {
                Log.d("MainActivity1", "refreshUserData - user not found on remote (session expired), signing out")
                // Clear cache and logout
                userRepository.clearUserCache()
                _appUiState.update {
                    it.copy(
                        appUserData = GUEST_USERDATA,
                        screenDestination = it.screenDestination.copy(
                            startScreenDestination = ScreenDestination.SIGN_IN
                        )
                    )
                }
            }
            is RemoteFetchResult.Error -> {
                Log.d("MainActivity1", "refreshUserData - network error or firestore fetch failed, keeping cache")
                // Keep existing cached data
            }
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
        val startScreenDestination = ScreenDestination.TRIPS

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

        // Update cache
        viewModelScope.launch(Dispatchers.IO) {
            if (userData != null) {
                userRepository.saveUserToCache(userData, Instant.now().toString())
            } else {
                userRepository.clearUserCache()
            }
        }
    }


    //
    fun deleteAllLocalImages(){
        commonImageRepository.deleteAllImagesFromInternalStorage()
    }
}