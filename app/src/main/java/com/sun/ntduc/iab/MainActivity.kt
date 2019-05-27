package com.sun.ntduc.iab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.billingclient.api.*
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), PurchasesUpdatedListener, BillingClientStateListener,
    ProductAdapter.OnClickItemListener, PurchaseHistoryResponseListener, ConsumeResponseListener,
    SkuDetailsResponseListener {
    override fun onSkuDetailsResponse(billingResult: BillingResult?, skuDetailsList: MutableList<SkuDetails>?) {
    }

    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
        Log.d(TAG, "onConsumeRespone" + billingResult.toString() + "   " + purchaseToken.toString())
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onClickItem(item: Sku) {
        if (item.canPurchase) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(SkuDetails(item.originalJson))
                .build()

            playStoreBillingClient.launchBillingFlow(this, flowParams)
        } else Toast.makeText(this, "mua roi ko can mua nua", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "Main"

        private val skuList = listOf("com.sun.ntduc.iab.food", "com.sun.ntduc.iab.drink")

        private val skuListSub = listOf("id_3")

        private val COMSUMABLE_LIST = listOf<String>("food")

        private val NON_COMSUMABLE = listOf("hero")

    }

    private lateinit var playStoreBillingClient: BillingClient

    private lateinit var productAdapter1: ProductAdapter

    private lateinit var productAdapter2: ProductAdapter

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "ppp" + purchases?.get(0)?.sku.toString())
                purchases?.forEach {
                    if (it.sku.toString().contains("blood")) {
                        comsumableSku(it)

                    } else  if(it.sku.toString().contains("hero")){
                        insertOrUpdate(it.sku, false)
                    }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
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

    private fun comsumableSku(it: Purchase) {


    }

    override fun onBillingServiceDisconnected() {
        connectToPlayBillingService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setUpAdapter()
        instantiateAndConnectToPlayBillingService()

        mainViewModel.getSkus().observe(this, Observer {
            productAdapter1.submitList(it)
        })


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
                querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList)
//                querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuListSub)
                queryPurchasesAsync()
                getHistory()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                Log.d(TAG, billingResult.debugMessage)
            }
            else -> {
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
                Log.d(TAG, "list Purchase" + this.toString())
                purchasesResult.addAll(this)
            }
            Log.d(TAG, "queryPurchasesAsync SUBS results: ${result?.purchasesList?.size}")
        }
    }

    private fun setUpAdapter() {
        productAdapter1 = ProductAdapter(this)
        productAdapter2 = ProductAdapter(this)

        rcv.adapter = productAdapter1
        //rcv2.adapter = productAdapter2

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
                        purchaseToken.apply { disburseConsumableEntitlements(it) }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun disburseConsumableEntitlements(it: Purchase) {
       // SkuDatabase.getDataBase(this).purchaseDao().delete(it)

    }

    private fun acknowledgeNonConsumablePurchasesAsync(nonConsumables: List<Purchase>) {
        nonConsumables.forEach { purchase ->
            val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(
                purchase
                    .purchaseToken
            ).build()
            playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {

                    }
                    else -> Log.d(
                        TAG,
                        "acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}"
                    )
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
            else -> Log.w(
                TAG,
                "isSubscriptionSupported() error: ${billingResult.debugMessage}"
            )
        }
        return succeeded
    }

    private fun querySkuDetailsAsync(
        @BillingClient.SkuType skuType: String,
        skuList: List<String>
    ) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(skuType).build()
        Log.d(TAG, "querySkuDetailsAsync for $skuType")
        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuDetailsList.orEmpty().isNotEmpty()) {
                        Log.d(TAG, skuDetailsList.toString())
                    }
                    skuDetailsList.forEach {
                        insert(it)
                    }
                }
                else -> {
                    Log.e(TAG, billingResult.debugMessage)
                }
            }
        }
    }

    private fun getHistory() {
        playStoreBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, this)
    }


    override fun onPurchaseHistoryResponse(
        billingResult: BillingResult?,
        purchaseHistoryRecordList: MutableList<PurchaseHistoryRecord>?
    ) {
        Log.d(TAG, "history" + purchaseHistoryRecordList?.size)

    }

    fun endDataSourceConnections() {
        playStoreBillingClient.endConnection()
        Log.d(TAG, "endDataSourceConnections")
    }

    fun insert(sku: SkuDetails) {
        SkuDatabase.getDataBase(this).skuDao().insertOrUpdate(sku)
    }

    fun insertOrUpdate(sku: String, canPurchase: Boolean) {
        SkuDatabase.getDataBase(this).skuDao().insertOrUpdate(sku, canPurchase)
    }

    override fun onDestroy() {
        endDataSourceConnections()
        super.onDestroy()
    }

}