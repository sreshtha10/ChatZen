package com.sreshtha.chatappandroid.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ActivityHomeBinding
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel
import com.sreshtha.chatappandroid.viewmodel.HomeViewModelFactory
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    lateinit var viewModel:HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        val viewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)
        setContentView(homeBinding.root)

        homeBinding.apply {
            val menuItems = arrayOf(
                CbnMenuItem(
                    R.drawable.ic_home,
                    R.drawable.avd_home,
                    R.id.chatHomeFragment
                ),
                CbnMenuItem(
                    R.drawable.ic_settings,
                    R.drawable.avd_settings,
                    R.id.settingsFragment
                ),
            )

            bottomNavView.setMenuItems(menuItems,0)
            val navController = findNavController(R.id.fragmentContainerView2)
            bottomNavView.setupWithNavController(navController)
        }
    }
}