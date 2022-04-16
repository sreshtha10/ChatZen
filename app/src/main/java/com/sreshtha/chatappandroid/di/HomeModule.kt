package com.sreshtha.chatappandroid.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton


@InstallIn(ViewModelComponent::class)
@Module
object HomeModule {


    @Provides
    @ViewModelScoped
    fun provideCurrentUser():FirebaseUser{
        return FirebaseAuth.getInstance().currentUser!!
    }


}