package com.sreshtha.chatappandroid.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.FragmentLoginBinding
import com.sreshtha.chatappandroid.ui.activities.HomeActivity
import com.sreshtha.chatappandroid.ui.activities.MainActivity

class LoginFragment:Fragment(){

    private var loginBinding: FragmentLoginBinding?=null
    private var signInClient:GoogleSignInClient?=null

    companion object{
        const val TAG = "LOGIN_FRAGMENT"
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            Log.d(MainActivity.TAG,"Result OK")
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.result
                firebaseAuthWithGoogle(account)
            }
            catch (e: ApiException){
                Log.d(MainActivity.TAG,e.toString())
            }
        }
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

        initGoogleClient()
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

            btnLoginGoogle.setOnClickListener {
                signInGoogle(signInClient!!)
                Log.d(TAG,"login to google clicked!")
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        loginBinding = null
    }

    private fun initGoogleClient(){
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(activity as MainActivity,gso)
    }

    fun signInGoogle(signInClient: GoogleSignInClient){
        val intent = signInClient.signInIntent
        resultLauncher.launch(intent)
    }





    private fun firebaseAuthWithGoogle(acc : GoogleSignInAccount){
        Log.d(MainActivity.TAG,"firebaseWithGoogle Called")
        val creds = GoogleAuthProvider.getCredential(acc.idToken,null)
        FirebaseAuth.getInstance().signInWithCredential(creds)
            .addOnSuccessListener {
                val intent = Intent(activity as MainActivity, HomeActivity::class.java)
                startActivity(intent)
                (activity as MainActivity).finish()
                Log.d(TAG,"google sign in:success")
            }

            .addOnFailureListener {
                Log.d(TAG,it.toString())
            }
    }





}