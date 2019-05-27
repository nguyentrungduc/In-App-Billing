package com.sun.ntduc.iab

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_product.view.*

class ProductAdapter(val listener: OnClickItemListener) :
    ListAdapter<Sku, ProductAdapter.ViewHolder>(SkuDiffCallback()) {

    interface OnClickItemListener {
        fun onClickItem(item: Sku)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ViewHolder(inflater.inflate(R.layout.item_product, viewGroup, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)

    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(sku: Sku, listener: OnClickItemListener) {
            val tv = itemView.findViewById<TextView>(R.id.tv_sku)
            if (!sku.canPurchase) {
                itemView.img.visibility = View.VISIBLE
            }
            itemView.tv_price.text = sku.price
            tv.text = sku.title
                tv.setOnClickListener {
                        listener.onClickItem(sku)
                }
        }
    }

    class SkuDiffCallback : DiffUtil.ItemCallback<Sku>() {
        override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
            return oldItem == newItem
        }
    }


}
