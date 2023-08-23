package com.image.photo.translator

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.appsflyer.adrevenue.AppsFlyerAdRevenue
import com.image.photo.translator.activities.SplashActivity
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.util.AdsApplication
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class CustomApplication : AdsApplication() {

    override fun onCreate() {
        super.onCreate()

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppsFlyerLib.getInstance().init(this.getString(R.string.appsflyer), null, this)
        AppsFlyerLib.getInstance().start(this)
        val afRevenueBuilder = AppsFlyerAdRevenue.Builder(this)
        AppsFlyerAdRevenue.initialize(afRevenueBuilder.build())
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getListTestDeviceId(): List<String?>? {
        return null
    }

    override fun getResumeAdId(): String {
        return getString(R.string.appopen_resume)
    }

    override fun buildDebug(): Boolean {
        return BuildConfig.DEBUG
    }

}