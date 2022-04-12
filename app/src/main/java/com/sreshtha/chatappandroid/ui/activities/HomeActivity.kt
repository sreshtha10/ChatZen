package com.sreshtha.chatappandroid.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ActivityHomeBinding
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
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