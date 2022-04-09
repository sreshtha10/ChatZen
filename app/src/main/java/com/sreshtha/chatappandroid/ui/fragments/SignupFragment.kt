package com.sreshtha.chatappandroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.FragmentSignupBinding

class SignupFragment:Fragment(){
    private var signupBinding:FragmentSignupBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signupBinding = FragmentSignupBinding.inflate(inflater,container,false)
        return signupBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signupBinding?.apply {

            tvGotoLogin.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.goto_loginFragment)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        signupBinding =null
    }
}