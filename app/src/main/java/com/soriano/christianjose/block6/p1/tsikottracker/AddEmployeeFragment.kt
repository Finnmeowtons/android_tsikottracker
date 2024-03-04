package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.soriano.christianjose.block6.p1.tsikottracker.adapter.AddEmployeeAdapter
import com.soriano.christianjose.block6.p1.tsikottracker.api.EmployeeApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAddEmployeeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddEmployeeFragment : Fragment() {
    private var _binding: FragmentAddEmployeeBinding? = null
    private lateinit var retrofit: Retrofit
    private lateinit var employeeApi : EmployeeApi
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEmployeeBinding.inflate(inflater, container, false)
        val view = binding.root
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        val authUserManager = AuthUserManager(requireContext())
        val companyId = authUserManager.getStoredCompanyId()
        val storedUserId = authUserManager.getStoredUserId()

        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        employeeApi = retrofit.create(EmployeeApi::class.java)

        val adapter = AddEmployeeAdapter(companyId)
        binding.rvAddServices.adapter = adapter

        binding.btnAddEmployee.setOnClickListener {
            adapter.addItem()
        }

        binding.btnSubmit.setOnClickListener {
            if (adapter.validateFields(binding, adapter)) {
                val services = adapter.getEmployees()
                for (service in services) {
                    Log.d("MyTag", service.toString())

                    employeeApi.createEmployee(service).enqueue(object : Callback<Employee> {
                        override fun onResponse(
                            call: Call<Employee>,
                            response: Response<Employee>
                        ) {
                            if (response.isSuccessful) {
                                val createdOffer = response.body()
                                Log.d("MyTag", "Response: $createdOffer")

                                if (isAdded) { // Check if the fragment is attached
                                    findNavController().popBackStack()
                                    activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
                                        View.VISIBLE
                                    activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                                        ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                }

                            } else {
                                Log.e(
                                    "MyTag",
                                    "API Error: ${response.code()} - ${response.message()}"
                                )
                            }
                        }

                        override fun onFailure(call: Call<Employee>, t: Throwable) {
                            Log.e("MyTag", "Network Error $call", t)
                        }
                    })
                }
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Empty Fields")
                    .setMessage("Please enter values.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}