package com.sreshtha.chatappandroid.fragments.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.adapter.ChatHomeRecyclerViewAdapter
import com.sreshtha.chatappandroid.databinding.FragmentChatHomeBinding
import com.sreshtha.chatappandroid.model.Message
import com.sreshtha.chatappandroid.model.Receiver
import com.sreshtha.chatappandroid.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ChatHomeFragment:Fragment() {
    private var chatHomeBinding:FragmentChatHomeBinding?=null
    private val db = Firebase.firestore
    private lateinit var adapter:ChatHomeRecyclerViewAdapter
    private val storage = FirebaseStorage.getInstance(Constants.CLOUD_URL)

    companion object{
        const val TAG = "CHAT_HOME_FRAGMENT"
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatHomeBinding = FragmentChatHomeBinding.inflate(inflater,container,false)
        adapter = ChatHomeRecyclerViewAdapter()
        return chatHomeBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO fetch all the chats which are initiated
        /*db.collection("${Constants.CHAT_REF}/${FirebaseAuth.getInstance().currentUser!!}").get()
            .addOnFailureListener {
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                Log.d(TAG,"fetching all collections :success")
            }
*/

        chatHomeBinding?.apply {
            ivAddFriend.setOnClickListener {
                //create sender side chat
                displayCustomAlert()
            }
        }

        initRecyclerView()


    }


    override fun onDestroy() {
        super.onDestroy()
        chatHomeBinding =null
    }

    private fun displayCustomAlert(){
        val custView = layoutInflater.inflate(R.layout.alert_edit_box,null)
        custView.findViewById<TextInputLayout>(R.id.text_input_layout_nickname).hint = "Enter the email of the friend"
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
            if(!(activity as HomeActivity).viewModel.hasInternetConnection()){
                Snackbar.make(requireView(),"No Internet Connection!", Snackbar.LENGTH_SHORT).show()
                alertDialog.cancel()
                return@setOnClickListener
            }

            //initChat
            initChat(email)
            alertDialog.cancel()

        }

    }

    private fun initChat(email:String){
        val msg = Message("INIT","-1",true)

        // TODO check if user exists or not
        var doesUserExist = false
        db.collection(Constants.USER_REF).document(Constants.USERS_DOC).get()
            .addOnSuccessListener {
                val map = it.data
                map?.forEach {
                    if(email == it.key){
                        doesUserExist = true
                    }
                }

                when {
                    doesUserExist -> {
                        //TODO check if chat exists or not.
                        db.collection("${FirebaseAuth.getInstance().currentUser!!.email}").document(email).get()
                            .addOnFailureListener {
                                createChat(msg,email)
                                Log.d(TAG,"chat created! as a result of failure")
                                Log.d(TAG,it.toString())
                            }
                            .addOnSuccessListener {
                                // chat already exists
                                Log.d(TAG,it.toString())
                                if(it.data != null ){
                                    Log.d(TAG,"chat already exists"+ it.toString())
                                }
                                else{
                                    createChat(msg,email)
                                    Log.d(TAG,"chat created!")
                                }

                            }

                        createReceiver(email)


                    }
                    else -> {
                        view?.let { it1 -> Snackbar.make(it1,"User does not exist!",Snackbar.LENGTH_SHORT).show() }
                        return@addOnSuccessListener
                    }
                }
                //todo toast
                Log.d(TAG,"fetch all users : success")

            }
            .addOnFailureListener {
                //todo toast
                Log.d(TAG,it.toString())
            }



    }


    private fun initRecyclerView(){
        chatHomeBinding?.apply {
            rvChatHome.adapter = adapter
            rvChatHome.layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun createChat(msg:Message,email: String){
        //for sender side
        db.collection((activity as HomeActivity).viewModel.currentUser.email.toString()).document(email).set(msg)
            .addOnFailureListener {
                //todo toast
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG,"add friend:success")
            }

        //for reciever side
        db.collection(email).document((activity as HomeActivity).viewModel.currentUser.email.toString()).set(msg)
            .addOnFailureListener {
                //todo toast
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG,"add friend receiver:success")
            }

    }


    private fun createReceiver(email: String){
        val userRef = storage.reference.child("${SettingsFragment.USER_IMAGE}/${email}/")
        lifecycleScope.launch(Dispatchers.IO){
            userRef.downloadUrl
                .addOnSuccessListener {
                    Log.d(TAG,"image download rv :Success ")
                    val receiver = Receiver(email,email,it)
                   // adapter.differ.currentList.add(receiver)
                    val newList = mutableListOf<Receiver>()
                    adapter.differ.currentList.forEach { newList.add(it) }
                    newList.add(receiver)
                    adapter.differ.submitList(newList)
                }
                .addOnFailureListener {
                    Log.d(TAG,it.toString())
                    val receiver = Receiver(email,email,null)
                    //adapter.differ.currentList.add(receiver)
                    val newList = mutableListOf<Receiver>()
                    adapter.differ.currentList.forEach { newList.add(it) }
                    newList.add(receiver)
                    adapter.differ.submitList(newList)
                }
        }
    }

}