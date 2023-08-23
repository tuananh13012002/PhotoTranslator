package com.image.photo.translator.ui.dialog_rating

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object SharePrefUtils {
    const val EMAIL_APP = "luickjahn49@gmail.com"
    const val POLICY =
        "https://campinggstudio.netlify.app/policy"
    private var mSharePref: SharedPreferences? = null
    fun init(context: Context?) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context)
        }
    }

    fun isRated(context: Context): Boolean {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getBoolean("rated", false)
    }

    fun getCountRateApp(context: Context): Int {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getInt("counts", 1)
    }

    fun increaseCountRateApp(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putInt("counts", pre.getInt("counts", 1) + 1)
        editor.commit()
    }

    fun forceRated(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putBoolean("rated", true)
        editor.commit()
    }

    fun getCountOpenApp(context: Context): Int {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getInt("countsOpenApp", 1)
    }

    fun increaseCountOpenApp(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putInt("countsOpenApp", pre.getInt("counts", 1) + 1)
        editor.apply()
    }

    fun setCountFirstOpenHome(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putInt("counts_home", 0)
        editor.apply()
    }

    fun getCountOpenHome(context: Context): Int {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        return pre.getInt("counts_home", 0)
    }

    fun increaseCountOpenHome(context: Context) {
        val pre = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor = pre.edit()
        editor.putInt("counts_home", pre.getInt("counts_home", 0) + 1)
        editor.apply()
    }
}