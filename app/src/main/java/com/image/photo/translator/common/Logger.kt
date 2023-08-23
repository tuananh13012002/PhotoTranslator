package com.image.photo.translator.common

import android.util.Log
import com.image.photo.translator.BuildConfig

object Logger {

    fun log(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

}