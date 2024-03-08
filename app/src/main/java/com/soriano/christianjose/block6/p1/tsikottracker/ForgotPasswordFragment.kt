package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.auth.api.ForgotPasswordApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.ForgotPasswordRequest
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LaravelAuthenticationResponse
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentForgotPasswordBinding
import com.soriano.christianjose.block6.p1.tsikottracker.dialog.ForgotPasswordDialogFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        val retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.btnEmail.setOnClickListener {
            binding.emailLayout.error = null
            binding.progressBar.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE


            val service = retrofit.create(ForgotPasswordApi::class.java)

            val email = binding.etEmail.text.toString()

            val forgotPasswordRequest = ForgotPasswordRequest(email)

            service.forgotPassword(forgotPasswordRequest).enqueue(object: Callback<LaravelAuthenticationResponse>{
                override fun onResponse(call: Call<LaravelAuthenticationResponse>, response: Response<LaravelAuthenticationResponse>) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.e("MyTag", "${response.code()} ${response.message()}")
                        binding.progressBar.visibility = View.GONE
                        binding.view.visibility = View.GONE
                        findNavController().navigate(R.id.action_forgotPasswordDialog)
                    } else {
                        Log.e("MyTag", "Forgot Password Failed: ${response.code()} ${response.message()}")
                        binding.progressBar.visibility = View.GONE
                        binding.view.visibility = View.GONE
                        binding.emailLayout.error = "We can't find a user with that email address."
                        // Show user-friendly error message here
                    }
                }

                override fun onFailure(call: Call<LaravelAuthenticationResponse>, t: Throwable) {
                    // Handle network errors
                    Log.e("MyTag", "Network Failed: $call", t)
                    binding.progressBar.visibility = View.GONE
                    binding.view.visibility = View.GONE
                    // Show user-friendly error message here
                }
            })
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
    }
}