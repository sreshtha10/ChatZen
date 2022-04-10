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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(mainActivityBinding.root)

    }

    fun startHomeActivity(){
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }




}