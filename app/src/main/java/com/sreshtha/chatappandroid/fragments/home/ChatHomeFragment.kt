package com.sreshtha.chatappandroid.fragments.home

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
import com.sreshtha.chatappandroid.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO store nickname somehow
// TODO create touch on item of rv chat home
// TODO fragment & layout for chat
// TODO design rv for chat (UI+Adapter)
// TODO maintain send and delete
// TODO add delete user functionality

@AndroidEntryPoint
class ChatHomeFragment : Fragment() {
    private var chatHomeBinding: FragmentChatHomeBinding? = null
    private val db = Firebase.firestore
    private lateinit var adapter: ChatHomeRecyclerViewAdapter
    private val storage = FirebaseStorage.getInstance(Constants.CLOUD_URL)

    private val mViewModel: HomeViewModel by activityViewModels()
    private val imageDownloadLiveData = MutableLiveData<Receiver>()
    private var rvList = mutableListOf<Receiver>()

    companion object {
        const val TAG = "CHAT_HOME_FRAGMENT"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatHomeBinding = FragmentChatHomeBinding.inflate(inflater, container, false)
        adapter = ChatHomeRecyclerViewAdapter()
        //waitForViewModel()
        return chatHomeBinding?.root
    }

    /*private fun waitForViewModel() {
        Handler(Looper.getMainLooper()).postDelayed({
            if ((activity as HomeActivity).viewModel != null) {
                mViewModel = ((activity as HomeActivity).viewModel)!!
                initRecyclerViewData()
                initRecyclerView()
            }
            else {
                waitForViewModel()
            }
        }, Long.MIN_VALUE)
    }*/


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewData()
        initRecyclerView()

        imageDownloadLiveData.observe(viewLifecycleOwner) { receiver ->
            rvList.forEach {
                if(it.email == receiver.email){
                    adapter.differ.submitList(rvList)
                    chatHomeBinding?.dotLoader?.visibility = View.INVISIBLE
                    return@observe
                }
            }
            rvList.add(receiver)
            adapter.differ.submitList(rvList)
            chatHomeBinding?.dotLoader?.visibility = View.GONE

            Log.d(TAG, "fetching all collections :success")
        }

        chatHomeBinding?.apply {
            ivAddFriend.setOnClickListener {
                //create sender side chat
                displayCustomAlert()
            }
        }



    }


    override fun onDestroy() {
        super.onDestroy()
        chatHomeBinding = null
    }

    private fun displayCustomAlert() {
        val custView = layoutInflater.inflate(R.layout.alert_edit_box, null)
        custView.findViewById<TextInputLayout>(R.id.text_input_layout_nickname).hint =
            "Enter the email of the friend"
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
            if (!mViewModel.hasInternetConnection()) {
                Snackbar.make(requireView(), "No Internet Connection!", Snackbar.LENGTH_SHORT)
                    .show()
                alertDialog.cancel()
                return@setOnClickListener
            }

            //initChat
            initChat(email)
            alertDialog.cancel()

        }

    }

    private fun initChat(email: String) {
        val msg = Message("INIT", "-1", true)

        // TODO check if user exists or not
        var doesUserExist = false
        db.collection(Constants.USER_REF).document(Constants.USERS_DOC).get()
            .addOnSuccessListener {
                val map = it.data
                map?.forEach {
                    if (email == it.key) {
                        doesUserExist = true
                    }
                }

                when {
                    doesUserExist -> {
                        //TODO check if chat exists or not.
                        db.collection("${mViewModel.currentUser.email}")
                            .document(email).get()
                            .addOnFailureListener {
                                createChat(msg, email)
                                Log.d(TAG, "chat created! as a result of failure")
                                Log.d(TAG, it.toString())
                            }
                            .addOnSuccessListener {
                                // chat already exists
                                Log.d(TAG, it.toString())
                                if (it.data != null) {
                                    Log.d(TAG, "chat already exists" + it.toString())
                                } else {
                                    createChat(msg, email)
                                    Log.d(TAG, "chat created!")
                                }

                            }

                        createReceiver(email)


                    }
                    else -> {
                        view?.let { it1 ->
                            Snackbar.make(
                                it1,
                                "User does not exist!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        return@addOnSuccessListener
                    }
                }
                //todo toast
                Log.d(TAG, "fetch all users : success")

            }
            .addOnFailureListener {
                //todo toast
                Log.d(TAG, it.toString())
            }


    }


    private fun initRecyclerView() {
        chatHomeBinding?.apply {
            rvChatHome.adapter = adapter
            rvChatHome.layoutManager = LinearLayoutManager(activity)
        }
    }


    private fun createChat(msg: Message, email: String) {
        //for sender side
        db.collection(mViewModel.currentUser.email.toString())
            .document(email).set(msg)
            .addOnFailureListener {
                //todo toast
                Log.d(TAG, it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG, "add friend:success")
            }

        //for reciever side
        db.collection(email)
            .document(mViewModel.currentUser.email.toString()).set(msg)
            .addOnFailureListener {
                //todo toast
                Log.d(TAG, it.toString())
            }
            .addOnSuccessListener {
                //todo toast
                Log.d(TAG, "add friend receiver:success")
            }

    }


    private fun createReceiver(email: String) {
        val userRef = storage.reference.child("${SettingsFragment.USER_IMAGE}/${email}/")
        lifecycleScope.launch(Dispatchers.IO) {
            userRef.downloadUrl
                .addOnSuccessListener {
                    Log.d(TAG, "image download rv :Success ")
                    val receiver = Receiver(email, email, it)
                    val newList = mutableListOf<Receiver>()
                    adapter.differ.currentList.forEach { newList.add(it) }
                    newList.add(receiver)
                    adapter.differ.submitList(newList)
                }
                .addOnFailureListener {
                    Log.d(TAG, it.toString())
                    val receiver = Receiver(email, email, null)
                    val newList = mutableListOf<Receiver>()
                    adapter.differ.currentList.forEach { newList.add(it) }
                    newList.add(receiver)
                    adapter.differ.submitList(newList)
                }
        }
    }


    private suspend fun getReceiverForEmail(email: String): Receiver {
        return withContext(Dispatchers.IO) {
            val receiver = Receiver(email, email, null)
            val userRef = storage.reference.child("${SettingsFragment.USER_IMAGE}/${email}/")
            userRef.downloadUrl
                .addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
                .addOnSuccessListener {
                    receiver.photoUrl = it
                    Log.d(TAG, it.toString())
                }
            receiver
        }

    }


    private fun initRecyclerViewData() {
        chatHomeBinding?.dotLoader?.visibility = View.VISIBLE
        db.collection(mViewModel.currentUser.email.toString()).get()
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
            .addOnSuccessListener {
                val docs = it.documents
                if (docs.isEmpty()) {
                    return@addOnSuccessListener
                }
                docs.forEach {
                    Log.d(TAG, it.id)
                    val email = it.id
                    val userRef =
                        storage.reference.child("${SettingsFragment.USER_IMAGE}/${it.id}/")
                    userRef.downloadUrl
                        .addOnFailureListener {
                            Log.d(TAG, it.toString())
                            imageDownloadLiveData.value = Receiver(email, email, null)
                        }
                        .addOnSuccessListener {
                            imageDownloadLiveData.value = Receiver(email, email, it)
                            Log.d(TAG,it.toString())
                        }
                }
            }
    }


}