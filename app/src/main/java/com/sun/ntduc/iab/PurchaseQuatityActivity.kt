package com.sun.ntduc.iab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.*

class PurchaseQuatityActivity : AppCompatActivity(), BillingClientStateListener, PurchasesUpdatedListener, ConsumeResponseListener {
    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBillingServiceDisconnected() {
    }

    private lateinit var playStoreBillingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        instantiateAndConnectToPlayBillingService()

    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

            }
            else -> {
            }
        }
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

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach {
                   comsumableSku(it)
                    SkuDatabase.getDataBase(this).purchaseDao().insert(CachedPurchase(it.toString()))
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToPlayBillingService()
            }
            else -> {
            }
        }
    }

    private fun comsumableSku(it: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(it.purchaseToken)
                .build()

        playStoreBillingClient.consumeAsync(consumeParams, this)

    }


}
