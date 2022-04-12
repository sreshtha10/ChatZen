package com.sreshtha.chatappandroid.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sreshtha.chatappandroid.databinding.FragmentChatHomeBinding

class ChatHomeFragment:Fragment() {
    private var chatHomeBinding:FragmentChatHomeBinding?=null


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