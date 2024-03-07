package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.soriano.christianjose.block6.p1.tsikottracker.OfferFragmentDirections
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.api.OfferApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemOfferBinding
import com.soriano.christianjose.block6.p1.tsikottracker.modal.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OfferAdapter(private val offerApi: OfferApi, private val fragmentManager: FragmentManager, private val companyId: Int, private val navController: NavController) : RecyclerView.Adapter<OfferViewHolder>() {

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

            root.setOnLongClickListener { _ ->
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
                Handler(Looper.getMainLooper()).postDelayed({

                    modalBottomSheet.view?.findViewById<Button>(R.id.btnEdit)?.setOnClickListener {
                        Log.d("MyTag", "Edit Button Clicked")
                        val directions = OfferFragmentDirections.actionOfferFragmentToUpdateOfferFragment(offer.name,
                            offer.price.toString(), offer.type, offer.id
                        )
                        navController.navigate(directions)

                        modalBottomSheet.dismiss()
                    }

                    modalBottomSheet.view?.findViewById<Button>(R.id.btnDelete)?.setOnClickListener {

                        Log.d("MyTag", "Delete Button Clicked")
                        CoroutineScope(Dispatchers.IO).launch {  // Perform network call on IO thread
                            try {
                                offer.company_id = null
                                offerApi.deleteOffer(offer.id, offer).enqueue(object : Callback<Offer> {
                                    override fun onResponse(
                                        call: Call<Offer>,
                                        response: Response<Offer>
                                    ) {
                                        Log.d("MyTag", "HEHE $call || ${response.body()}")

                                        offerApi.getOffer(companyId).enqueue(object : Callback<List<Offer>> {
                                            override fun onResponse(call: Call<List<Offer>>, response: Response<List<Offer>>) {
                                                if (response.isSuccessful) {
                                                    val offersApiResponse = response.body()
                                                    Log.d("MyTag", "$offersApiResponse || ${response.body()}")
                                                    if (offersApiResponse != null) {
                                                        offers = offersApiResponse
                                                        notifyDataSetChanged()
                                                    }
                                                }
                                            }

                                            override fun onFailure(call: Call<List<Offer>>, t: Throwable) {
                                                Log.d("MyTag", "$call", t)
                                            }
                                        })
                                    }

                                    override fun onFailure(call: Call<Offer>, t: Throwable) {
                                        Log.d("MyTag", "$call", t)
                                    }
                                })
                            } catch (e: Exception) {
                                Log.e("MyTag", "Oops $e")
                            }
                        }
                        modalBottomSheet.dismiss()
                    }

                }, 100)
                true
            }

            tvOfferName.text = offer.name
            tvPrice.text = offer.price.toString()
            tvType.text = offer.type.toSentenceCase()
        }
    }
    fun String.toSentenceCase(): String {
        return lowercase()
            .split("\\s+".toRegex())
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }
}

class OfferViewHolder(val binding : ItemOfferBinding) : RecyclerView.ViewHolder(binding.root)