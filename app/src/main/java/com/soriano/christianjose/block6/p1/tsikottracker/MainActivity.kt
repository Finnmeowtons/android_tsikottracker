package com.soriano.christianjose.block6.p1.tsikottracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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

        binding.navView.setNavigationItemSelectedListener {menuItem ->

            when(menuItem.itemId){
                R.id.item_dashboard -> navController.navigate(R.id.action_side_nav_pop_up_to_dashboard)
                R.id.item_record -> navController.navigate(R.id.action_side_nav_pop_up_to_record)
                R.id.item_analytics -> navController.navigate(R.id.action_side_nav_pop_up_to_analytic)
                R.id.item_offers -> navController.navigate(R.id.action_side_nav_pop_up_to_offer)
                R.id.customerFragment -> navController.navigate(R.id.action_side_nav_pop_up_to_customer)
                R.id.item_employees -> navController.navigate(R.id.action_side_nav_pop_up_to_employee)
                R.id.settingsFragment -> navController.navigate(R.id.action_side_nav_pop_up_to_settings)
            }

            binding.drawerLayout.closeDrawers()
            true
        }




    }
}