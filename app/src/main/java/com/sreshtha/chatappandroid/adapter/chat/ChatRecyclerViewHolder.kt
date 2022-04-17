package com.sreshtha.chatappandroid.adapter.chat

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.sreshtha.chatappandroid.databinding.ItemReceiverChatBinding
import com.sreshtha.chatappandroid.databinding.ItemSenderChatBinding
import com.sreshtha.chatappandroid.model.ChatRecyclerViewItem

sealed class ChatRecyclerViewHolder(
    binding:ViewBinding
) : RecyclerView.ViewHolder(binding.root) {

     class SenderChatViewHolder(private val  itemSenderChatBinding: ItemSenderChatBinding):ChatRecyclerViewHolder(itemSenderChatBinding){
         fun bind(senderChatItem: ChatRecyclerViewItem){
             // todo
         }
     }

    class ReceiverChatViewHolder(private  val itemReceiverChatBinding: ItemReceiverChatBinding):ChatRecyclerViewHolder(itemReceiverChatBinding){
        fun bind(receiverChatItem:ChatRecyclerViewItem){
            //todo
        }
    }

}