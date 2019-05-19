package com.sun.ntduc.iab

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter : ListAdapter<Sku, ProductAdapter.ViewHolder>(SkuDiffCallback()) {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(inflater.inflate(R.layout.item_product, viewGroup, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(sku: Sku) {
            val tv = itemView.findViewById<TextView>(R.id.tv_id)
            tv.text = sku.id

        }
    }

    class SkuDiffCallback : DiffUtil.ItemCallback<Sku>() {
        override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
            return oldItem == newItem
        }
    }


}