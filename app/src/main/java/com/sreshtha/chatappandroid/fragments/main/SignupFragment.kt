package com.sreshtha.chatappandroid.fragments.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.MainActivity
import com.sreshtha.chatappandroid.databinding.FragmentSignupBinding
import java.util.regex.Pattern

class SignupFragment : Fragment() {
    private var signupBinding: FragmentSignupBinding? = null

    companion object {
        const val TAG = "SIGNUP_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signupBinding = FragmentSignupBinding.inflate(inflater, container, false)
        return signupBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signupBinding?.apply {

            tvGotoLogin.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.goto_loginFragment)
            }

            btnSignup.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()
                if (email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                    Snackbar.make(view, "Empty Fields not allowed!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!isValidEmail(email)) {
                    Snackbar.make(view, "Invalid Email!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (password != confirmPass) {
                    Snackbar.make(view, "Passwords do not match!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (passwordStrength(password) < 2) {
                    Snackbar.make(view, "Password is too weak!", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:Success")
                            (activity as MainActivity).startHomeActivity()
                            (activity as MainActivity).addUserToFireStore(
                                etEmail.text.toString(),
                                etEmail.text.toString()
                            )
                            Snackbar.make(
                                view,
                                "Account Created Successfully!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.d(TAG, "createUserWithEmail:Failure ${it.exception}")
                            Snackbar.make(view, "Cannot Create Account!", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }


            }

            etPassword.addTextChangedListener {
                val strength = passwordStrength(it.toString())
                progressBar.progress = strength
                if (strength < 2 || (it.toString() != signupBinding!!.etConfirmPassword.text.toString() && signupBinding!!.etConfirmPassword.text.isNotEmpty())) {
                    progressBar.progressDrawable.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.parseColor("#FF0000"), BlendModeCompat.SRC_ATOP
                        )
                } else if (strength < 4) {
                    progressBar.progressDrawable.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.parseColor("#Ecb721"), BlendModeCompat.SRC_ATOP
                        )
                } else {
                    progressBar.progressDrawable.colorFilter =
                        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            Color.parseColor("#21ec21"), BlendModeCompat.SRC_ATOP
                        )
                }
            }


            btnLoginGoogle.setOnClickListener {
                (activity as MainActivity).signInGoogle()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        signupBinding = null
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(".+@.+\\.[a-z]+")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun passwordStrength(password: String): Int {
        when {
            password.isEmpty() -> {
                return 0
            }
            password.length < 4 -> {
                return 1
            }
            password.length < 6 -> {
                return 2
            }
            password.length < 8 -> {
                return 3
            }
            password.length < 10 -> {
                return 4
            }
            else -> {
                return 5
            }
        }
    }


}