package com.sun.ntduc.iab

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.RewardResponseListener
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener

class EmptyActivity : AppCompatActivity(), RewardResponseListener, RewardedVideoAdListener{

    private val AD_UNIT_ID = "/6499/example/rewarded-video"

    private var coinCount: Int = 0
    private var coinCountText: TextView? = null
    private var rewardedVideoAd: RewardedVideoAd? = null
    private var showVideoButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        rewardedVideoAd?.rewardedVideoAdListener = this
        loadRewardedVideoAd()


        showVideoButton = findViewById(R.id.btn)
        showVideoButton?.setOnClickListener {

            showRewardedVideo()
        }

    }

    public override fun onPause() {
        super.onPause()
        rewardedVideoAd!!.pause(this)
    }

    public override fun onResume() {
        super.onResume()

        rewardedVideoAd!!.resume(this)
    }


    private fun loadRewardedVideoAd() {
        if (!rewardedVideoAd?.isLoaded!!) {
            rewardedVideoAd?.loadAd(AD_UNIT_ID, PublisherAdRequest.Builder().build())
        }
    }

    private fun addCoins(coins: Int) {
        coinCount += coins
        coinCountText!!.text = "Coins: $coinCount"
    }


    private fun showRewardedVideo() {
        showVideoButton?.visibility = View.INVISIBLE
        if (rewardedVideoAd?.isLoaded!!) {
            rewardedVideoAd?.show()
        }
    }

    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
        // Preload the next video ad.
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewarded(reward: RewardItem) {
        Toast.makeText(
            this,
            String.format(
                " onRewarded! currency: %s amount: %d", reward.type,
                reward.amount
            ),
            Toast.LENGTH_SHORT
        ).show()
        addCoins(reward.amount)
    }

    override fun onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardResponse(responseCode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
