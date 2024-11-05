package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.newpaper.somewhere.core.data.repository.more.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_VIEWMODEL_TAG = "Subscription-ViewModel"

data class SubscriptionUiState(
    val billingClientInitialized: Boolean = false,
    val isUsingSomewherePro: Boolean = false,
    val formattedPrice: String = "",
    val oneFreeWeekEnabled: Boolean = false,

    val buttonEnabled: Boolean = true,

    val showErrorPage: Boolean = false,
    val showErrorSnackbar: Boolean = false
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {
    private val _subscriptionUiState = MutableStateFlow(SubscriptionUiState())
    val subscriptionUiState = _subscriptionUiState.asStateFlow()


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            when (billingResult.responseCode) {

                BillingClient.BillingResponseCode.OK -> {
                    if (purchases?.isNotEmpty() == true) {
                        for (purchase in purchases) {
                            subscriptionRepository.acknowledgePurchase(
                                purchase = purchase,
                                purchasedResult = { purchasedResult ->
                                    Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - after purchased result: $purchasedResult")
                                    Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - purchase token: ${purchase.purchaseToken}")
                                    if (purchasedResult != null)
                                        setIsUsingSomewherePro(purchasedResult)
                                        //update app viewmodel app user data
                                },
                                onError = {
                                    viewModelScope.launch {
                                        setShowErrorSnackbar(true)
                                        delay(4500)
                                        setShowErrorSnackbar(false)
                                    }
                                }
                            )
                        }
                    }
                    else {
                        //purchases is empty or null
                        Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - purchases is empty or null")
                        viewModelScope.launch {
                            setShowErrorSnackbar(true)
                            delay(4500)
                            setShowErrorSnackbar(false)
                        }
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - ${billingResult.responseCode} user canceled")
                }
                else -> {
                    //error
                    Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - ${billingResult.responseCode}")
//                    viewModelScope.launch {
//                        setShowErrorSnackbar(true)
//                        delay(4500)
//                        setShowErrorSnackbar(false)
//                    }
                }
            }
        }






    fun setBillingClientInitialized(billingClientInitialized: Boolean){
        _subscriptionUiState.update {
            it.copy(billingClientInitialized = billingClientInitialized)
        }
    }

    fun setFormattedPrice(formattedPrice: String){
        _subscriptionUiState.update {
            it.copy(formattedPrice = formattedPrice)
        }
    }

    fun setOneFreeWeekEnabled(oneFreeWeekEnabled: Boolean){
        _subscriptionUiState.update {
            it.copy(oneFreeWeekEnabled = oneFreeWeekEnabled)
        }
    }

    fun setIsUsingSomewherePro(isUsingSomewherePro: Boolean){
        _subscriptionUiState.update {
            it.copy(isUsingSomewherePro = isUsingSomewherePro)
        }
    }

    fun setButtonEnabled(buttonEnabled: Boolean){
        _subscriptionUiState.update {
            it.copy(buttonEnabled = buttonEnabled)
        }
    }

    fun setShowErrorScreen(showErrorPage: Boolean){
        _subscriptionUiState.update {
            it.copy(showErrorPage = showErrorPage)
        }
    }

    fun setShowErrorSnackbar(showErrorSnackbar: Boolean){
        _subscriptionUiState.update {
            it.copy(showErrorSnackbar = showErrorSnackbar)
        }
    }

    fun billingClientStartConnection(
        onClickRestorePurchases: Boolean = false,
        showSnackBarNotPurchased: () -> Unit = {},
        showSnackError: () -> Unit = {}
    ) {
        if (!_subscriptionUiState.value.billingClientInitialized || onClickRestorePurchases) {
            if (onClickRestorePurchases)
                setButtonEnabled(false)

            viewModelScope.launch {
                setBillingClientInitialized(true)
                subscriptionRepository.billingClientStartConnection(
                    purchasesUpdatedListener = purchasesUpdatedListener,
                    onPurchased = { purchased ->
                        if (onClickRestorePurchases && purchased != true)
                            showSnackBarNotPurchased()

                        if (purchased != null)
                            setIsUsingSomewherePro(purchased)
                    },
                    onFormattedPrice = { formattedPrice, oneFreeWeekEnable ->
                        setFormattedPrice(formattedPrice)
                        setOneFreeWeekEnabled(oneFreeWeekEnable)
                    },
                    onError = {
                        setShowErrorScreen(true)
                        showSnackError()
                    }
                )
                delay(2000)
                setButtonEnabled(true)
            }
        }
    }

    fun launchBillingFlow(
        activity: Activity,
        showSnackbarError: () -> Unit
    ){
        viewModelScope.launch {
            setButtonEnabled(false)
            subscriptionRepository.launchBillingFlow(
                activity = activity,
                onError = showSnackbarError
            )
            delay(2000)
            setButtonEnabled(true)
        }
    }
}