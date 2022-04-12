package com.sreshtha.chatappandroid.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

class HomeViewModelFactory(
    private val app:Application,
    private val user:FirebaseUser
):ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(app,user) as T
    }
}