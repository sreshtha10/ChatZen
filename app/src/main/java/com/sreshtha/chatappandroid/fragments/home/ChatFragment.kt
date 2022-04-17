package com.sreshtha.chatappandroid.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.adapter.chat.ChatRecyclerViewAdapter
import com.sreshtha.chatappandroid.databinding.FragmentChatBinding
import com.sreshtha.chatappandroid.model.ChatRecyclerViewItem
import com.sreshtha.chatappandroid.model.Message
import com.sreshtha.chatappandroid.model.Receiver

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

        val bottomNav = (activity as HomeActivity).findViewById<np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE

        val bundle = this.arguments
        if(bundle!=null){
            val receiver = bundle.getSerializable("RECEIVER") as Receiver

            chatBinding?.apply {
                tvNickname.text = receiver.nickname
                Glide.with(view).load(receiver.photoUrl).into(ivProfilePic)
            }
        }

        setUpRecyclerView()

        chatAdapter.differ.submitList(
            listOf(
                ChatRecyclerViewItem(nickname = "ABC", message = Message("hello","ds",true)),
                ChatRecyclerViewItem(nickname = "RECEIVER", message = Message("hello","ds",false))
            ).reversed()
        )

        chatBinding?.apply {
            ivBackBtn.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
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
            rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
                this.reverseLayout = true
            }
        }
    }

}