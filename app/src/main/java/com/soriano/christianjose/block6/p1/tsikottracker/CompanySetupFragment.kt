package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.soriano.christianjose.block6.p1.tsikottracker.api.CompanyApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.data.Company
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentCompanySetupBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanySetupFragment : Fragment() {

    private var _binding: FragmentCompanySetupBinding? = null
    private var menuItem: MenuItem? = null
    private lateinit var retrofit: Retrofit
    private lateinit var companyAPI: CompanyApi
    private val args : CompanySetupFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanySetupBinding.inflate(inflater, container, false)
        val view = binding.root
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        sharedViewModel.updateAppBarTitle("Company Setup")
        retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        companyAPI = retrofit.create(CompanyApi::class.java)
        if (!args.myArgs){
            binding.tvCompanySetupTitle.visibility = View.GONE
            val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
            toolbar?.setNavigationIcon(R.drawable.arrow_back)

            toolbar?.setNavigationOnClickListener {
                if(isAdded) {
                    findNavController().popBackStack()
                }
            }
        }




        binding.btnCreate.setOnClickListener {
            val companyName = binding.etCompanyName.text.toString().trim().lowercase()
            val authUserManager = AuthUserManager(requireContext())
            val storedUserId = authUserManager.getStoredUserId()
            Log.d("MyTag", storedUserId.toString())

            if (companyName.length >= 3) {
                val company = Company(id = 0, name = companyName, owner_id = storedUserId)
                companyAPI.createCompany(company).enqueue(object : Callback<Company>{
                    override fun onResponse(call: Call<Company>, response: Response<Company>) {
                        if (response.isSuccessful) {
                            val createdCompany = response.body()
                            val companyId = response.body()?.id
                            if (companyId != null) {
                                authUserManager.storeCompanyId(companyId)
                            }

                            Log.d("MyTag", "${createdCompany.toString()} || $companyId")

                            if (args.myArgs){
                                Log.d("MyTag", "If ${args.myArgs}")
                                val directions = CompanySetupFragmentDirections.actionCompanySetupFragmentToAddServiceFragment(false)
                                findNavController().navigate(directions)
                            } else {
                                Log.d("MyTag", "Else ${args.myArgs}")
                                val directions = CompanySetupFragmentDirections.actionCompanySetupFragmentToAddServiceFragment(true)
                                findNavController().navigate(directions)
                            }

                        } else {
                            // Handle API error
                        }
                    }

                    override fun onFailure(call: Call<Company>, t: Throwable) {
                        Log.d("MyTag", call.toString(), t)
                    }

                })
            } else {
                // Display error (e.g., in a TextView)
            }

        }

        binding.btnJoin.setOnClickListener {

            activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.VISIBLE
            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            view.findNavController().navigate(R.id.action_side_nav_pop_up_to_dashboard)


        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isAdded) {
                findNavController().popBackStack()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val toolbar = activity?.findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar?.setNavigationIcon(R.drawable.menu_fill0_wght400_grad0_opsz24)
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }


}