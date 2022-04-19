package com.sreshtha.chatappandroid.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sreshtha.chatappandroid.R
import com.sreshtha.chatappandroid.databinding.ItemReceiverChatBinding
import com.sreshtha.chatappandroid.databinding.ItemSenderChatBinding
import com.sreshtha.chatappandroid.model.ChatRecyclerViewItem
import java.lang.IllegalArgumentException

class ChatRecyclerViewAdapter : RecyclerView.Adapter<ChatRecyclerViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<ChatRecyclerViewItem>() {
        override fun areItemsTheSame(oldItem: ChatRecyclerViewItem, newItem: ChatRecyclerViewItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatRecyclerViewItem, newItem: ChatRecyclerViewItem): Boolean {
            return oldItem.equals(newItem)
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRecyclerViewHolder {
        return when(viewType){
            R.layout.item_sender_chat -> {
                ChatRecyclerViewHolder.SenderChatViewHolder(
                            ItemSenderChatBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                        false
                    )
                )
            }

            R.layout.item_receiver_chat ->{
                ChatRecyclerViewHolder.ReceiverChatViewHolder(
                    ItemReceiverChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else  ->{
                throw IllegalArgumentException("Invalid view")
            }
        }
    }

    override fun onBindViewHolder(holder: ChatRecyclerViewHolder, position: Int) {
        return when(holder){
            is ChatRecyclerViewHolder.ReceiverChatViewHolder -> holder.bind(differ.currentList[position])
            is ChatRecyclerViewHolder.SenderChatViewHolder -> holder.bind(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun getItemViewType(position: Int): Int {
        return when(differ.currentList[position].message.currentUserSender){
            true -> R.layout.item_sender_chat
            false -> R.layout.item_receiver_chat
        }
    }


}