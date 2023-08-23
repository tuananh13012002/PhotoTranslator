package com.image.photo.translator.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.image.photo.translator.R
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.data.models.Intro
import com.image.photo.translator.databinding.ActivityIntroDemoBinding
import com.image.photo.translator.ui.adapters.CarouselAdapter
import com.image.photo.translator.ui.permission.PermissionAct
import com.image.photo.translator.utils.AdsInter.inter_intro
import com.image.photo.translator.utils.DeviceUtil
import com.image.photo.translator.utils.DeviceUtil.hasCameraPermission
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil.haveNetworkConnection
import com.nlbn.ads.callback.InterCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
@AndroidEntryPoint
class IntroAct : BaseActivity() {
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var binding: ActivityIntroDemoBinding
    private var countLoadNativeOnBoard = 0
    var position = 0
    private var posViewPager = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {

        carouselAdapter = CarouselAdapter(getData())
        binding.viewPager2.adapter = carouselAdapter
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(0))
        compositePageTransformer.addTransformer { view, position ->
            val r = 1 - abs(position)
            view.scaleY = 0.8f + r * 0.2f
            val absPosition = abs(position)
            view.alpha = 1.0f - (1.0f - 0.3f) * absPosition
        }

        countLoadNativeOnBoard++
        loadNativeTutorial()
        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("InvalidAnalyticsName", "UseCompatLoadingForDrawables")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        posViewPager = 0
                        binding.tvGetStart.text = getString(R.string.intro_next)
                        binding.view1.setImageResource(R.drawable.ic_view_select)
                        binding.view2.setImageResource(R.drawable.ic_view_un_select)
                        binding.view3.setImageResource(R.drawable.ic_view_un_select)
                    }

                    1 -> {
                        if (countLoadNativeOnBoard == 1) {
                            countLoadNativeOnBoard++
                        }
                        posViewPager = 2
                        posViewPager = 1
                        binding.tvGetStart.text = getString(R.string.intro_next)
                        binding.view1.setImageResource(R.drawable.ic_view_un_select)
                        binding.view2.setImageResource(R.drawable.ic_view_select)
                        binding.view3.setImageResource(R.drawable.ic_view_un_select)
                    }

                    2 -> {
                        posViewPager = 2
                        binding.tvGetStart.text = getString(R.string.intro_next)
                        binding.view1.setImageResource(R.drawable.ic_view_un_select)
                        binding.view2.setImageResource(R.drawable.ic_view_un_select)
                        binding.view3.setImageResource(R.drawable.ic_view_select)
                    }
                }
            }
        })
        bindView()
        if (inter_intro == null) {
            loadInterIntro()
        }
    }

    private fun bindView() {
        binding.tvGetStart.setOnClickListener {
            /*gotoNextScreen()*/
            when (posViewPager) {
                0 -> {
                    posViewPager = 1
                    binding.viewPager2.currentItem = 1
                    binding.tvGetStart.text = getString(R.string.intro_next)
                    binding.view1.setImageResource(R.drawable.ic_view_un_select)
                    binding.view2.setImageResource(R.drawable.ic_view_select)
                    binding.view3.setImageResource(R.drawable.ic_view_un_select)
                }

                1 -> {
                    binding.viewPager2.currentItem = 2
                    binding.tvGetStart.text = getString(R.string.intro_next)
                    binding.view1.setImageResource(R.drawable.ic_view_un_select)
                    binding.view2.setImageResource(R.drawable.ic_view_un_select)
                    binding.view3.setImageResource(R.drawable.ic_view_select)
                }

                2 -> {
                    gotoNextScreen()
                }
            }
        }
    }

    private fun gotoNextScreen() {
        SharePrefUtils.setFirstClick2(this@IntroAct, true)
        if (haveNetworkConnection(this)) {
            if (SharePrefUtils.getFirstClick3(this@IntroAct)) {
                checkPerGotoScreen()
            } else {
                Admob.getInstance().showInterAds(this, inter_intro, object : InterCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        checkPerGotoScreen()
                    }
                })
            }
        } else {
            if (!DeviceUtil.hasStoragePermission(this@IntroAct) || !SharePrefUtils.getFirstClick3(this)) {
                val intent = Intent(this@IntroAct, PermissionAct::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@IntroAct, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun checkPerGotoScreen() {
        if (!hasCameraPermission(this@IntroAct)) {
            startActivity(Intent(this@IntroAct, PermissionAct::class.java))
            finish()
        } else {
            startActivity(Intent(this@IntroAct, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        exitProcess(0)

    }

    private fun loadNativeTutorial() {
        if (haveNetworkConnection(this)) {
            try {
                Admob.getInstance().loadNativeAd(this,
                    getString(R.string.native_intro),
                    object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            super.onNativeAdLoaded(nativeAd)
                            val adView = LayoutInflater.from(this@IntroAct)
                                .inflate(R.layout.layout_native_language, null) as NativeAdView
                            binding.frAds.removeAllViews()
                            binding.frAds.addView(adView)
                            Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                        }

                        override fun onAdFailedToLoad() {
                            super.onAdFailedToLoad()
                            binding.frAds.removeAllViews()
                        }
                    })
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        } else {
            binding.frAds.removeAllViews()
        }

    }

    private fun loadInterIntro() {
        Admob.getInstance().loadInterAds(this@IntroAct,
            resources.getString(R.string.inter_intro),
            object : InterCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    super.onInterstitialLoad(interstitialAd)
                    inter_intro = interstitialAd
                }
            })
    }

    private fun getData(): ArrayList<Intro> {
        val mHelpGuide: ArrayList<Intro> = ArrayList()
        mHelpGuide.add(
            Intro(
                getString(R.string.title_intro_1),
                getString(R.string.content_intro_1),
                R.drawable.intro1_v2
            )
        )
        mHelpGuide.add(
            Intro(
                getString(R.string.title_intro_2),
                getString(R.string.content_intro_2),
                R.drawable.intro2_v2
            )
        )
        mHelpGuide.add(
            Intro(
                getString(R.string.title_intro_3),
                getString(R.string.content_intro_3),
                R.drawable.intro3_v2
            )
        )

        return mHelpGuide
    }
}