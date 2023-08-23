package com.image.photo.translator.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.image.photo.translator.R
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityMainBinding
import com.image.photo.translator.ui.dialog_rating.DialogRating
import com.image.photo.translator.ui.home.HomeFr
import com.image.photo.translator.ui.scan.ScanFr
import com.image.photo.translator.ui.settings.SettingsFragment
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil.haveNetworkConnection
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CommitTransaction")
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var preferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    var lastClickTime = 0L
    //Rate
    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savePrefData()
        nav()
        //ads admob banner
        if (haveNetworkConnection(this)){
            binding.rlBannerList.visibility = View.VISIBLE
            Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collab), "bottom")
        }else{
            binding.rlBannerList.visibility = View.GONE
        }
        clickBanner()
    }

    private fun nav() {
        replaceFragment(HomeFr())
        binding.ivHome.setImageResource(R.drawable.ic_home_v2_select)
        binding.ivHome.setOnClickListener {
            binding.nav.isGone = false
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= 1000) { // Giới hạn thời gian giữa các lần click (ví dụ: 1 giây)
                lastClickTime = currentTime
                replaceFragment(HomeFr())
            }
        }
        binding.ivSetting.setOnClickListener {
            binding.nav.isGone = false
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= 1000) { // Giới hạn thời gian giữa các lần click (ví dụ: 1 giây)
                lastClickTime = currentTime
                replaceFragment(SettingsFragment())
            }
        }
        binding.ivScan.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= 1000) { // Giới hạn thời gian giữa các lần click (ví dụ: 1 giây)
                lastClickTime = currentTime
                replaceFragment(ScanFr())
            }
            AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
        }
        val backSettingsFragment = intent.getBooleanExtra("back_setting", false)
        if (backSettingsFragment) {
            replaceFragment(SettingsFragment())
            binding.ivSetting.setImageResource(R.drawable.ic_setting_v2_selected)
            binding.ivHome.setImageResource(R.drawable.ic_home_v2)
            return
        }
    }

    private fun savePrefData() {
        val pref = applicationContext.getSharedPreferences("myPref", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isIntroOpened", true)
        editor.commit()
    }

    override fun onResume() {
        super.onResume()
        binding.nav.visibility=View.VISIBLE
        Log.d("CUONGVC_RESUME", "onResume: aksdjakljdkajsd")
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)
    }

    override fun onBackPressed() {
        val numberBack = getSharedPreferences("PREF_NAME", MODE_PRIVATE).getInt("NUMBER_BACK", 1)
        if (!SharePrefUtils.isRated(this)) {
            if (numberBack % 2 == 1) {
                getSharedPreferences("PREF_NAME", MODE_PRIVATE).edit()
                    .putInt("NUMBER_BACK", numberBack + 1)
                    .apply()
                binding.frameLayout.visibility = View.GONE
               showDialogRate()
            } else {
                showNativeBackYakin()
            }
        } else {
            showNativeBackYakin()
        }
    }


    private fun showNativeBackYakin() {
        binding.frameLayout.visibility = View.GONE
        val dialog = Dialog(this)
        val viewRename = LayoutInflater.from(this).inflate(R.layout.dialog_go_back, null, false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(viewRename)
        dialog.setCancelable(false)
        val w2 = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val h2 = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(w2, h2)
        dialog.show()
        val button = dialog.findViewById<View>(R.id.bt_cancel) as Button
        button.setOnClickListener {
            binding.frameLayout.visibility = View.VISIBLE
            dialog.dismiss()
        }
        val button2 = dialog.findViewById<View>(R.id.bt_yes) as Button
        button2.setOnClickListener { view ->
            binding.frameLayout.visibility = View.VISIBLE
            okExitYakin(view)
        }
    }
    private fun okExitYakin(view: View?) {
        val numberBack = getSharedPreferences("PREF_NAME", MODE_PRIVATE).getInt("NUMBER_BACK", 0)
        if (numberBack < 9) {
            getSharedPreferences("PREF_NAME", MODE_PRIVATE).edit()
                .putInt("NUMBER_BACK", numberBack + 1)
                .apply()
        }
        finish()
    }

    private fun showDialogRate(){
        val ratingDialog = DialogRating(this)
        ratingDialog.init(
            this,
            object : DialogRating.OnPress {
                override fun sendThank() {
                    com.image.photo.translator.ui.dialog_rating.SharePrefUtils.forceRated(
                        this@MainActivity
                    )
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.txt_thankyou),
                        Toast.LENGTH_SHORT
                    ).show()

                    ratingDialog.dismiss()
                    finish()
                }

                override fun rating() {
                    manager = ReviewManagerFactory.create(this@MainActivity)
                    val request: Task<ReviewInfo> = manager!!.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reviewInfo = task.result
                            val flow: Task<Void> =
                                manager!!.launchReviewFlow(this@MainActivity, reviewInfo!!)
                            flow.addOnSuccessListener {
                                com.image.photo.translator.ui.dialog_rating.SharePrefUtils.forceRated(
                                    this@MainActivity
                                )
                                ratingDialog.dismiss()
                                finish()

                            }
                        } else {
                            ratingDialog.dismiss()
                            finish()
                        }
                    }
                }

                override fun cancel() {
                    binding.frameLayout.visibility = View.VISIBLE
                    finish()
                }
                override fun later() {
                    binding.frameLayout.visibility = View.VISIBLE
                    finish()
                    ratingDialog.dismiss()
                }
            }
        )
        try {
            ratingDialog.show()
        } catch (e: WindowManager.BadTokenException) {
            e.printStackTrace()
        }
    }
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
        when (fragment) {
            is HomeFr -> {
                binding.ivLibrary.isGone = false
                binding.nav.isGone = false
                binding.ivHome.setImageResource(R.drawable.ic_home_v2_select)
                binding.ivSetting.setImageResource(R.drawable.ic_setting_v2)
            }

            is SettingsFragment -> {
                binding.ivLibrary.isGone = true
                binding.nav.isGone = false
                binding.ivHome.setImageResource(R.drawable.ic_home_v2)
                binding.ivSetting.setImageResource(R.drawable.ic_setting_v2_selected)
            }
            is ScanFr->{
                binding.ivLibrary.isGone = true
                binding.nav.isGone = true
                binding.ivHome.setImageResource(R.drawable.ic_home_v2)
                binding.ivSetting.setImageResource(R.drawable.ic_setting_v2)
            }
        }
    }

    private fun clickBanner() {
        binding.ivLibrary.setOnClickListener {
            AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
            ImagePicker.with(this)
                .galleryOnly()
                .crop(
                    1080f,
                    1080f
                )                    //Crop image(Optional), Check Customization for more option
                //.compress(1920)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                ) //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                val scanFr = ScanFr.newInstance(fileUri)
                (this).replaceFragment(scanFr)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(appContext, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
            }
        }
}