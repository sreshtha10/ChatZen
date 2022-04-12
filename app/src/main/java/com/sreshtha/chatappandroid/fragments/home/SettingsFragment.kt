package com.sreshtha.chatappandroid.fragments.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.activities.MainActivity
import com.sreshtha.chatappandroid.databinding.FragmentSettingsBinding
import com.sreshtha.chatappandroid.util.Constants
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

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
            //add image to image view
            settingsBinding?.profileImage?.let { it1 -> Glide.with(this).asDrawable().load(it).into(it1) }
            // todo add image to cloud
            settingsBinding?.dotLoader?.visibility = View.VISIBLE
            val bitmap =  ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, it))
            uploadToCloud(bitmap)
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

    private fun uploadToCloud(bitmap:Bitmap){
        if(!mViewModel.hasInternetConnection()){
            //todo toast
            return
        }
        val userRef = storage.reference.child("$USER_IMAGE/${mViewModel.currentUser.email}/")

        //val bitmap  = (settingsBinding?.profileImage?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()

        lifecycleScope.launch(Dispatchers.IO){
            val uploadTask = userRef.putBytes(data)
            uploadTask.addOnFailureListener{
                settingsBinding?.dotLoader?.visibility = View.GONE
                Log.d(TAG,it.toString())
            }
            uploadTask.addOnSuccessListener {
                getUrlFromCloud()
                settingsBinding?.dotLoader?.visibility = View.GONE
                Log.d(TAG,"file upload:success")
            }
        }
    }


    private fun getUrlFromCloud(){
        val userRef = storage.reference.child("$USER_IMAGE/${mViewModel.currentUser.email}/")
        lifecycleScope.launch(Dispatchers.IO){
            userRef.downloadUrl
                .addOnSuccessListener {
                    val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                    mViewModel.currentUser.updateProfile(profileUpdates)
                    Toast.makeText(activity,"Profile Picture Updated!",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity,"Cannot Update Profile Picture!",Toast.LENGTH_SHORT).show()
                    Log.d(TAG,it.toString())
                }
        }
    }
}