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
             itemSenderChatBinding.apply {
                 tvMessage.text = senderChatItem.message.description
                 tvNickname.text = "You"
                 tvTimeStamp.text = senderChatItem.message.timeStamp
             }
         }
     }

    class ReceiverChatViewHolder(private  val itemReceiverChatBinding: ItemReceiverChatBinding):ChatRecyclerViewHolder(itemReceiverChatBinding){
        fun bind(receiverChatItem:ChatRecyclerViewItem){
            itemReceiverChatBinding.apply {
                tvMessage.text = receiverChatItem.message.description
                tvNickname.text = receiverChatItem.nickname
                tvTimeStamp.text = receiverChatItem.message.timeStamp
            }
        }
    }

}