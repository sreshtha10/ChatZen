package com.sreshtha.chatappandroid.model

import java.io.Serializable


data class Message(
    val description: String = "",
    val timeStamp: String = "",
    val currentUserSender: Boolean = false
):Serializable