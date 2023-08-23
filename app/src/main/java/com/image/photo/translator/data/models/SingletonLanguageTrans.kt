package com.image.photo.translator.data.models

import android.content.Context
import com.example.ap_translator.models.LanguageTrans
import com.image.photo.translator.R
import com.google.mlkit.nl.translate.Translator

public class SingletonLanguageTrans private constructor() {
    init {
        // define in constructor
// Create an English-German translator:
    }
        var languageType = ""
        var translator: Translator? = null

        var langTransFrom: LanguageTrans = LanguageTrans(name = "English", code = "en", avatar = R.drawable.color_united_kingdom)
        var langTransFromPositionSeleced = 5
        var langTransTo: LanguageTrans = LanguageTrans(name = "French", code = "fr", avatar = R.drawable.color_france)
        var langTransToPositionSeleced = 6


    private object Holder { val INSTANCE = SingletonLanguageTrans() }

    public fun swapLang() {
        val languageTrans = langTransFrom
        val langPosition = langTransFromPositionSeleced
        langTransFrom = langTransTo
        langTransFromPositionSeleced = langTransToPositionSeleced
        langTransTo = languageTrans
        langTransToPositionSeleced = langPosition
    }

    companion object {
        @JvmStatic
        fun getInstance(): SingletonLanguageTrans {
            return Holder.INSTANCE
        }
    }
}

public class DbConstants {
    companion object {
        val APP_NAME = "Camera Translator"

        fun getAppLable(context: Context): String {
            return  context.getString(R.string.app_name)
        }

    }
}