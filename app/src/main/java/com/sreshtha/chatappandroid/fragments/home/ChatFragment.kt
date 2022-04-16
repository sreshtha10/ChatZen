package com.sreshtha.chatappandroid.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.FragmentChatBinding

class ChatFragment:Fragment() {
    private  var chatBinding:FragmentChatBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = FragmentChatBinding.inflate(inflater,container,false)
        return chatBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatBinding?.apply {
            ivBackBtn.setOnClickListener {
                findNavController().navigate(R.id.goto_chatHomeFragment)
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        chatBinding = null
    }

}