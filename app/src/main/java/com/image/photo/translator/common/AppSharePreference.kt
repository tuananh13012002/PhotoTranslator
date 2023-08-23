package com.image.photo.translator.common

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppSharePreference @Inject constructor(private val context: Context) {
    companion object{
        const val APP_SHARE_KEY = "com.image.photo.translator"
        const val FIRST_LAUNCH = "com.image.photo.translator.first_launch"
        const val APP_RATING = "com.image.photo.translator.rating"
    }

    fun getSharedPreferences(): SharedPreferences?{
        return context.getSharedPreferences(APP_SHARE_KEY,Context.MODE_PRIVATE)
    }

}