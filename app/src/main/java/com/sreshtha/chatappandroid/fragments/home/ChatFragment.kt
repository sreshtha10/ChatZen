package com.sreshtha.chatappandroid.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.activities.HomeActivity
import com.sreshtha.chatappandroid.adapter.chat.ChatRecyclerViewAdapter
import com.sreshtha.chatappandroid.databinding.FragmentChatBinding
import com.sreshtha.chatappandroid.model.ChatRecyclerViewItem
import com.sreshtha.chatappandroid.model.Message
import com.sreshtha.chatappandroid.model.Messages
import com.sreshtha.chatappandroid.model.Receiver
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel
import java.util.*


// todo observe when receivers sends

class ChatFragment:Fragment() {
    private  var chatBinding:FragmentChatBinding?=null
    private val chatAdapter = ChatRecyclerViewAdapter()
    private lateinit var mViewModel: HomeViewModel
    private var receiver:Receiver?=null
    private val db = Firebase.firestore
    private var rvList =  mutableListOf<ChatRecyclerViewItem>()


    companion object{
        const val TAG = "CHAT_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatBinding = FragmentChatBinding.inflate(inflater,container,false)
        mViewModel = (activity as HomeActivity).viewModel
        return chatBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showEmptyLabel(true)

        val bottomNav = (activity as HomeActivity).findViewById<np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE


        val backPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                bottomNav.visibility = View.VISIBLE
                findNavController().navigate(R.id.goto_chatHomeFragment)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this,backPressedCallback)

        val bundle = this.arguments
        if(bundle!=null){
            receiver = bundle.getSerializable("RECEIVER") as Receiver

            chatBinding?.apply {
                tvNickname.text = receiver?.nickname
                Glide.with(view).load(receiver?.photoUrl).into(ivProfilePic)
            }
        }

        updateChat()
        initMessages()

        setUpRecyclerView()

        chatBinding?.apply {
            ivBackBtn.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                findNavController().navigate(R.id.goto_chatHomeFragment)
            }

            ivSend.setOnClickListener {
                val text = etTypeHere.text.toString()
                if(text.isEmpty()){
                    Snackbar.make(view,"Can't send an empty message",Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(!mViewModel.hasInternetConnection()){
                    Snackbar.make(view,"No Internet Connection!",Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                sendMessage(text)
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


    private fun sendMessage(text:String){

        if(receiver==null){
            Log.d(TAG,"receiver is null !")
            return
        }
        // to Sender/Receiver
        db.collection(mViewModel.currentUser.email.toString()).document(receiver!!.email).get()
            .addOnFailureListener {
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                Log.d(TAG,"get doc from firestore: success")
                val messages = it.toObject(Messages::class.java)
                if(messages!=null){
                    val newMessageList = mutableListOf<Message>()
                    messages.messages.forEach {
                        newMessageList.add(it)
                    }

                    val newMessage = Message(description = text, timeStamp = mViewModel.getCurrentTime(), currentUserSender = true)
                    newMessageList.add(newMessage)
                    val newMessages = Messages(newMessageList)

                    db.collection(mViewModel.currentUser.email.toString()).document(receiver!!.email).set(newMessages)
                        .addOnSuccessListener {
                            rvList.add(ChatRecyclerViewItem(nickname = mViewModel.currentUser.displayName.toString(), message = newMessage))
                            chatAdapter.differ.submitList(rvList.reversed())
                            chatBinding?.etTypeHere?.text?.clear()
                            showEmptyLabel(false)
                            Log.d(TAG,"setting new message : success")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.toString())
                        }

                }
                else{
                    Log.d(TAG,"messages is null !")
                }
            }


        // to Receiver/Sender
        db.collection(receiver!!.email).document(mViewModel.currentUser.email.toString()).get()
            .addOnFailureListener {
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                Log.d(TAG,"get doc from firestore: success")
                val messages = it.toObject(Messages::class.java)
                if(messages!=null){
                    val newMessageList = mutableListOf<Message>()
                    messages.messages.forEach {
                        newMessageList.add(it)
                    }

                    val newMessage = Message(description = text, timeStamp = mViewModel.getCurrentTime(), currentUserSender = false)
                    newMessageList.add(newMessage)

                    val newMessages = Messages(newMessageList)
                    Log.d(TAG,newMessages.toString())

                    db.collection(receiver!!.email).document(mViewModel.currentUser.email.toString()).set(newMessages)
                        .addOnSuccessListener {
                            Log.d(TAG,"setting new message : success")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,it.toString())
                        }
                }
                else{
                    Log.d(TAG,"messages is null !")
                }
            }

    }


    private fun initMessages(){
        if(receiver==null){
            showEmptyLabel(true)
            Log.d(TAG,"receiver is null!")
            return
        }
        db.collection(mViewModel.currentUser.email.toString()).document(receiver!!.email).get()
            .addOnFailureListener {
                showEmptyLabel(true)
                Log.d(TAG,it.toString())
            }
            .addOnSuccessListener {
                //val gson = GsonBuilder().create()
                //val messages = gson.fromJson(it.toString(),Messages::class.java)
                val messages = it.toObject(Messages::class.java)
                Log.d(TAG,it.toString())
                if(messages==null || messages.messages.size<=1){
                    showEmptyLabel(true)
                }
                else{
                    val newList = mutableListOf<ChatRecyclerViewItem>()
                    showEmptyLabel(false)
                    messages.messages.forEach {
                        Log.d(TAG,it.currentUserSender.toString())
                        if(it.timeStamp=="-1"){
                            //nothing todo
                        }
                        else if(it.currentUserSender){
                            newList.add(ChatRecyclerViewItem(mViewModel.currentUser.displayName.toString(), it))
                        }
                        else{
                            newList.add(ChatRecyclerViewItem(receiver!!.nickname, it))
                        }
                    }

                    if(newList.size == rvList.size){
                        return@addOnSuccessListener
                    }
                    else{
                        rvList = newList
                    }
                    chatAdapter.differ.submitList(rvList.reversed())

                    Log.d(TAG,chatAdapter.differ.currentList[chatAdapter.differ.currentList.size-1].message.currentUserSender.toString())
                }
            }
    }

    private fun showEmptyLabel(boolean: Boolean){
        when(boolean){
            true -> chatBinding?.llLabelEmptyRv?.visibility = View.VISIBLE
            false -> chatBinding?.llLabelEmptyRv?.visibility = View.GONE
        }
    }

    private fun updateChat(){
        val timer = Timer()

        val task = object: TimerTask(){
            override fun run() {
                initMessages()
            }
        }
        timer.schedule(task,0,100)
    }

}