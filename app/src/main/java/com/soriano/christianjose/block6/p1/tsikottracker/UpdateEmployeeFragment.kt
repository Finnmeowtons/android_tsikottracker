package com.soriano.christianjose.block6.p1.tsikottracker

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.soriano.christianjose.block6.p1.tsikottracker.api.EmployeeApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Employee
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentUpdateEmployeeBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateEmployeeFragment : Fragment() {
    private var _binding: FragmentUpdateEmployeeBinding? = null
    private lateinit var retrofit: Retrofit
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: UpdateEmployeeFragmentArgs by navArgs()
    private lateinit var employeeApi: EmployeeApi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateEmployeeBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Edit Employee")
        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.arrow_back)
        toolbar?.setNavigationOnClickListener {
            if (isAdded) {
                findNavController().popBackStack()
            }
        }
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
        val authUserManager = AuthUserManager(requireContext())
        val companyId = authUserManager.getStoredCompanyId()
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        employeeApi = retrofit.create(EmployeeApi::class.java)


        val idArgs = args.updateIdValue
        val nameArgs = args.updateNameValue
        val positionArgs = args.updatePositionValue
        val contactDetailArgs = args.updateContactValue

        binding.etName.setText(nameArgs)
        binding.etPosition.setText(positionArgs)
        if (contactDetailArgs != "null") {
            binding.etContactDetail.setText(contactDetailArgs)
        }



        binding.btnConfirmEdit.setOnClickListener {
            var allValid = true

            val etName = binding.etName.text
            val etPosition = binding.etPosition.text
            if (etName.toString() == nameArgs && etPosition.toString() == positionArgs && binding.etContactDetail.text.toString() == contactDetailArgs || binding.etContactDetail.text.isNullOrBlank()){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Changes Not Submitted")
                    .setMessage("It appears you haven't made any edits. Please review your changes and try again.")
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            if (etName.isNullOrBlank()) {
                binding.nameLayout.error = "Please input name"
                allValid = false
            } else {
                binding.nameLayout.error = null
            }

            if (etPosition.isNullOrBlank()) {
                binding.positionLayout.error = "Enter a position"
                allValid = false
            } else {
                binding.positionLayout.error = null
            }
            if (!allValid){
                return@setOnClickListener
            }

            val employee = Employee(0, etName.toString(),  binding.etContactDetail.text.toString(), etPosition.toString(), companyId)
            employeeApi.deleteEmployee(idArgs, employee).enqueue(object : Callback<Employee> {
                override fun onResponse(call: Call<Employee>, response: Response<Employee>) {
                    if (response.isSuccessful) {
                        val createdEmployee = response.body()
                        Log.d("MyTag", "Response: $createdEmployee")

                        if (isAdded) {
                            findNavController().popBackStack()
                            activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.VISIBLE
                            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
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





        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val toolbar =
            activity?.findViewById<MaterialToolbar>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.topAppBar)
        toolbar?.setNavigationIcon(com.soriano.christianjose.block6.p1.tsikottracker.R.drawable.menu_fill0_wght400_grad0_opsz24)

        activity?.findViewById<DrawerLayout>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        val drawerLayout =
            activity?.findViewById<DrawerLayout>(com.soriano.christianjose.block6.p1.tsikottracker.R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }
}