package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentAnalyticBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel

class AnalyticFragment : Fragment() {
    private var _binding: FragmentAnalyticBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)
        val view = binding.root
        sharedViewModel.updateAppBarTitle("Analytics")


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}