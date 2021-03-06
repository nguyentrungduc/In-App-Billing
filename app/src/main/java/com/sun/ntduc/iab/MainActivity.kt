package com.sun.ntduc.iab

import android.content.Intent
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


class MainActivity : AppCompatActivity(), PurchasesUpdatedListener, BillingClientStateListener,
    ProductAdapter.OnClickItemListener, PurchaseHistoryResponseListener, ConsumeResponseListener,
    SkuDetailsResponseListener {

    private val listHistory = arrayListOf<String>()
    override fun onSkuDetailsResponse(billingResult: BillingResult?, skuDetailsList: MutableList<SkuDetails>?) {
        Log.d(TAG, "onSkuDetailResponse" + skuDetailsList.toString())
    }

    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
        Log.d(TAG, "onConsumeRespone" + purchaseToken.toString())
    }

    private lateinit var mainViewModel: MainViewModel

    override fun onClickItem(item: Sku) {
        Log.d(TAG, "onClick" + item)
        if (item.canPurchase) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(SkuDetails(item.originalJson))
                .build()

            playStoreBillingClient.launchBillingFlow(this, flowParams)
        } else Toast.makeText(this, "mua roi ko can mua nua", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "Main"

        private val skuList = listOf("id_2"
            ,"com.sun.selfstudy.product1",
            "id_4",
            "com.sun.ntduc.iab.drink",
            "com.sun.ntduc.iab.food")

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
                    comsumableSku(it)
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
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(it.purchaseToken)
                .build()

        playStoreBillingClient.consumeAsync(consumeParams, this)



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
        btn_restore.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        btn_purchase.setOnClickListener {
            val intent = Intent(this, PurchaseQuatityActivity::class.java)
            startActivity(intent)
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

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "onBillingSetupFinished successfully")
                querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuListSub)
                       querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList)
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
        skuList1: List<String>
    ) {
        val params = SkuDetailsParams.newBuilder().setSkusList(skuList1).setType(skuType).build()
        Log.d(TAG, "querySkuDetailsAsync for $skuType")
        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuList1 == skuList) {
                        if (skuDetailsList.orEmpty().isNotEmpty()) {
                            Log.d(TAG, skuDetailsList.toString())
                        }
                        skuDetailsList.forEach {
                            insert(it)
                            if (listHistory.contains(it.sku)) {
                                insertOrUpdate(it.sku, false)
                            }
                        }
                    }
                    else Log.d(TAG, "subbb" + skuDetailsList)
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
        Log.d(TAG, "history"+purchaseHistoryRecordList.toString())
        purchaseHistoryRecordList?.forEach {
            listHistory.add(it.sku)
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()

            playStoreBillingClient.consumeAsync(consumeParams, this)
        }

        querySkuDetailsAsync(BillingClient.SkuType.INAPP, skuList)

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