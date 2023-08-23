package com.image.photo.translator.ui.multi_lang

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.image.photo.translator.R
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityMultilangBinding
import com.image.photo.translator.ui.adapters.ItemMultiLangAdapter
import com.image.photo.translator.ui.intro.IntroAct
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION", "CAST_NEVER_SUCCEEDS")
@AndroidEntryPoint
class MultiLangAct : BaseActivity() {
    private lateinit var binding: ActivityMultilangBinding
    var type: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultilangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getIntExtra(TYPE_LANG, 0)
        bindRv()
        checkType()
        loadNativeLanguage()
    }

    private fun checkType() {
        when (type) {
            1 -> {
                binding.typeLang1.visibility = View.VISIBLE
                binding.typeLang2.visibility = View.GONE
                binding.btnChooseLang.setOnClickListener {
                    SharePrefUtils.setFirstClick(this@MultiLangAct, true)
                    startActivity(Intent(this, IntroAct::class.java))
                    finish()
                }
            }

            2 -> {
                binding.typeLang1.visibility = View.GONE
                binding.typeLang2.visibility = View.VISIBLE
                binding.imgBack.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("back_setting", true)
                    startActivity(intent)
                    finish()
                }
            }
            else -> {
                finish()
            }
        }
    }

    private fun bindRv() {
        findViewById<RecyclerView>(R.id.rcv_langs).adapter = ItemMultiLangAdapter(
            ItemMultiLangAdapter.dummyData,
            this,
            getPosition(),
        ) { position, item ->
            SystemUtil.setPreLanguage(this, item.code)
            SystemUtil.setLocale(this)
        }
    }

    companion object {
        private const val TYPE_LANG = "MultiLangAct_Lang"
        fun getIntent(context: Context, type: Int): Intent {
            val intent = Intent(context, MultiLangAct::class.java)
            intent.putExtra(TYPE_LANG, type)
            return intent
        }
    }

    private fun getPosition(): Int {
        val pref = applicationContext.getSharedPreferences("myPref", MODE_PRIVATE)
        return pref.getInt("positionLang", 0)
    }

    private fun loadNativeLanguage() {
        // Load ads
        val listID: MutableList<String> = ArrayList()
        listID.add(getString(R.string.native_language))
        listID.add(getString(R.string.native_language1))
        try {
            Admob.getInstance().loadNativeAdFloor(this, listID, object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    val adView = LayoutInflater.from(this@MultiLangAct)
                        .inflate(R.layout.layout_native_language, null) as NativeAdView
                    binding.frAds.removeAllViews()
                    binding.frAds.addView(adView)
                    Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                }

                override fun onAdFailedToLoad() {
                    binding.frAds.visibility = View.GONE
                    binding.frAds.removeAllViews()
                }
            })
        } catch (e: Exception) {
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
        }
    }
}