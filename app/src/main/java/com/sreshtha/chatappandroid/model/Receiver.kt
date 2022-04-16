package com.sreshtha.chatappandroid.model

import android.net.Uri
import java.io.Serializable


class Receiver(
    val email:String,
    val nickname:String,
    var photoUrl:Uri?
):Serializable