package com.sreshtha.chatappandroid.fragments.main

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.MainActivity
import com.sreshtha.chatappandroid.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var loginBinding: FragmentLoginBinding? = null

    companion object {
        const val TAG = "LOGIN_FRAGMENT"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        return loginBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginBinding?.apply {

            tvGotoSignup.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.goto_signupFragment)
            }

            btnLogin.setOnClickListener {

                if (etEmail.text.isEmpty() || etPassword.text.isEmpty()) {
                    Snackbar.make(view, "Empty Fields Not Allowed!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
                            (activity as MainActivity).startHomeActivity()
                        } else {
                            Log.d(TAG, "signInWithEmail:failure")
                            Toast.makeText(activity, "Login Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            btnLoginGoogle.setOnClickListener {
                (activity as MainActivity).signInGoogle()
                Log.d(TAG, "login to google clicked!")
            }

            tvResetPassword.setOnClickListener {
                resetPassword()
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        loginBinding = null
    }


    private fun resetPassword(){
        val custView = layoutInflater.inflate(R.layout.alert_edit_box, null)
        custView.findViewById<TextInputLayout>(R.id.text_input_layout_nickname).hint =
            "Enter your email address"
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setCancelable(false)

        alertDialog.setView(custView)
        alertDialog.show()


        val btnCancel = custView.findViewById<Button>(R.id.btn_cancel)
        val btnOk = custView.findViewById<Button>(R.id.btn_ok)

        btnCancel.setOnClickListener {
            alertDialog.cancel()
        }

        btnOk.setOnClickListener {
            val email = custView.findViewById<TextInputEditText>(R.id.et_nickname).text.toString()
            if(email.isEmpty()){
                Toast.makeText(activity, "Invalid Email!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Email Sent!",Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                        Log.d(TAG, "Email sent.")
                    }
                    else{
                        Toast.makeText(activity, "Email not sent!",Toast.LENGTH_SHORT).show()
                        alertDialog.cancel()
                    }
                }
        }
    }


}