package com.sreshtha.chatappandroid.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.adapter.chat.ChatRecyclerViewAdapter
import com.sreshtha.chatappandroid.databinding.FragmentChatBinding
import com.sreshtha.chatappandroid.model.ChatRecyclerViewItem
import com.sreshtha.chatappandroid.model.Message

class ChatFragment:Fragment() {
    private  var chatBinding:FragmentChatBinding?=null
    private val chatAdapter = ChatRecyclerViewAdapter()

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


        setUpRecyclerView()

        chatAdapter.differ.submitList(
            listOf(
                ChatRecyclerViewItem(nickname = "ABC", message = Message("hello","ds",true)),
                ChatRecyclerViewItem(nickname = "RECEIVER", message = Message("hello","ds",false))
            )
        )

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

    private fun setUpRecyclerView(){
        chatBinding?.apply {
            rvChat.adapter = chatAdapter
            rvChat.layoutManager = LinearLayoutManager(requireContext())
        }
    }

}