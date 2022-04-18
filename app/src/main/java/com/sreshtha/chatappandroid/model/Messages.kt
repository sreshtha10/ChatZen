package com.sreshtha.chatappandroid.model

import java.io.Serializable

data class Messages(
    val messages: List<Message> = listOf()
): Serializable