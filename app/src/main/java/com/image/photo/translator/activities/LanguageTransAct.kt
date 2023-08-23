package com.image.photo.translator.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.image.photo.translator.R
import com.image.photo.translator.data.models.SingletonLanguageTrans
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityLanguageTransBinding
import com.image.photo.translator.ui.adapters.ItemLanguageTransAdapter
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager

class LanguageTransAct : BaseActivity() {
    private lateinit var binding: ActivityLanguageTransBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageTransBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindLanguageToTransRcv()
        binding.langToTransBtnBack.setOnClickListener {
            finish()
        }
        loadNativeSmallChooseLanguage()
    }


    private fun bindLanguageToTransRcv() {
        val rcvReading: RecyclerView?
        val typeLang = intent.getStringExtra(TYPE_LANGUAGE)

        rcvReading = binding.langToTransRcvLang
        rcvReading.adapter = ItemLanguageTransAdapter(
            ItemLanguageTransAdapter.dummyData,
            this,
            if (typeLang == "from") {
                SingletonLanguageTrans.getInstance().langTransFromPositionSeleced
            } else {
                SingletonLanguageTrans.getInstance().langTransToPositionSeleced
            },
        ) { position, item ->
            //call back
            if (typeLang == "from") {
                SingletonLanguageTrans.getInstance().langTransFrom = item
                SingletonLanguageTrans.getInstance().langTransFromPositionSeleced = position
            } else {
                SingletonLanguageTrans.getInstance().langTransTo = item
                SingletonLanguageTrans.getInstance().langTransToPositionSeleced = position
            }

        }
    }
    private fun loadNativeSmallChooseLanguage() {
        if (SystemUtil.haveNetworkConnection(this)) {
            Admob.getInstance().loadNativeAd(
                this,
                getString(R.string.native_choose_language),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                        val adView = LayoutInflater.from(this@LanguageTransAct)
                            .inflate(R.layout.layout_native_small, null) as NativeAdView
                        binding.frAds.removeAllViews()
                        binding.frAds.addView(adView)
                        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                    }

                    override fun onAdFailedToLoad() {
                        binding.frAds.removeAllViews()
                    }
                }
            )
        } else {
            binding.frAds.removeAllViews()
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(LanguageTransAct::class.java)
    }
    companion object {
         const val TYPE_LANGUAGE = "LanguageTransAct-Type"
        fun getIntent(context: Context, type: String): Intent {
            val intent = Intent(context, LanguageTransAct::class.java)
            intent.putExtra(TYPE_LANGUAGE, type)
            return intent
        }
    }
}