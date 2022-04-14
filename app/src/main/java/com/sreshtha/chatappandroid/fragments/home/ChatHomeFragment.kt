package com.sreshtha.chatappandroid.fragments.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.databinding.FragmentChatHomeBinding
import com.sreshtha.chatappandroid.model.Message

/*
Schema for chat app:
1. Sender side - user1/receiver/messages
2. Receiver side - user2/sender/messages

//todo  -> check if chat already exists for init chat
//todo -> check if user exists or not.
 */





class ChatHomeFragment:Fragment() {
    private var chatHomeBinding:FragmentChatHomeBinding?=null
    private val db = Firebase.firestore

    companion object{
        const val TAG = "CHAT_HOME_FRAGMENT"
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatHomeBinding = FragmentChatHomeBinding.inflate(inflater,container,false)
        return chatHomeBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatHomeBinding?.apply {
            ivAddFriend.setOnClickListener {
                //create sender side chat
                displayCustomAlert()
            }
        }

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
        db.collection("chat/${(activity as HomeActivity).viewModel.currentUser.email.toString()}/${email}")
            .add(msg)
            .addOnFailureListener {
                //todo toast
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG,"add friend:success")
            }
    }


    private fun initRecyclerView(){

    }
}