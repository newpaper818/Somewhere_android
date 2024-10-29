package com.newpaper.somewhere.core.data.repository.more

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject
import javax.inject.Inject

private const val SUBSCRIPTION_TAG = "subscription"

private const val SOMEWHERE_PRO_SUBSCRIPTION_ID = "somewhere_pro"


class SubscriptionRepository @Inject constructor(
    @ApplicationContext private val context: Context
){
    private lateinit var billingClient: BillingClient
    private var productDetailList = mutableListOf<ProductDetails>()

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (purchases?.isNotEmpty() == true) {
                        for (purchase in purchases) {
                            acknowledgePurchase(
                                purchase = purchase,
                                purchasedResult = { purchased ->
                                    //to viewmodel
                                }
                            )
                        }
                    }
                    else {
                        //purchases is empty or null
                        Log.d(SUBSCRIPTION_TAG, "purchasesUpdatedListener - purchases is empty or null")
                    }
                }
                else -> {
                    //error
                    Log.d(SUBSCRIPTION_TAG, "purchasesUpdatedListener - ${billingResult.responseCode}")
                }
            }
        }

    suspend fun billingClientStartConnection(

    ): Boolean {
        Log.d(SUBSCRIPTION_TAG, "initBillingClient")
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        return startConnection()
    }

    private suspend fun startConnection(

    ): Boolean {
        val purchasedResult = CompletableDeferred<Boolean>()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(SUBSCRIPTION_TAG, "startConnection")
                    queryProducts(
                        onDone = {
                            queryPurchases(
                                purchasedResult = { purchasedResult.complete(it) }
                            )
                        }
                    )
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(SUBSCRIPTION_TAG, "startConnection - billing service disconnected")
                billingClient.startConnection(this)
            }
        })

        return purchasedResult.await()
    }

    private fun queryProducts(
        onDone: () -> Unit
    ){
        Log.d(SUBSCRIPTION_TAG, "queryProducts")

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(SOMEWHERE_PRO_SUBSCRIPTION_ID)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                )
                .build()


        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult, productDetailsList ->
            // check billingResult
            // process returned productDetailsList
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    Log.d(SUBSCRIPTION_TAG, "queryProducts - add to productDetailList: $productDetailsList")
                    productDetailList.addAll(productDetailsList)
                }
                else {
                    //no such product
                    Log.d(SUBSCRIPTION_TAG, "queryProducts - no such product")
                }
            }
            else {
                //display billing error
                Log.d(SUBSCRIPTION_TAG, "queryProducts - ${billingResult.responseCode}")
            }
        }

        onDone()
    }

    private fun queryPurchases(
        purchasedResult: (purchased: Boolean) -> Unit
    ){
        Log.d(SUBSCRIPTION_TAG, "queryPurchases")

        if (!billingClient.isReady){
            //billing client not ready
            Log.d(SUBSCRIPTION_TAG, "queryPurchases - billing client not ready")

            return
        }

        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, productDetailList ->
            when (val responseCode = billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Log.d(SUBSCRIPTION_TAG, "queryPurchases - productDetailList size: ${productDetailList.size}")

                    if (productDetailList.isNotEmpty()){
                        for (purchase in productDetailList){
                            acknowledgePurchase(
                                purchase = purchase,
                                purchasedResult = purchasedResult
                            )
                        }
                    }
                    else {
                        //no purchase found
                        Log.d(SUBSCRIPTION_TAG, "queryPurchases - no such product")
                    }
                }
                else -> {
                    //display billing error
                    Log.d(SUBSCRIPTION_TAG, "queryPurchases - $responseCode")
                }
            }
        }
    }

    private fun acknowledgePurchase(
        purchase: Purchase,
        purchasedResult: (purchased: Boolean) -> Unit
    ){
        Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase")

        val json = purchase.originalJson
        try {
            val jsonObject = JSONObject(json)
            val productId = jsonObject.getString("productId")

            if (
                productId == SOMEWHERE_PRO_SUBSCRIPTION_ID
                && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                && !purchase.isAcknowledged
            ){
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)

                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()){ billingResult ->
                    when (val responseCode = billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            //purchase acknowledged
                            //TODO save purchase to firestore
                            Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - save to firestore")
                            purchasedResult(true)
                        }
                        else -> {
                            //display billing error
                            Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - $responseCode")
                            purchasedResult(false)
                        }
                    }
                }
            }
            else {
                Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - productId: $productId")
                Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - purchased: ${purchase.purchaseState == Purchase.PurchaseState.PURCHASED}")
                Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - isAcknowledged: ${purchase.isAcknowledged}")
                purchasedResult(purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
            }
        }
        catch (e: Exception){
            //error
            Log.d(SUBSCRIPTION_TAG, "acknowledgePurchase - $e")
            purchasedResult(false) //TODO: is this right???
        }
    }

    fun launchBillingFlow(
        activity: Activity,
    ){
        Log.d(SUBSCRIPTION_TAG, "launchBillingFlow")

        if (productDetailList.isEmpty()) {
            Log.d(SUBSCRIPTION_TAG, "launchBillingFlow - productDetailList empty")
            return
        }

        val offerToken = productDetailList[0].subscriptionOfferDetails?.get(0)?.offerToken

        if (offerToken == null){
            Log.d(SUBSCRIPTION_TAG, "launchBillingFlow - offerToken is null")
            return
        }

        val productDetailsParamList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetailList[0])
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamList)
            .build()

        if (!billingClient.isReady){
            //try again later
            return
        }

        //launching billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d(SUBSCRIPTION_TAG, "launchBillingFlow - result:$billingResult")


//        when (val responseCode = billingResult.responseCode) {
//            BillingClient.BillingResponseCode.OK -> {
//                //launch success
//            }
//
//            else -> {
//                //display billing error
//            }
//        }
    }
}