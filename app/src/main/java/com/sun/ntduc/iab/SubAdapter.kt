package com.sun.ntduc.iab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.billingclient.api.SkuDetails
import kotlinx.android.synthetic.main.item_sub.view.*

class SubAdapter(private val onClickItemListener: OnClickItemListener) :
    ListAdapter<SkuDetails, SubAdapter.ViewHolder>(SkuDetailsDiffCallback()) {

    interface OnClickItemListener {
        fun onClickItem(item: SkuDetails)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(inflater.inflate(R.layout.item_sub, viewGroup, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClickItemListener)

    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(sku: SkuDetails, onClickItemListener: OnClickItemListener) {
            itemView.tv_sub.text = sku.title + sku.price
            itemView.setOnClickListener {
                onClickItemListener.onClickItem(sku)
            }

        }
    }

    class SkuDetailsDiffCallback : DiffUtil.ItemCallback<SkuDetails>() {
        override fun areItemsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean {
            return oldItem == newItem
        }
    }


}
