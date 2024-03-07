package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentCompanyDashboardBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel

class CompanyDashboardFragment : Fragment() {
    private var _binding: FragmentCompanyDashboardBinding? =null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentCompanyDashboardBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Dashboard")
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
            View.VISIBLE
        activity?.findViewById<MaterialToolbar>(R.id.topAppBar)?.visibility =
            View.VISIBLE
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}