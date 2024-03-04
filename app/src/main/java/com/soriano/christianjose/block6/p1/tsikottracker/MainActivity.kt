package com.soriano.christianjose.block6.p1.tsikottracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.soriano.christianjose.block6.p1.tsikottracker.auth.AuthUserManager
import com.soriano.christianjose.block6.p1.tsikottracker.databinding.ActivityMainBinding
import com.soriano.christianjose.block6.p1.tsikottracker.viewmodel.SharedViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.appbarTitle.observe(this) { newTitle ->
            binding.topAppBar.title = newTitle
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController




        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener {menuItem ->

            when(menuItem.itemId){
                R.id.item_dashboard -> navController.navigate(R.id.action_side_nav_pop_up_to_dashboard)
                R.id.item_record -> navController.navigate(R.id.action_side_nav_pop_up_to_record)
                R.id.item_analytics -> navController.navigate(R.id.action_side_nav_pop_up_to_analytic)
                R.id.item_offers -> navController.navigate(R.id.action_side_nav_pop_up_to_offer)
                R.id.item_companies -> navController.navigate(R.id.action_side_nav_pop_up_to_company)
                R.id.item_customers -> navController.navigate(R.id.action_side_nav_pop_up_to_customer)
                R.id.item_employees -> navController.navigate(R.id.action_side_nav_pop_up_to_employee)
                R.id.item_settings -> navController.navigate(R.id.action_side_nav_pop_up_to_settings)
                R.id.item_logout -> {
                    val authManager = AuthUserManager(this)
                    authManager.storeToken(null)
                    authManager.storeUserId(0)
                    authManager.storeEmail(null)
                    navController.navigate(R.id.action_logout)
                }
            }

            binding.drawerLayout.closeDrawers()
            true
        }

        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressedDispatcher
            }
        }


    }
}