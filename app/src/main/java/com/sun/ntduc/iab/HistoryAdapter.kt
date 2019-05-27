package com.sun.ntduc.iab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.billingclient.api.PurchaseHistoryRecord
import kotlinx.android.synthetic.main.item_purchase.view.*

class HistoryAdapter() :
    ListAdapter<PurchaseHistoryRecord, HistoryAdapter.ViewHolder>(PurchaseHistoryRecordDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(inflater.inflate(R.layout.item_purchase, viewGroup, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(purchase: PurchaseHistoryRecord) {
            itemView.tv_purchase.text = purchase.sku + " "+ purchase.purchaseTime

        }
    }

    class PurchaseHistoryRecordDiffCallback : DiffUtil.ItemCallback<PurchaseHistoryRecord>() {
        override fun areItemsTheSame(oldItem: PurchaseHistoryRecord, newItem: PurchaseHistoryRecord): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PurchaseHistoryRecord, newItem: PurchaseHistoryRecord): Boolean {
            return oldItem == newItem
        }
    }


}
