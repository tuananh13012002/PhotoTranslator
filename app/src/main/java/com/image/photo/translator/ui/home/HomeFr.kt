package com.image.photo.translator.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.image.photo.translator.R
import com.image.photo.translator.activities.LanguageTransAct
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.fragment.BaseFragment
import com.image.photo.translator.data.models.DbConstants
import com.image.photo.translator.data.models.SingletonLanguageTrans
import com.image.photo.translator.databinding.FrHomeBinding
import com.image.photo.translator.ui.adapters.PhotoAdapter
import com.image.photo.translator.ui.dialog_rating.DialogRating
import com.image.photo.translator.ui.guide.GuideAct
import com.image.photo.translator.ui.permission.PermissionAct
import com.image.photo.translator.ui.photos.DetailPhotoAct
import com.image.photo.translator.ui.photos.PhotoViewAct
import com.image.photo.translator.ui.photos.PhotoViewAct.Companion.checkOpenFr
import com.image.photo.translator.ui.scan.ScanFr
import com.image.photo.translator.utils.AdsInter
import com.image.photo.translator.utils.AdsInter.inter_guide
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.callback.InterCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
@AndroidEntryPoint
class HomeFr : BaseFragment() {

    private lateinit var binding: FrHomeBinding
    private val viewmodel:HomeViewModel by activityViewModels()
    //Rate
    private var manager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrHomeBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        registerAllExceptionEvent(viewmodel, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeLanguageHome()
        bindRecycleView()
//        clickBanner()
        binding.all.setOnClickListener {
            startActivity(Intent(appContext, DetailPhotoAct::class.java))
        }

        binding.btnSwapLang.setOnClickListener {
            SingletonLanguageTrans.getInstance().swapLang()
            changeLanguageHome()
        }
        if (inter_guide == null) {
            loadInterGuide()
        }
        startGuide()
        loadNativeSmallHome()
    }

    private fun loadInterGuide() {
        Admob.getInstance().loadInterAds(requireActivity(),
            resources.getString(R.string.inter_guide),
            object : InterCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    super.onInterstitialLoad(interstitialAd)
                    inter_guide = interstitialAd
                }
            })
    }

    private fun clickBanner() {
        binding.ivLibrary.setOnClickListener {
            ImagePicker.with(requireActivity())
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
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data!!
                    val scanFr = ScanFr.newInstance(fileUri)
                    (activity as? MainActivity)?.replaceFragment(scanFr)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(appContext, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                }
            }
        }

    private fun changeLanguageHome() {
        binding.langFrom.text = SingletonLanguageTrans.getInstance().langTransFrom.name
        binding.langTo.text = SingletonLanguageTrans.getInstance().langTransTo.name
        binding.imgLangFrom.setImageResource(
            SingletonLanguageTrans.getInstance().langTransFrom.avatar ?: 0
        )
        binding.imgLangTo.setImageResource(
            SingletonLanguageTrans.getInstance().langTransTo.avatar ?: 0
        )

        binding.formLang.setOnClickListener {
            startForResult.launch(LanguageTransAct.getIntent(appContext, "from"))
        }
        binding.toLang.setOnClickListener {
            startForResult.launch(LanguageTransAct.getIntent(appContext, "to"))
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
            } else {
                // Handle accordingly
            }
        }

    override fun onResume() {
        super.onResume()
        binding.langFrom.text = SingletonLanguageTrans.getInstance().langTransFrom.name
        binding.langTo.text = SingletonLanguageTrans.getInstance().langTransTo.name
        binding.imgLangFrom.setImageResource(
            SingletonLanguageTrans.getInstance().langTransFrom.avatar ?: 0
        )
        binding.imgLangTo.setImageResource(
            SingletonLanguageTrans.getInstance().langTransTo.avatar ?: 0
        )
        SystemUtil.setPreLanguage(appContext, SystemUtil.getPreLanguage(appContext))
        SystemUtil.setLocale(appContext)
        bindRecycleView()

        if (checkOpenFr){
            checkOpenFr = false
            if (!SharePrefUtils.isRated(appContext)) {
                val count: Int = SharePrefUtils.getCountBackHome(appContext)
                if ((count == 2) || (count == 4) || (count == 6) || (count == 8)) {
                    binding.frmNative.visibility = View.GONE
                    showDialogRate()
                    Log.d("CUONGVC", "onResume: $count")
                } else {
                    return
                }
            }
        }
    }

    private fun bindRecycleView() {
        val listFile = loadData().toMutableList()
        binding.apply {
            rv.adapter = PhotoAdapter(listFile, appContext) { index, file ->
                startActivity(PhotoViewAct.getIntent(appContext, file.absolutePath, 2))
                (activity as? MainActivity)?.finish()
            }
        }
    }

    private fun loadData(): List<File> {
        val YOUR_FOLDER_NAME = DbConstants.getAppLable(appContext)
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            YOUR_FOLDER_NAME
        )
        // Create a storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(YOUR_FOLDER_NAME, "Failed to create directory")
            }
        }
        val files = mediaStorageDir.listFiles()
        files.sortByDescending {
            it.name
        }
        return files?.asList() ?: listOf()

    }
    private fun startGuide(){
        binding.ivAbout.setOnClickListener {
            if (SystemUtil.haveNetworkConnection(appContext)) {
                Admob.getInstance()
                    .showInterAds(requireActivity(), inter_guide, object : InterCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            val intent = Intent(requireActivity(), GuideAct::class.java)
                            startActivity(intent)
                            loadInterGuide()
                        }
                    })
            } else {
                val intent = Intent(requireActivity(), GuideAct::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadNativeSmallHome() {
        if (SystemUtil.haveNetworkConnection(requireActivity())) {
            try {
                Admob.getInstance().loadNativeAd(
                    requireActivity(),
                    getString(R.string.native_home),
                    object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                            try {
                                val adView = LayoutInflater.from(requireActivity())
                                    .inflate(R.layout.layout_native_small, null) as NativeAdView
                                binding.frmNative.removeAllViews()
                                binding.frmNative.addView(adView)
                                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
                            }catch (e : Exception){
                                e.printStackTrace()
                            }
                        }

                        override fun onAdFailedToLoad() {
                            binding.frmNative.removeAllViews()
                        }
                    }
                )
            }catch (e : Exception){
                e.printStackTrace()
            }

        } else {
            binding.frmNative.removeAllViews()
        }
    }

    private fun showDialogRate(){
        val ratingDialog = DialogRating(appContext)
        ratingDialog.init(
            appContext,
            object : DialogRating.OnPress {
                override fun sendThank() {
                    com.image.photo.translator.ui.dialog_rating.SharePrefUtils.forceRated(
                        appContext
                    )
                    Toast.makeText(
                        appContext,
                        context?.getString(R.string.txt_thankyou),
                        Toast.LENGTH_SHORT
                    ).show()

                    ratingDialog.dismiss()
                }

                override fun rating() {
                    manager = ReviewManagerFactory.create(appContext)
                    val request: Task<ReviewInfo> = manager!!.requestReviewFlow()
                    request.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reviewInfo = task.result
                            val flow: Task<Void> =
                                manager!!.launchReviewFlow(activity!!, reviewInfo!!)
                            flow.addOnSuccessListener {
                                com.image.photo.translator.ui.dialog_rating.SharePrefUtils.forceRated(
                                    appContext
                                )
                                ratingDialog.dismiss()

                            }
                        } else {
                            ratingDialog.dismiss()
                        }
                    }
                }

                override fun cancel() {
                    binding.frmNative.visibility = View.VISIBLE
                }
                override fun later() {
                    binding.frmNative.visibility = View.VISIBLE
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

}