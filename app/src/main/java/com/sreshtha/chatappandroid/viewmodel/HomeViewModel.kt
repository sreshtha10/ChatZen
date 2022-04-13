package com.sreshtha.chatappandroid.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import com.sreshtha.chatappandroid.ChatAppAndroid
import com.sreshtha.chatappandroid.model.Message

class HomeViewModel(
    val app:Application,
    val user:FirebaseUser
):AndroidViewModel(app) {


    val currentUser = this.user

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ChatAppAndroid>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> return false
        }
    }


    fun getMessageHash(message: Message):Map<String,String>{
        return mapOf<String,String>(
            "description" to message.description,
            "timeStamp" to message.timeStamp,
            "isCurrentUserSender" to message.isCurrentUserSender.toString()
        )
    }

}