package com.sun.ntduc.iab

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),PurchasesUpdatedListener, BillingClientStateListener  {

    private lateinit var playStoreBillingClient: BillingClient

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // will handle server verification, consumables, and updating the local cache
                purchases?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                Log.d(TAG, billingResult.debugMessage)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToPlayBillingService()
            }
            else -> {
                Log.i(TAG, billingResult?.debugMessage)
            }
        }
    }

    companion object {
        private const val TAG = "Main"
    }

    override fun onBillingServiceDisconnected() {
        connectToPlayBillingService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instantiateAndConnectToPlayBillingService()
    }

    private fun instantiateAndConnectToPlayBillingService() {
        playStoreBillingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this).build()
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService(): Boolean {
        if (!playStoreBillingClient.isReady) {
            playStoreBillingClient.startConnection(this)
            return true
        }
        return false
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "onBillingSetupFinished successfully")
//                querySkuDetailsAsync(BillingClient.SkuType.INAPP, GameSku.INAPP_SKUS)
//                querySkuDetailsAsync(BillingClient.SkuType.SUBS, GameSku.SUBS_SKUS)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                //Some apps may choose to make decisions based on this knowledge.
                Log.d(TAG, billingResult.debugMessage)
            }
            else -> {
                //do nothing. Someone else will connect it through retry policy.
                //May choose to send to server though
                Log.d(TAG, billingResult.debugMessage)
            }
        }
    }

    private fun queryPurchasesAsync() {
        Log.d(TAG, "queryPurchasesAsync called")
        val purchasesResult = HashSet<Purchase>()
        var result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Log.d(TAG, "queryPurchasesAsync INAPP results: ${result?.purchasesList?.size}")
        result?.purchasesList?.apply { purchasesResult.addAll(this) }
        if (isSubscriptionSupported()) {
            result = playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
            result?.purchasesList?.apply {
                Log.d(TAG,"list Purchase" +  this.toString())
                //setUpAdapter(this)
                purchasesResult.addAll(this)
            }
            Log.d(TAG, "queryPurchasesAsync SUBS results: ${result?.purchasesList?.size}")
        }
        processPurchases(purchasesResult)
    }

    private fun setUpAdapter(list: List<Purchase>) {
        val productAdapter = ProductAdapter()
        val rcv = findViewById<RecyclerView>(R.id.rcv)
        rcv.adapter = productAdapter

        //productAdapter.submitList(list)

    }

    private fun processPurchases(purchasesResult: Set<Purchase>) =
        CoroutineScope(Job() + Dispatchers.IO).launch {
            Log.d(TAG, "processPurchases called")
            val validPurchases = HashSet<Purchase>(purchasesResult.size)
            Log.d(TAG, "processPurchases newBatch content $purchasesResult")
            purchasesResult.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//                    if (isSignatureValid(purchase)) {
//                        validPurchases.add(purchase)
//                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Log.d(TAG, "Received a pending purchase of SKU: ${purchase.sku}")
                    // handle pending purchases, e.g. confirm with users about the pending
                    // purchases, prompt them to complete it, etc.
                }
            }
//            val (consumables, nonConsumables) = validPurchases.partition {
//                GameSku.CONSUMABLE_SKUS.contains(it.sku)
//            }

//            handleConsumablePurchasesAsync(consumables)
//            acknowledgeNonConsumablePurchasesAsync(nonConsumables)
        }

    private fun handleConsumablePurchasesAsync(consumables: List<Purchase>) {
        Log.d(TAG, "handleConsumablePurchasesAsync called")
        consumables.forEach {
            Log.d(TAG, "handleConsumablePurchasesAsync foreach it is $it")
            val params =
                ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
            playStoreBillingClient.consumeAsync(params) { billingResult, purchaseToken ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        Log.d(TAG, purchaseToken)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumables: List<Purchase>) {
        nonConsumables.forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase
                .purchaseToken).build()
            playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        Log.d(TAG, purchase.toString())
                    }
                    else -> Log.d(TAG, "acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}")
                }
            }

        }
    }

    private fun isSubscriptionSupported(): Boolean {
        val billingResult =
            playStoreBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        var succeeded = false
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> connectToPlayBillingService()
            BillingClient.BillingResponseCode.OK -> succeeded = true
            else -> Log.w(TAG,
                "isSubscriptionSupported() error: ${billingResult.debugMessage}")
        }
        return succeeded
    }

}