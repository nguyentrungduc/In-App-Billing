package com.sun.ntduc.iab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_purchase_quatity.*

class PurchaseQuatityActivity : AppCompatActivity(), BillingClientStateListener, PurchasesUpdatedListener, ConsumeResponseListener, SubAdapter.OnClickItemListener {
    override fun onClickItem(item: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(SkuDetails(item.originalJson))
            .build()

        playStoreBillingClient.launchBillingFlow(this, flowParams)
    }

    private lateinit var subAdapter: SubAdapter

    companion object {
        private const val TAG = "Purchase"

        private val skuListSub = listOf("id_3"
            ,"com.sun.ntduc.iab.yearly",
            "com.sun.ntduc.iab.monthly")

        private val COMSUMABLE_LIST = listOf<String>("food")

        private val NON_COMSUMABLE = listOf("hero")

    }

    var value = 0
    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBillingServiceDisconnected() {
    }

    private lateinit var playStoreBillingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_quatity)
        instantiateAndConnectToPlayBillingService()
        subAdapter = SubAdapter(this)
        rcv_sub.adapter = subAdapter
        tv_add.setOnClickListener {
            value++
            tv_value.text = value.toString()
        }

    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        when (billingResult?.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Log.d(TAG, "okok")
                val params = SkuDetailsParams.newBuilder().setSkusList(skuListSub).setType(BillingClient.SkuType.SUBS).build()
                playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            Log.d(TAG, "list" + skuDetailsList)
                            subAdapter.submitList(skuDetailsList)
                        }
                        else -> {

                        }
                    }
                }
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
