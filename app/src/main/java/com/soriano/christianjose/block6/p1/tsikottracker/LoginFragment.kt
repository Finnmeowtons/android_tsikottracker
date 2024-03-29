package com.soriano.christianjose.block6.p1.tsikottracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.auth.api.LoginApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.api.LogoutApi
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.LoginRequest
import com.soriano.christianjose.block6.p1.tsikottracker.auth.data.TokenResponse
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.FragmentLoginAndRegisterBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginAndRegisterBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private var isEmail = false
    private var isPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginAndRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
        activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)



        val retrofit = Retrofit.Builder()
            .baseUrl("http://146.190.111.209/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()




        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.trim().toString()
            val password = binding.etPassword.text?.trim().toString()

            val service = retrofit.create(LoginApi::class.java)
            val loginRequest = LoginRequest(email, password)

            val emailField = binding.etEmail
            val passwordField = binding.etPassword

            if (emailField.text?.trim().isNullOrEmpty()){
                binding.emailLayout.error = "Field is empty"
                isEmail = false
            } else if(!emailField.text.toString().contains(".com") || !emailField.text.toString().contains("@")){
                Log.e("MyTag", emailField.text.toString().contains(".com").toString())
                binding.emailLayout.error = "The email address is incomplete"
                isEmail = false
            } else {
                Log.e("MyTagg", emailField.text.toString().contains(".com").toString())
                binding.emailLayout.error = null
                isEmail = true
            }
            if (passwordField.text?.trim().isNullOrEmpty()){
                binding.passwordLayout.error = "Field is empty"
                isPassword = false
            } else {
                isPassword = true
                binding.passwordLayout.error = null
            }
            if (isEmail && isPassword) {
                binding.progressBar.visibility = View.VISIBLE
                binding.view.visibility = View.VISIBLE


                service.login(loginRequest)
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onResponse(
                            call: Call<TokenResponse>,
                            response: Response<TokenResponse>
                        ) {
                            binding.progressBar.visibility = View.GONE
                            binding.view.visibility = View.GONE
                            if (response.isSuccessful && response.code() == 200) {
                                Log.e(
                                    "MyTag",
                                    "Login Success: $call || ${response.body()?.token}"
                                )

                                val receivedToken = response.body()?.token
                                val receivedEmail = response.body()?.email
                                val receivedUserId = response.body()?.userId
                                Log.d("MyTag", "$receivedToken || $receivedEmail || $receivedUserId")
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
                                activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
                                    View.VISIBLE
                                activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                                    ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                                findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
                            } else {
                                binding.emailLayout.error = "Invalid Credentials"
                                binding.passwordLayout.error = "Invalid Credentials"
                                emailField.text = null
                                Log.e("MyTag", "Login Failed: $call || ${response.raw()}")

                            }
                        }

                        override fun onFailure(
                            call: Call<TokenResponse>,
                            t: Throwable
                        ) {
                            binding.progressBar.visibility = View.GONE
                            binding.view.visibility = View.GONE
                            Log.e("MyTag", "Network Failed: $call")
                        }
                    })
            }
        }




        binding.tvBtnNoAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val authManager = AuthUserManager(requireContext())
        val storedToken = authManager.getStoredToken()
        Log.d("MyTag", "${authManager.getEmail()} || ${authManager.getStoredUserId()} || ${authManager.getStoredToken()} ")
        if (storedToken != null) {
            // User is logged in
            activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility =
                View.VISIBLE
            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
                ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            if (isAdded) {
                findNavController().navigate(R.id.action_side_nav_pop_up_to_record)
            }
        }
    }
}