package com.sreshtha.chatappandroid.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sreshtha.chatappandroid.databinding.FragmentChatHomeBinding

/*
Schema for chat app:
1. Sender side - user1/receiver/messages
2. Receiver side - user2/sender/messages
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
    }


    override fun onDestroy() {
        super.onDestroy()
        chatHomeBinding =null
    }

}