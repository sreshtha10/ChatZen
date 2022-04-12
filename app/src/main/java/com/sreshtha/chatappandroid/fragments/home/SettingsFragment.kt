package com.sreshtha.chatappandroid.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.sreshtha.chatappandroid.databinding.FragmentSettingsBinding
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.activities.MainActivity

class SettingsFragment:Fragment() {
    private var settingsBinding:FragmentSettingsBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsBinding= FragmentSettingsBinding.inflate(inflater,container,false)
        return settingsBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsBinding?.apply {

            llLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity as HomeActivity,MainActivity::class.java)
                startActivity(intent)
                (activity)?.finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        settingsBinding = null
    }

}