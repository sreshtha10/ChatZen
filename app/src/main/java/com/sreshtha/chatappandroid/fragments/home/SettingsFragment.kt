package com.sreshtha.chatappandroid.fragments.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.activities.MainActivity
import com.sreshtha.chatappandroid.databinding.FragmentSettingsBinding
import com.sreshtha.chatappandroid.util.Constants
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var settingsBinding: FragmentSettingsBinding? = null
    private val mViewModel: HomeViewModel by activityViewModels()
    private val storage = FirebaseStorage.getInstance(Constants.CLOUD_URL)
    private val db = Firebase.firestore


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == true) {
                //launch gallery intent
                openGallery()
            }
        }

    private val readExternalLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                //add image to image view
                settingsBinding?.profileImage?.let { it1 ->
                    Glide.with(this).asDrawable().load(it).into(it1)
                }
                // todo add image to cloud
                settingsBinding?.dotLoader?.visibility = View.VISIBLE
                val bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        it
                    )
                )
                uploadToCloud(bitmap)
                Log.d(TAG, it.toString())
            }
        }


    companion object {
        const val TAG = "SETTINGS_FRAGMENT"
        const val USER_SETTINGS = "user_settings"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return settingsBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues(view)

        settingsBinding?.apply {

            llLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity as HomeActivity, MainActivity::class.java)
                startActivity(intent)
                (activity)?.finish()
            }


            llChangeProfilePic.setOnClickListener {
                if (!mViewModel.hasInternetConnection()) {
                    //todo toast
                    return@setOnClickListener
                }
                if (hasReadExternalStoragePermission(activity as HomeActivity)) {
                    //openGallery
                    openGallery()


                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }


            llChangeNickname.setOnClickListener {
                displayCustomAlert()
            }


            ivGithub.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GITHUB_URL))
                startActivity(intent)
            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        settingsBinding = null
    }


    private fun hasReadExternalStoragePermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun openGallery() {
        readExternalLauncher.launch("image/*")

    }

    private fun uploadToCloud(bitmap: Bitmap) {
        if (!mViewModel.hasInternetConnection()) {
            //todo toast
            return
        }
        val userRef = storage.reference.child("${Constants.USER_IMAGE}/${mViewModel.currentUser.email}/")

        //val bitmap  = (settingsBinding?.profileImage?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        lifecycleScope.launch(Dispatchers.IO) {
            val uploadTask = userRef.putBytes(data)
            uploadTask.addOnFailureListener {
                settingsBinding?.dotLoader?.visibility = View.GONE
                Log.d(TAG, it.toString())
            }
            uploadTask.addOnSuccessListener {
                getUrlFromCloud()
                settingsBinding?.dotLoader?.visibility = View.GONE
                Log.d(TAG, "file upload:success")
            }
        }
    }


    private fun getUrlFromCloud() {
        val userRef = storage.reference.child("${Constants.USER_IMAGE}/${mViewModel.currentUser.email}/")
        lifecycleScope.launch(Dispatchers.IO) {
            userRef.downloadUrl
                .addOnSuccessListener {
                    val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                    mViewModel.currentUser.updateProfile(profileUpdates)
                    Toast.makeText(activity, "Profile Picture Updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Cannot Update Profile Picture!", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, it.toString())
                }
        }
    }

    private fun displayCustomAlert() {
        val custView = layoutInflater.inflate(R.layout.alert_edit_box, null)
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
            val nickname =
                custView.findViewById<TextInputEditText>(R.id.et_nickname).text.toString()
            //update nickname
            if (!mViewModel.hasInternetConnection()) {
                Snackbar.make(requireView(), "No Internet Connection!", Snackbar.LENGTH_SHORT)
                    .show()
                alertDialog.cancel()
                return@setOnClickListener
            }
            settingsBinding?.dotLoader?.visibility = View.VISIBLE
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
            mViewModel.currentUser.updateProfile(profileUpdates)
            changeNicknameOnFireStore(nickname)
            settingsBinding?.dotLoader?.visibility = View.GONE
            settingsBinding?.tvUserName?.text = nickname
            alertDialog.cancel()

        }

    }


    private fun initValues(view: View) {
        settingsBinding?.apply {
            val nickname = mViewModel.currentUser.email?.split("@")?.get(0)
            lifecycleScope.launch {

                if (mViewModel.currentUser.displayName.isNullOrEmpty()) {
                    val profileUpdates =
                        UserProfileChangeRequest.Builder().setDisplayName(nickname).build()
                    mViewModel.currentUser.updateProfile(profileUpdates)
                }

                tvUserName.text = mViewModel.currentUser.displayName
            }
            tvUserEmail.text = mViewModel.currentUser.email

            Glide.with(view).load(mViewModel.currentUser.photoUrl).into(profileImage)
        }
    }


    private fun changeNicknameOnFireStore(nickname:String){
        db.collection(Constants.NICKNAME_REF).document(Constants.DOC_NICKNAME_UID).get()
            .addOnFailureListener {
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                if(it.data==null){
                    //create new document
                    db.collection(Constants.NICKNAME_REF).document(Constants.DOC_NICKNAME_UID).set(
                        mapOf(mViewModel.currentUser.email to nickname)
                    )
                        .addOnSuccessListener {
                            Log.d(TAG,"add nickname to firestore when no doc exist: success")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.toString())
                        }
                }
                else{
                    val map = it.data
                    map?.set(mViewModel.currentUser.email.toString(), nickname)
                    if (map != null) {
                        db.collection(Constants.NICKNAME_REF).document(Constants.DOC_NICKNAME_UID).set(map)
                            .addOnFailureListener {
                                Log.d(TAG,it.toString())
                            }
                            .addOnSuccessListener {
                                Log.d(TAG,"add nickname to firestore when doc exists : success")
                            }
                    }
                }
            }
    }



}