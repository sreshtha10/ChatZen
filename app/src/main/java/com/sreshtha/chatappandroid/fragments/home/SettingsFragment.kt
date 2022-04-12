package com.sreshtha.chatappandroid.fragments.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.sreshtha.chatappandroid.databinding.FragmentSettingsBinding
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.activities.MainActivity
import com.sreshtha.chatappandroid.util.Constants
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel

class SettingsFragment:Fragment() {
    private var settingsBinding:FragmentSettingsBinding?=null
    private lateinit var mViewModel:HomeViewModel
    private val storage = FirebaseStorage.getInstance(Constants.CLOUD_URL)





    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it==true){
            //launch gallery intent
            openGallery()
        }
    }

    private val readExternalLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        if(it!=null){
            //upload image
            Log.d(TAG,it.toString())
        }
    }




    companion object{
        const val TAG = "SETTINGS_FRAGMENT"
        const val USER_SETTINGS="user_settings"
        const val USER_IMAGE="user_image"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsBinding= FragmentSettingsBinding.inflate(inflater,container,false)
        mViewModel = (activity as HomeActivity).viewModel
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


            Glide.with(view).load(mViewModel.currentUser.photoUrl).into(profileImage)

            llChangeProfilePic.setOnClickListener {
                if(!mViewModel.hasInternetConnection()){
                    //todo toast
                    return@setOnClickListener
                }
                if(hasReadExternalStoragePermission(activity as HomeActivity)){
                    //openGallery
                    openGallery()

                }
                else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        settingsBinding = null
    }


    private fun setupStorage(){
        val storageRef = storage.reference
    }

    private fun hasReadExternalStoragePermission(context: Context):Boolean{
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }


    private fun openGallery(){
        readExternalLauncher.launch("image/*")

    }
}