package com.sreshtha.chatappandroid.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ActivityMainBinding
import com.sreshtha.chatappandroid.ui.fragments.LoginFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding :ActivityMainBinding
    var signInClient:GoogleSignInClient?=null
    lateinit var auth: FirebaseAuth

    companion object{
        const val TAG="MAIN_ACTIVITY"
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(mainActivityBinding.root)
        initGoogleClient()

    }

    fun startHomeActivity(){
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun initGoogleClient(){
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this,gso)
    }

    fun signInGoogle(){
        val intent = signInClient?.signInIntent
        resultLauncher.launch(intent)
    }





    private fun firebaseAuthWithGoogle(acc : GoogleSignInAccount){
        Log.d(MainActivity.TAG,"firebaseWithGoogle Called")
        val creds = GoogleAuthProvider.getCredential(acc.idToken,null)
        FirebaseAuth.getInstance().signInWithCredential(creds)
            .addOnSuccessListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                this.finish()
                Log.d(LoginFragment.TAG,"google sign in:success")
            }

            .addOnFailureListener {
                Log.d(LoginFragment.TAG,it.toString())
            }
    }




}