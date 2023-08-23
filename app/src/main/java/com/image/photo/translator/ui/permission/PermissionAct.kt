package com.image.photo.translator.ui.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.image.photo.translator.R
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityPermissionBinding
import com.image.photo.translator.utils.DeviceUtil
import com.image.photo.translator.utils.SharePrefUtils
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager

@Suppress("CAST_NEVER_SUCCEEDS", "DEPRECATION")
class PermissionAct : BaseActivity() {
    private lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnGo.setOnClickListener {
            if (binding.switch1.isChecked && binding.switch2.isChecked) {
                SharePrefUtils.setFirstClick3(this@PermissionAct, true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            checkStorePermission()
            binding.frAds.visibility = View.GONE
        }
        binding.switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            checkCameraPermission()
            binding.frAds.visibility = View.GONE
        }
        maxPermission()
        hideSystemUI()
        loadNativePermission()
    }

    private fun checkStorePermission() {
        try {
            if (DeviceUtil.hasStoragePermission(this)) {
                binding.switch1.isChecked = true
                binding.switch1.isEnabled = false
            } else {
                binding.switch1.isEnabled = true
                binding.switch1.isChecked = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), REQUEST_CODE_STORAGE_PERMISSION)
                else
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
            }
        } catch (e: Exception) {
            Log.i("Tuananh", e.toString())
        }
    }
    private fun maxPermission(){
        if(!binding.switch1.isChecked){
            binding.switch1.setOnClickListener {
                indexClickPermissionStore++
                if (indexClickPermissionStore > 2) {
                    startSettingApp()
                }
            }
        }

        if(!binding.switch2.isChecked){
            binding.switch2.setOnClickListener {
                indexClickPermissionCamera++
                if (indexClickPermissionCamera > 2) {
                    startSettingApp()
                }
            }
        }
    }
    private fun startSettingApp() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        requestAppSettingsLauncher.launch(intent)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionAct::class.java)
    }

    private val requestAppSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.switch2.isChecked = true
            binding.switch2.isEnabled = false
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
            binding.switch2.isEnabled = true
            binding.switch2.isChecked = false
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.frAds.visibility = View.VISIBLE
        if (indexClickPermissionStore > 2) {
            checkStorePermission()
        }
        if (indexClickPermissionCamera > 2) {
            checkCameraPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                binding.switch1.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }

            CAMERA_PERMISSION_CODE -> {
                binding.switch2.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switch1.isChecked = DeviceUtil.hasStoragePermission(this)
        binding.switch2.isChecked = DeviceUtil.hasCameraPermission(this)
        binding.frAds.visibility = View.VISIBLE
        AppOpenManager.getInstance().enableAppResumeWithActivity(PermissionAct::class.java)
    }


    override fun onStop() {
        super.onStop()
        hideSystemUI()
    }
    private fun loadNativePermission() {
        try {
            Admob.getInstance().loadNativeAd(
                this,
                getString(R.string.native_permission),
                object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        val adView = LayoutInflater.from(this@PermissionAct)
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
    }

    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 101
        const val CAMERA_PERMISSION_CODE = 1001
        var indexClickPermissionStore = 0
        var indexClickPermissionCamera = 0
    }
}