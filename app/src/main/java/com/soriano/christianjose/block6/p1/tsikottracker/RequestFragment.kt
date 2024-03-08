package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentRequestBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RequestFragment : Fragment() {
    private var _binding: FragmentRequestBinding? = null
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://146.190.111.209/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val view = binding.root





        return view
    }
}