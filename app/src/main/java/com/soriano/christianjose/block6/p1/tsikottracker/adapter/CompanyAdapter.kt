package com.soriano.christianjose.block6.p1.tsikottracker.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.soriano.christianjose.block6.p1.tsikottracker.MyNavDirections
import com.soriano.christianjose.block6.p1.tsikottracker.R
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.data.DeleteResponse
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ItemCompaniesBinding
import com.soriano.christianjose.block6.p1.tsikottracker.modal.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompanyAdapter(
    private val companyApi: CompanyApi,
    private val fragmentManager: FragmentManager,
    private val companyId: Int,
    private val navController: NavController,
    private val context: Context,
    private val storedUserId: Int,
    private val activity: FragmentActivity?
) : RecyclerView.Adapter<CompanyViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Company>() {
        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var companies: List<Company>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
            ItemCompaniesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.binding.apply {
            val company = companies[position]
            tvId.text = company.name
            root.setOnLongClickListener {
                val modalBottomSheet = ModalBottomSheet()
                modalBottomSheet.show(fragmentManager, ModalBottomSheet.TAG)
                Handler(Looper.getMainLooper()).postDelayed({
                    val btnEdit = modalBottomSheet.view?.findViewById<Button>(R.id.btnEdit)
                    btnEdit?.text = "Rename"
                    btnEdit?.setOnClickListener {
                        val builder = MaterialAlertDialogBuilder(context)
                        val dialogView = LayoutInflater.from(context)
                            .inflate(R.layout.dialog_update_company_name, null)
                        builder.setView(dialogView)

                        val etNewCompanyName =
                            dialogView.findViewById<TextInputEditText>(R.id.etCompanyName)
                        val companyNameLayout =
                            dialogView.findViewById<TextInputLayout>(R.id.companyNameLayout)
                        etNewCompanyName.setText(company.name)

                        val btnRenameConfirm =
                            dialogView.findViewById<Button>(R.id.btnRenameConfirm)
                        val btnCancel = dialogView.findViewById<Button>(R.id.btnRenameCancel)
                        val dialog = builder.show()

                        btnRenameConfirm.setOnClickListener {
                            val newName = etNewCompanyName.text.toString()
                            val sendCompany = Company(
                                id = companyId,
                                newName,
                                company.invitation_code,
                                owner_id = storedUserId
                            )
                            if (newName.length < 3) {
                                companyNameLayout.error = "Must at least have 3 characters."
                            } else if (newName == company.name) {
                                companyNameLayout.error = "Please provide a new name."
                            } else {
                                companyApi.updateCompanyName(companyId, sendCompany)
                                    .enqueue(object :
                                        Callback<Company> {
                                        override fun onResponse(
                                            call: Call<Company>,
                                            response: Response<Company>
                                        ) {
                                            if (response.isSuccessful) {
                                                Log.d("MyTag", "Renamed $sendCompany")
                                                companyApi.getCompanies(storedUserId)
                                                    .enqueue(object : Callback<List<Company>> {
                                                        override fun onResponse(
                                                            call: Call<List<Company>>,
                                                            response: Response<List<Company>>
                                                        ) {
                                                            if (response.isSuccessful) {
                                                                val companyApiResponse =
                                                                    response.body()
                                                                Log.d(
                                                                    "MyTag",
                                                                    "$companyApiResponse || ${response.body()}"
                                                                )
                                                                if (companyApiResponse != null) {
                                                                    companies = companyApiResponse
                                                                    notifyDataSetChanged()
                                                                    dialog.dismiss()
                                                                } else {
                                                                    Log.d(
                                                                        "MyTag",
                                                                        "Api Error2 $call || $response"
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        override fun onFailure(
                                                            call: Call<List<Company>>,
                                                            t: Throwable
                                                        ) {
                                                            Log.d("MyTag", "Oh no2 $call", t)
                                                        }
                                                    })
                                            } else {
                                                Log.d("MyTag", "Api Error $call || $response")
                                            }
                                        }

                                        override fun onFailure(call: Call<Company>, t: Throwable) {
                                            Log.d("MyTag", "Oh no $call", t)
                                        }
                                    })
                            }
                        }

                        btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                        modalBottomSheet.dismiss()
                    }


                    modalBottomSheet.view?.findViewById<Button>(R.id.btnDelete)
                        ?.setOnClickListener {
                            val authUserManager = AuthUserManager(context)
                            Log.d("MyTag", "Delete Button Clicked")
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Delete Company?")
                                .setMessage("Are you sure you want to delete this company? This action cannot be undone.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Delete") { _, _ ->
                                    Log.d("MyTag", "Delete Button Clicked2")
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.d("MyTag", "Delete Button Clicked2")
                                        try {
                                            Log.d("MyTag", "Delete Button Clicked3")
                                            Log.d("MyTag", "Delete Button Clicked3")
                                            companyApi.deleteCompany(company.id, storedUserId)
                                                .enqueue(object : Callback<DeleteResponse> {
                                                    override fun onResponse(
                                                        call: Call<DeleteResponse>,
                                                        response: Response<DeleteResponse>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            Log.d(
                                                                "MyTag",
                                                                "${response.body()?.nextCompanyId} || $response"
                                                            )
                                                            if (response.body()?.nextCompanyId == 0) {
                                                                val directions = MyNavDirections.actionToCompanySetupFragment(true)
                                                                navController.navigate(directions)
                                                                activity?.findViewById<MaterialToolbar>(R.id.topAppBar)?.visibility = View.GONE

                                                            } else if (authUserManager.getStoredCompanyId() == company.id) {
                                                                authUserManager.storeCompanyId(
                                                                    response.body()?.nextCompanyId.toString()
                                                                        .toInt()
                                                                )
                                                            }
                                                            companyApi.getCompanies(storedUserId)
                                                                .enqueue(object :
                                                                    Callback<List<Company>> {
                                                                    override fun onResponse(
                                                                        call: Call<List<Company>>,
                                                                        response: Response<List<Company>>
                                                                    ) {
                                                                        if (response.isSuccessful) {
                                                                            val companyApiResponse =
                                                                                response.body()
                                                                            Log.d(
                                                                                "MyTag",
                                                                                "$companyApiResponse || ${response.body()}"
                                                                            )
                                                                            if (companyApiResponse != null) {
                                                                                companies =
                                                                                    companyApiResponse
                                                                                notifyDataSetChanged()
                                                                            } else {
                                                                                Log.d(
                                                                                    "MyTag",
                                                                                    "Api Error3 $call || $response"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    override fun onFailure(
                                                                        call: Call<List<Company>>,
                                                                        t: Throwable
                                                                    ) {
                                                                        Log.d(
                                                                            "MyTag",
                                                                            "Oh no3 $call",
                                                                            t
                                                                        )
                                                                    }
                                                                })


                                                        } else {
                                                            Log.d("MyTag","Oh no33 $call")
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<DeleteResponse>,
                                                        t: Throwable
                                                    ) {
                                                        Log.d("MyTag", "Oh no34 $call", t)
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

class CompanyViewHolder(val binding: ItemCompaniesBinding) : RecyclerView.ViewHolder(binding.root) {

}