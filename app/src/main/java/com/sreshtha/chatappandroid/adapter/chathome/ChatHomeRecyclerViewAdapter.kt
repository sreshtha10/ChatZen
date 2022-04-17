package com.sreshtha.chatappandroid.adapter.chathome

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ItemChatHomeRvBinding
import com.sreshtha.chatappandroid.model.Receiver
import java.lang.Exception

class ChatHomeRecyclerViewAdapter :
    RecyclerView.Adapter<ChatHomeRecyclerViewAdapter.ChatHomeViewHolder>() {

    companion object {
        const val TAG = "CHAT_HOME_RV"
    }

    inner class ChatHomeViewHolder(val binding: ItemChatHomeRvBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Receiver>() {
        override fun areItemsTheSame(oldItem: Receiver, newItem: Receiver): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Receiver, newItem: Receiver): Boolean {
            return oldItem.equals(newItem)
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHomeViewHolder {
        val holder = ItemChatHomeRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ChatHomeViewHolder(holder)
    }

    override fun onBindViewHolder(holder: ChatHomeViewHolder, position: Int) {
        holder.binding.apply {
            Log.d(TAG, differ.currentList[position].photoUrl.toString())
            Glide.with(holder.binding.root)
                .load(differ.currentList[position].photoUrl)
                .placeholder(R.drawable.ic_dummy_profile)
                .override(100, 100)
                .into(ivChatUser)
            tvChatEmail.text = differ.currentList[position].email
            tvNickname.text = differ.currentList[position].nickname


            root.setOnClickListener {
                try {
                    onItemClickListener?.let { it(differ.currentList[position]) }
                }
                catch (e:Exception){
                    Log.d(TAG,"Cannot set listener ${e.toString()}")
                }
            }
        }



    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener:((Receiver) -> Unit)?=null

    fun setOnClickListener(listener : (Receiver)->Unit){
        onItemClickListener = listener
    }

}