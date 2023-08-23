package com.image.photo.translator.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.ads.LoadAdError
import com.image.photo.translator.R
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivitySplashBinding
import com.image.photo.translator.ui.intro.IntroAct
import com.image.photo.translator.ui.multi_lang.MultiLangAct
import com.image.photo.translator.ui.permission.PermissionAct
import com.image.photo.translator.utils.DeviceUtil.hasStoragePermission
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.callback.InterCallback
import com.nlbn.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private var interCallback: InterCallback? = null
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtil.setPreLanguage(this, SystemUtil.getPreLanguage(this))
        SystemUtil.setLocale(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interCallback = object : InterCallback() {
            override fun onAdClosed() {
                super.onAdClosed()
                checkScreen()
            }

            override fun onAdFailedToLoad(i: LoadAdError?) {
                super.onAdFailedToLoad(i)
                onAdClosed()
            }
        }

        Admob.getInstance().loadSplashInterAds2(
            this@SplashActivity,
            getString(R.string.inter_splash),
            3000,
            interCallback
        )
    }

    private fun checkScreen() {
        if (!SharePrefUtils.getFirstClick(this)) {
            startActivity(MultiLangAct.getIntent(this, 1))
            finish()
        } else {

            if (!SharePrefUtils.getFirstClick2(this)) {
                val intent = Intent(this@SplashActivity, IntroAct::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                if (!SharePrefUtils.getFirstClick3(this)) {
                    if (!hasStoragePermission(this@SplashActivity) || !SharePrefUtils.getFirstClick3(this)) {
                        val intent = Intent(this@SplashActivity, PermissionAct::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    val intent = Intent(this@SplashActivity, IntroAct::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        Admob.getInstance().onCheckShowSplashWhenFail(this, interCallback, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Admob.getInstance().dismissLoadingDialog()
    }

    override fun onStop() {
        super.onStop()
        Admob.getInstance().dismissLoadingDialog()
    }

    override fun onBackPressed() {
    }
}