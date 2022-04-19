package com.sreshtha.chatappandroid.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import com.sreshtha.chatappandroid.ChatAppAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val app: Application,
    val user: FirebaseUser
) : AndroidViewModel(app) {

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

    fun getCurrentTime():String{
        val data =  Calendar.getInstance().time.toString().split(" ")
        val time = data[3].split(":")
        return data[0]+" "+data[1]+" "+data[2]+" "+time[0]+":"+time[1]
    }



}