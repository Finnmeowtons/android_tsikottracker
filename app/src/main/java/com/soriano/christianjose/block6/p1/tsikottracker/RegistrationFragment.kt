package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.auth.api.RegistrationApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.RegistrationRequest
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.TokenResponse
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? =null
    private val binding get() = _binding!!
    private var isName = false
    private var isEmail = false
    private var isPassword = false
    private var isConfirmPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val view = binding.root
        val retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(RegistrationApi::class.java)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordConfirmation = binding.etPasswordConfirmation.text.toString()
            val registrationRequest = RegistrationRequest(name, email, password, passwordConfirmation)

            val nameField = binding.etName
            val emailField = binding.etEmail
            val passwordField = binding.etPassword
            val confirmPasswordField = binding.etPasswordConfirmation

            if (nameField.text?.trim().isNullOrEmpty()){
                binding.nameLayout.error = "Field is empty"
                isName = false
            } else if(nameField.text?.trim()?.length!!  < 5){
                binding.nameLayout.error = "Should have at least 5 characters"
                isName = false
            } else {
                binding.nameLayout.error = null
                isName = true
            }

            if (emailField.text?.trim().isNullOrEmpty()){
                binding.emailLayout.error = "Field is empty"
                isEmail = false
            } else if(!emailField.text.toString().contains(".com") && !emailField.text.toString().contains("@")){
                binding.emailLayout.error = "The email address is incomplete"
                isEmail = false
            } else {
                binding.emailLayout.error = null
                isEmail = true
            }

            if (passwordField.text?.trim().isNullOrEmpty()){
                binding.passwordLayout.error = "Field is empty"
                isPassword = false
            } else if (passwordField.text?.trim()?.length!! < 8){
                binding.passwordLayout.error = "Should have at least 8 characters"
                isPassword = false
            } else {
                isPassword = true
                binding.passwordLayout.error = null
            }

            if (confirmPasswordField.text?.trim().isNullOrEmpty()){
                binding.confirmPasswordLayout.error = "Field is empty"
                isConfirmPassword = false
            } else if (confirmPasswordField.text.toString().trim() != passwordField.text.toString().trim()){
                binding.confirmPasswordLayout.error = "Passwords do not match"
                isConfirmPassword = false
            } else {
                isConfirmPassword = true
                binding.confirmPasswordLayout.error = null
            }

            if(isEmail && isConfirmPassword && isName && isPassword) {
                binding.progressBar.visibility = View.VISIBLE
                binding.view.visibility = View.VISIBLE

                service.register(registrationRequest).enqueue(object : Callback<TokenResponse> {
                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        binding.progressBar.visibility = View.GONE
                        binding.view.visibility = View.GONE
                        if (response.isSuccessful && response.code() == 201) {
                            val receivedToken = response.body()?.token
                            val receivedEmail = response.body()?.email
                            val receivedUserId = response.body()?.userId
                            val authManager = AuthUserManager(requireContext())
                            if (receivedToken != null) {
                                authManager.storeToken(receivedToken)
                            }
                            if (receivedEmail != null) {
                                authManager.storeEmail(receivedEmail)
                            }
                            if (receivedUserId != null) {
                                authManager.storeUserId(receivedUserId)
                            }

                            Log.d("MyTag", "Response: $call || ${response.body()} || ${authManager.getStoredUserId()}")
                            val directions = MyNavDirections.actionToCompanySetupFragment(true)
                            findNavController().navigate(directions)
                        } else {
                            Log.e("MyTag", "Registration Failed: $call || ${response.body()}")
                        }
                    }

                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        binding.view.visibility = View.GONE
                        binding.emailLayout.error = "Email already taken"
                        Log.e("MyTag", "Network Failed: $call")
                    }
                })
            }
        }

        binding.tvBtnHaveAccount.setOnClickListener {
            findNavController().popBackStack()
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
