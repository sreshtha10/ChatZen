package com.sreshtha.chatappandroid.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ActivityMainBinding
import com.sreshtha.chatappandroid.fragments.main.LoginFragment
import com.sreshtha.chatappandroid.fragments.main.SignupFragment
import com.sreshtha.chatappandroid.util.Constants
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding: ActivityMainBinding
    var signInClient: GoogleSignInClient? = null
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    companion object {
        const val TAG = "MAIN_ACTIVITY"
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d(MainActivity.TAG, "Result OK")
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.result
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    Log.d(TAG, e.toString())
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startHomeActivity()
        }
        setTheme(R.style.Theme_ChatAppAndroid)
        setContentView(mainActivityBinding.root)
        initGoogleClient()


    }

    fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initGoogleClient() {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, gso)
    }

    fun signInGoogle() {
        val intent = signInClient?.signInIntent
        resultLauncher.launch(intent)
    }


    private fun firebaseAuthWithGoogle(acc: GoogleSignInAccount) {
        Log.d(MainActivity.TAG, "firebaseWithGoogle Called")
        val creds = GoogleAuthProvider.getCredential(acc.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(creds)
            .addOnSuccessListener {

                lifecycleScope.launch {
                    addUserToFireStore(acc.email.toString(), acc.displayName.toString())
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    this@MainActivity.finish()
                }

                Log.d(LoginFragment.TAG, "google sign in:success")
            }

            .addOnFailureListener {
                Log.d(LoginFragment.TAG, it.toString())
            }
    }

    fun addUserToFireStore(email: String, name: String) {
        db.collection(Constants.USER_REF).document(Constants.USERS_DOC).set(
            mapOf(email to name),
            SetOptions.merge()
        )
            .addOnFailureListener {
                //todo toast
                Log.d(SignupFragment.TAG, it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG, "user added to firestore:success")
            }

    }



}