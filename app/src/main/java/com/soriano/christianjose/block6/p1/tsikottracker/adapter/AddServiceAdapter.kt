package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Offer
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddServiceBinding
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentUpdateRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemAddServiceBinding

class AddServiceAdapter(private val companyId: Int, private val context: Context) : RecyclerView.Adapter<AddServiceViewHolder>() {

    private val offerList = mutableListOf(Offer(id = 0, name ="", price =0, type ="", company_id = companyId))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddServiceViewHolder {
        return AddServiceViewHolder(ItemAddServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    override fun onBindViewHolder(holder: AddServiceViewHolder, position: Int) {
        val currentItem = offerList[position]
        Log.e("MyTag", currentItem.toString())

        if (position != 0) {
            holder.binding.etServiceInput.setOnLongClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Delete Offer?")
                    .setMessage("Are you sure you want to remove this offer?")
                    .setPositiveButton("Delete") { _, _ ->
                        offerList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }

        holder.binding.etServiceInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val name = p0.toString()
                if (name.isNotBlank()) {
                    currentItem.name = name
                } else {
                    holder.binding.etServiceInput.error = "Please enter name"
                }
            }
        })

        holder.binding.etServiceType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val type = p0.toString().lowercase()
                if (type.isNotBlank()) {
                    currentItem.type = type
                } else {
                    holder.binding.etServiceType.error = "Please pick type"
                }
            }
        })

        holder.binding.etServicePriceInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //haha
            }

            override fun afterTextChanged(p0: Editable?) {
                val priceString = p0.toString()

                if (priceString.isNotBlank()) {
                    try {
                        currentItem.price = priceString.toInt()
                    } catch (e: NumberFormatException) {
                        holder.binding.etServicePriceInput.error = "Please enter a valid price"
                    }
                } else {
                    currentItem.price = 0
                }
            }
        })
    }

    fun addItem() {
        offerList.add(Offer(id = 0, name = "", price = 0, type = "", company_id = companyId))
        notifyItemInserted(offerList.size+1)
    }

    fun addItemOffer(offer: Offer) { // Add a new method
        offerList.add(offer)
        notifyItemInserted(offerList.size - 1)
    }


    fun getServices(): List<Offer> {
        return offerList
    }


    fun validateFieldsService(binding: FragmentAddServiceBinding, adapter: AddServiceAdapter): Boolean {
        var allValid = true

        for (i in 0 until adapter.offerList.size) {
            val viewHolder = binding.rvAddServices.findViewHolderForAdapterPosition(i) as? AddServiceViewHolder
            if (viewHolder != null) {
                val etName = viewHolder.binding.etServiceInput.text
                if (etName.isNullOrBlank()) {
                    viewHolder.binding.offerLayout.error = "Please input an offer"
                    allValid = false
                } else {
                    viewHolder.binding.offerLayout.error = null
                }

                val etType = viewHolder.binding.etServiceType.text
                if (etType.isNullOrBlank()){
                    viewHolder.binding.typeLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.typeLayout.error = null
                }

                val etPrice = viewHolder.binding.etServicePriceInput.text
                if (etPrice.isNullOrBlank()){
                    viewHolder.binding.priceLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.priceLayout.error = null
                }

                if (!allValid) {
                    return false
                }
            }
        }
        return allValid
    }

    fun validateFieldsRecord(binding: FragmentAddRecordBinding, adapter: AddServiceAdapter): Boolean {
        var allValid = true

        for (i in 0 until adapter.offerList.size) {
            val viewHolder = binding.rvAddServices.findViewHolderForAdapterPosition(i) as? AddServiceViewHolder
            if (viewHolder != null) {
                val etName = viewHolder.binding.etServiceInput.text
                if (etName.isNullOrBlank()) {
                    viewHolder.binding.offerLayout.error = "Please input an offer"
                    allValid = false
                } else {
                    viewHolder.binding.offerLayout.error = null
                }

                val etType = viewHolder.binding.etServiceType.text
                if (etType.isNullOrBlank()){
                    viewHolder.binding.typeLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.typeLayout.error = null
                }

                val etPrice = viewHolder.binding.etServicePriceInput.text
                if (etPrice.isNullOrBlank()){
                    viewHolder.binding.priceLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.priceLayout.error = null
                }

                if (!allValid) {
                    return false
                }
            }
        }
        return allValid
    }

    fun validateFieldsUpdateRecord(binding: FragmentUpdateRecordBinding, adapter: AddServiceAdapter): Boolean {
        var allValid = true

        for (i in 0 until adapter.offerList.size) {
            val viewHolder = binding.rvAddServices.findViewHolderForAdapterPosition(i) as? AddServiceViewHolder
            if (viewHolder != null) {
                val etName = viewHolder.binding.etServiceInput.text
                if (etName.isNullOrBlank()) {
                    viewHolder.binding.offerLayout.error = "Please input an offer"
                    allValid = false
                } else {
                    viewHolder.binding.offerLayout.error = null
                }

                val etType = viewHolder.binding.etServiceType.text
                if (etType.isNullOrBlank()){
                    viewHolder.binding.typeLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.typeLayout.error = null
                }

                val etPrice = viewHolder.binding.etServicePriceInput.text
                if (etPrice.isNullOrBlank()){
                    viewHolder.binding.priceLayout.error = "Invalid"
                    allValid = false
                } else {
                    viewHolder.binding.priceLayout.error = null
                }

                if (!allValid) {
                    return false
                }
            }
        }
        return allValid
    }
}

class AddServiceViewHolder(val binding: ItemAddServiceBinding): RecyclerView.ViewHolder(binding.root){

}