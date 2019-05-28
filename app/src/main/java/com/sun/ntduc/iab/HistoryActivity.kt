package com.sun.ntduc.iab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_empty.*

class HistoryActivity : AppCompatActivity(), BillingClientStateListener, PurchasesUpdatedListener, PurchaseHistoryResponseListener {

    private lateinit var historyAdapter: HistoryAdapter
    override fun onPurchaseHistoryResponse(
        billingResult: BillingResult?,
        purchaseHistoryRecordList: MutableList<PurchaseHistoryRecord>?
    ) {
        Log.d("history", purchaseHistoryRecordList.toString())
        historyAdapter.submitList(purchaseHistoryRecordList)
    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                getHistory()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

            }
            else -> {
            }
        }
    }

    private fun getHistory() {
        playStoreBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)

    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
    }

    private lateinit var playStoreBillingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        instantiateAndConnectToPlayBillingService()
        historyAdapter = HistoryAdapter()
        rcv_history.adapter = historyAdapter

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

}
