package com.sreshtha.chatappandroid.util

import java.util.regex.Pattern

object Utility {


    fun validateRegistrationInput(email:String, password:String, confirmedPassword:String):Boolean{
        if(email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()){
            return  false
        }
        return true
    }


    fun validateLoginInput(email: String, password: String):Boolean{
        if(email.isEmpty() || password.isEmpty()){
            return false
        }
        return true
    }


    fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(".+@.+\\.[a-z]+")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun passwordIsSameAsConfirmPassword(password: String,confirmedPassword: String):Boolean{
        return password == confirmedPassword
    }




}