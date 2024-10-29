package com.newpaper.somewhere.feature.more.subscription

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newpaper.somewhere.core.data.repository.more.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SubscriptionUiState(
    val isUsingSomewherePro: Boolean = false,
    val initialized: Boolean = false
)

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {
    private val _subscriptionUiState = MutableStateFlow(SubscriptionUiState())
    val subscriptionUiState = _subscriptionUiState.asStateFlow()

    fun setIsUsingSomewherePro(isUsingSomewherePro: Boolean){
        _subscriptionUiState.update {
            it.copy(isUsingSomewherePro = isUsingSomewherePro)
        }
    }

    fun setInitialized(initialized: Boolean){
        _subscriptionUiState.update {
            it.copy(initialized = initialized)
        }
    }

    fun billingClientStartConnection(

    ) {
        if (!_subscriptionUiState.value.initialized) {
            viewModelScope.launch {
                setInitialized(true)
                val purchased = subscriptionRepository.billingClientStartConnection()
                Log.d("subscription", "purchased: $purchased")
                setIsUsingSomewherePro(purchased)
            }
        }
    }

    fun launchBillingFlow(
        activity: Activity
    ){
        subscriptionRepository.launchBillingFlow(activity)
    }
}