package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import com.newpaper.somewhere.core.data.repository.more.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_VIEWMODEL_TAG = "Subscription-ViewModel"

data class SubscriptionUiState(
    val initialized: Boolean = false,
    val isUsingSomewherePro: Boolean = false,
    val formattedPrice: String = "",

    val showErrorPage: Boolean = false
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
                                    if (purchasedResult != null)
                                        setIsUsingSomewherePro(purchasedResult)
                                },
                                onError = { }
                            )
                        }
                    }
                    else {
                        //purchases is empty or null
                        Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - purchases is empty or null")
                    }
                }
                else -> {
                    //error
                    Log.d(SUBSCRIPTION_VIEWMODEL_TAG, "purchasesUpdatedListener - ${billingResult.responseCode}")
                }
            }
        }






    fun setInitialized(initialized: Boolean){
        _subscriptionUiState.update {
            it.copy(initialized = initialized)
        }
    }

    fun setFormattedPrice(formattedPrice: String){
        _subscriptionUiState.update {
            it.copy(formattedPrice = formattedPrice)
        }
    }

    fun setIsUsingSomewherePro(isUsingSomewherePro: Boolean){
        _subscriptionUiState.update {
            it.copy(isUsingSomewherePro = isUsingSomewherePro)
        }
    }

    fun setShowErrorScreen(showErrorPage: Boolean){
        _subscriptionUiState.update {
            it.copy(showErrorPage = showErrorPage)
        }
    }

    fun billingClientStartConnection(

    ) {
        if (!_subscriptionUiState.value.initialized) {
            viewModelScope.launch {
                setInitialized(true)
                subscriptionRepository.billingClientStartConnection(
                    purchasesUpdatedListener = purchasesUpdatedListener,
                    onPurchased = { purchased ->
                        if (purchased != null)
                            setIsUsingSomewherePro(purchased)
                    },
                    onFormattedPrice = { formattedPrice ->
                        if (formattedPrice != null)
                            setFormattedPrice(formattedPrice)
                    },
                    onError = {
                        setShowErrorScreen(true)
                    }
                )
            }
        }
    }

    fun launchBillingFlow(
        activity: Activity
    ){
        subscriptionRepository.launchBillingFlow(
            activity = activity,
            onError = {
                //error snackbar
            }
        )
    }
}