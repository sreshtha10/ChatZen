package com.sreshtha.chatappandroid.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.FragmentLoginBinding
import com.sreshtha.chatappandroid.ui.activities.MainActivity

class LoginFragment:Fragment(){
    private var loginBinding: FragmentLoginBinding?=null

    companion object{
        const val TAG = "LOGIN_FRAGMENT"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginBinding = FragmentLoginBinding.inflate(inflater,container,false)
        return loginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBinding?.apply {

            tvGotoSignup.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.goto_signupFragment)
            }

            btnLogin.setOnClickListener {
                if(etEmail.text.isEmpty()|| etPassword.text.isEmpty()){
                    Snackbar.make(view,"Empty Fields Not Allowed!",Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(etEmail.text.toString(),etPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(activity,"Success!",Toast.LENGTH_SHORT).show()
                            (activity as MainActivity).startHomeActivity()
                        } else {
                            Log.d(TAG, "signInWithEmail:failure")
                            Toast.makeText(activity,"Login Failed!",Toast.LENGTH_SHORT).show()
                        }
                    }
            }



        }

    }

    override fun onDestroy() {
        super.onDestroy()
        loginBinding = null
    }



}