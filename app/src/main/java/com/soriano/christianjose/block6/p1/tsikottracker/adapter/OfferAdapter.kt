package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemOfferBinding

class OfferAdapter : RecyclerView.Adapter<OfferViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Offer>(){
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var offers: List<Offer>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        return OfferViewHolder(ItemOfferBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return offers.size
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.binding.apply {
            val offer = offers[position]
            tvOfferName.text = offer.name
            tvPrice.text = offer.price.toString()
            tvType.text = offer.type
        }
    }
}

class OfferViewHolder(val binding : ItemOfferBinding) : RecyclerView.ViewHolder(binding.root)