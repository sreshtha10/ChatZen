package com.sreshtha.chatappandroid.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sreshtha.chatappandroid.databinding.FragmentSettingsBinding

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
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsBinding = null
    }
}