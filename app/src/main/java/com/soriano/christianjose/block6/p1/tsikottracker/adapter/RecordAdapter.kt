package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.RecordFragmentDirections
import com.soriano.christianjose.block6.p1.tsikottracker.api.RecordApi
import com.soriano.christianjose.block6.p1.tsikottracker.data.DeleteResponse
import com.soriano.christianjose.block6.p1.tsikottracker.data.GetRecord
import com.soriano.christianjose.block6.p1.tsikottracker.data.Record
import com.soriano.christianjose.block6.p1.tsikottracker.data.StartEndDate
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemRecordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.modal.ModalBottomSheet
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.OfferIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class RecordAdapter(private val recordApi: RecordApi, private val fragmentManager: FragmentManager, private val companyId: Int, private val navController: NavController, private val offerIdsViewModel: OfferIds, private val context: Context, private val startEndDate: StartEndDate) : RecyclerView.Adapter<RecordViewHolder>() {
    private val diffCallback = object : DiffUtil.ItemCallback<GetRecord>(){
        override fun areItemsTheSame(oldItem: GetRecord, newItem: GetRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GetRecord, newItem: GetRecord): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var records: List<GetRecord>
        get() = differ.currentList
        set(value) { differ.submitList(value) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.binding.apply {
            val record = records[position]
            val offerNames = record.offers.joinToString(", ") { it.offer }
            val offerPrices = record.offers.sumOf { it.offer_price }
            tvRegistrationPlateValue.text = record.customer_car_plate_number
            if (record.employee_name.isNullOrEmpty()){
                tvEmployeeNameValue.visibility = View.GONE
                tvEmployeeNameTitle.visibility = View.GONE
            } else {
                tvEmployeeNameValue.text = record.employee_name
            }
            if (record.customer_name.isNullOrEmpty()){
                tvCustomerNameValue.visibility = View.GONE
                tvCustomerNameTitle.visibility = View.GONE
            } else {
                tvCustomerNameValue.text = record.customer_name
            }
            tvOfferValue.text = offerNames
            tvPriceValue.text = offerPrices.toString()
            val outputFormat = SimpleDateFormat("HH:mm, LLL dd", Locale.getDefault())
            val formattedTime = record.time?.let { outputFormat.format(it) }
            tvTimeValue.text = formattedTime

            root.setOnLongClickListener {
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
                Handler(Looper.getMainLooper()).postDelayed({

                    val offerIdList = record.offers.map { it.offer_id } // Get IDs
                    offerIdsViewModel.setOfferIdList(offerIdList)
                    Log.d("MyTag", "$offerIdList || ${offerIdsViewModel.setOfferIdList(offerIdList)}")

                    modalBottomSheet.view?.findViewById<Button>(R.id.btnEdit)?.setOnClickListener {
                        Log.d("MyTag", "Edit Button Clicked")
                        val directions = RecordFragmentDirections.actionRecordFragment2ToUpdateRecordFragment(
                            record.customer_name.toString(),
                            record.customer_car_plate_number,
                            record.employee_name.toString(),
                            record.employee_position.toString(),
                            record.notes.toString(),
                            record.id
                        )
                        navController.navigate(directions)



                        modalBottomSheet.dismiss()
                    }
                    modalBottomSheet.view?.findViewById<Button>(R.id.btnDelete)?.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Delete Record?")
                            .setMessage("Are you sure you want to delete this record? This action cannot be undone.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete") {_,_ ->

                                CoroutineScope(Dispatchers.IO).launch {  // Perform network call on IO thread
                                    try {
                                        recordApi.deleteRecord(record.id).enqueue(object :
                                            Callback<DeleteResponse>{
                                            override fun onResponse(
                                                call: Call<DeleteResponse>,
                                                response: Response<DeleteResponse>
                                            ) {
                                                if(response.isSuccessful){
                                                    recordApi.getRecords(companyId, startEndDate).enqueue(object : Callback<List<GetRecord>>{
                                                        override fun onResponse(
                                                            call: Call<List<GetRecord>>,
                                                            response: Response<List<GetRecord>>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val recordApiResponse = response.body()
                                                                Log.d("MyTag", "$records || ${response.body()}")
                                                                if (recordApiResponse != null) {
                                                                    records = recordApiResponse
                                                                    notifyDataSetChanged()
                                                                }
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<List<GetRecord>>,
                                                            t: Throwable
                                                        ) {
                                                            Log.d("MyTag", "Oh no $call", t)
                                                        }
                                                    })
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<DeleteResponse>,
                                                t: Throwable
                                            ) {
                                                Log.d("MyTag", "Oh no $call", t)
                                            }

                                        })
                                    } catch (e: Exception) {
                                        Log.e("MyTag", "Oops $e")
                                    }
                                }

                            }
                            .show()
                        modalBottomSheet.dismiss()
                    }

                }, 100)
                true
            }

        }
    }
}

class RecordViewHolder(val binding: ItemRecordBinding): RecyclerView.ViewHolder(binding.root){

}