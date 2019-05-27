package com.sun.ntduc.iab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.SkuDetails
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

        private const val TAG = "MainViewModel"

    }

    private val skus = MutableLiveData<List<Sku>>()
    init {
        SkuDatabase.getDataBase(application).skuDao().getInappSkuDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                skus.postValue(it)

            }

    }

    fun getSkus() = skus

}