package com.image.photo.translator.ui.scan

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.image.photo.translator.R
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.fragment.BaseFragment
import com.image.photo.translator.data.models.DbConstants
import com.image.photo.translator.data.models.SingletonLanguageTrans
import com.image.photo.translator.databinding.FrScanBinding
import com.image.photo.translator.ui.adapters.ItemLanguageTransAdapter
import com.image.photo.translator.ui.home.HomeFr
import com.image.photo.translator.ui.home.HomeViewModel
import com.image.photo.translator.ui.photos.PhotoViewAct
import com.image.photo.translator.utils.AdsInter
import com.image.photo.translator.utils.SystemUtil
import com.image.photo.translator.vision.GraphicOverlay
import com.image.photo.translator.vision.TextGraphic
import com.nlbn.ads.callback.InterCallback
import com.nlbn.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ScanFr : BaseFragment() {

    private lateinit var binding: FrScanBinding
    private val viewmodel: HomeViewModel by activityViewModels()

    private var textRecognizer: TextRecognizer? = null
    private var translator: Translator? = null
    private var conditions: DownloadConditions? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FrScanBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        setOnBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val argumentValue = arguments?.getParcelable<Uri>(ARG_KEY_GALLERY)
        if (argumentValue != null) {
            loading()
            viewmodel.mSelectedImage =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, argumentValue)
            runTextRecognition(viewmodel.mSelectedImage!!)
            bindData()
            binding.btnSave.setOnClickListener {
                if (viewmodel.mSelectedImage != null) {
                    getSaveImageFilePath()
                }
            }
        } else {
            bindData()
            setupLanguage()
        }
        binding.imgBack.setOnClickListener {
            goBack()
        }
        if (AdsInter.inter_details == null) {
            loadInterDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        if (translator == null) {
            setOptionAndParamsTranslator()
        }
    }

    private fun bindData() {
        if (viewmodel.currentFrom != SingletonLanguageTrans.getInstance().langTransFrom.code!! || viewmodel.currentTo != SingletonLanguageTrans.getInstance().langTransTo.code!!) {
            viewmodel.currentFrom = SingletonLanguageTrans.getInstance().langTransFrom.code!!
            viewmodel.currentTo = SingletonLanguageTrans.getInstance().langTransTo.code!!
            setOptionAndParamsTranslator()

        } else {

            runTextRecognition(viewmodel.mSelectedImage)
        }
    }

    private fun setOptionAndParamsTranslator() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(viewmodel.currentFrom)
            .setTargetLanguage(viewmodel.currentTo)
            .build()

        //            SingletonLanguageTrans.getInstance().translator
        translator = Translation.getClient(options)
        conditions = DownloadConditions.Builder().build()
        conditions?.let {
            translator?.downloadModelIfNeeded(it)
                ?.addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    Log.d("LogCrash", "downloadModelIfNeeded success")
                    if (viewmodel.mSelectedImage != null) {
                        runTextRecognition(viewmodel.mSelectedImage!!)
                    }
                }
                ?.addOnFailureListener { error ->
                    Log.e("LogCrash", "downloadModelIfNeeded error $error")
                }
        }

    }

    private fun setupLanguage() {
        if (viewmodel.currentTo == "") {
            try {
                val defaultLanguageCode = Locale.getDefault().language
                Log.i(DbConstants.APP_NAME, defaultLanguageCode)
                val languageIndex = ItemLanguageTransAdapter.dummyData.indexOfFirst {
                    defaultLanguageCode.contains(it.code!!)
                }
                SingletonLanguageTrans.getInstance().langTransTo =
                    ItemLanguageTransAdapter.dummyData[languageIndex]
                SingletonLanguageTrans.getInstance().langTransToPositionSeleced = languageIndex
            } catch (e: java.lang.Exception) {
                Log.d("", e.localizedMessage, e)
            }
        }


        binding.apply {
            layoutContentTranslated.isClickable = true
            layoutContentTranslated.setOnClickListener {
                viewmodel.showTranslatedLayout = !viewmodel.showTranslatedLayout
                graphicOverlay.isVisible = viewmodel.showTranslatedLayout
                transparentView.isVisible = viewmodel.showTranslatedLayout
            }

            btnSave.setOnClickListener {
                if (viewmodel.mSelectedImage != null) {
                    getSaveImageFilePath()
                }
            }
            startCamera()
            imageView.setImageBitmap(viewmodel.mSelectedImage)
        }
    }

    private fun startCamera() {
        ImagePicker.with(this@ScanFr)
            .cameraOnly()
            .crop(
                1080f,
                1080f
            )    //Crop image(Optional), Check Customization for more option
            //.compress(1920)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startImageResult.launch(intent)
            }
    }

    private val startImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                viewmodel.mSelectedImage =
                    MediaStore.Images.Media.getBitmap(appContext.contentResolver, uri)

                if (viewmodel.mSelectedImage != null) {
                    // loading
                    loading()
                    val resizedBitmap = Bitmap.createScaledBitmap(
                        viewmodel.mSelectedImage!!,
                        (viewmodel.mSelectedImage!!.width),
                        (viewmodel.mSelectedImage!!.height),
                        true
                    )

                    binding.imageView.setImageBitmap(resizedBitmap)
                    viewmodel.mSelectedImage = resizedBitmap
                    runTextRecognition(viewmodel.mSelectedImage!!)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showToast(ImagePicker.getError(data))
            } else {
                binding.changeBg.visibility = View.GONE
                (activity as? MainActivity)?.replaceFragment(HomeFr())
            }
        }


    private fun getSaveImageFilePath(): String {
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

        // Create a media file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageName = "${YOUR_FOLDER_NAME}_$timeStamp.jpg"
        val selectedOutputPath = mediaStorageDir.path + File.separator + imageName
        Log.d(YOUR_FOLDER_NAME, "selected camera path $selectedOutputPath")
        binding.layoutContentTranslated.isDrawingCacheEnabled = true
        binding.layoutContentTranslated.buildDrawingCache()
        var bitmap: Bitmap = Bitmap.createBitmap(binding.layoutContentTranslated.drawingCache)
        val maxSize = 1080
        val bWidth = bitmap.width
        val bHeight = bitmap.height
        bitmap = if (bWidth > bHeight) {
            val imageHeight =
                Math.abs(maxSize * (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt()
            Bitmap.createScaledBitmap(bitmap, maxSize, imageHeight, true)
        } else {
            val imageWidth =
                Math.abs(maxSize * (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt()
            Bitmap.createScaledBitmap(bitmap, imageWidth, maxSize, true)
        }
        binding.layoutContentTranslated.isDrawingCacheEnabled = false
        binding.layoutContentTranslated.destroyDrawingCache()
        var fOut: OutputStream? = null
        try {
            val file = File(selectedOutputPath)
            fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
            lifecycleScope.launch {
                delay(1000)
            }
            showToast(requireActivity().getString(R.string.saved_successfully))
            if (SystemUtil.haveNetworkConnection(requireActivity())) {
                Admob.getInstance().showInterAds(requireActivity(), AdsInter.inter_details, object : InterCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        startActivity(PhotoViewAct.getIntent(requireActivity(), file.path, 3))
                        (activity as? MainActivity)?.finish()
                    }
                })
            }else{
                startActivity(PhotoViewAct.getIntent(requireActivity(), file.path, 3))
                (activity as? MainActivity)?.finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return selectedOutputPath
    }

    private fun runTextRecognition(bitmap: Bitmap?) {
        if (bitmap == null) {
            return
        }
        val image = InputImage.fromBitmap(bitmap, 0)
        binding.imageView.setImageBitmap(bitmap)
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textRecognizer?.process(image)//?.addOnSuccessListener(listenerSuccess())
            ?.addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            ?.addOnFailureListener { e -> // Task failed with an exception
                e.printStackTrace()
            }
    }

    private fun listenerSuccess() = OnSuccessListener<Text> {
        texts -> processTextRecognitionResult(texts)
    }

    fun processTextRecognitionResult(texts: Text) {
        binding.graphicOverlay.clear()
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            showToast(appContext.getString(R.string.no_text_fail))
            return
        }
        viewmodel.showTranslatedLayout = true

        binding.graphicOverlay.isVisible = viewmodel.showTranslatedLayout
        binding.transparentView.isVisible = viewmodel.showTranslatedLayout
//        binding.btnSave.isVisible = viewmodel.showTranslatedLayout
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
//                val elements = lines[j].elements
                //for (k in elements.indices) {
                translator?.translate(lines[j].text)
                    ?.addOnSuccessListener { translatedText ->
                        Log.d("LogCrash", "addOnSuccessListener")
                        val textGraphic: GraphicOverlay.Graphic =
                            TextGraphic(
                                binding.graphicOverlay,
                                lines[j],
                                lines[j].boundingBox,
                                translatedText
                            )
                        binding.graphicOverlay.add(textGraphic)   // Translation successful.
                    }
                    ?.addOnFailureListener { exception ->
                        Log.e("LogCrash", "addOnFailureListener")
                    }
                //}
            }
        }
    }

    private fun loading() {
        binding.progress.progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.progress.progressBar.visibility = View.GONE
        }, 3000L)
    }

    private fun showToast(s: String) {
        Toast.makeText(appContext, s, Toast.LENGTH_SHORT).show()
    }

    private fun setOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = false
                    goBack()
                }
            }
        })
    }
    private fun loadInterDetails() {
        Admob.getInstance().loadInterAds(activity,
            resources.getString(R.string.inter_details),
            object : InterCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    super.onInterstitialLoad(interstitialAd)
                    AdsInter.inter_details = interstitialAd
                }
            })
    }
    private fun goBack() {
        activity?.let {
//            startActivity(Intent(it, MainActivity::class.java)
            (activity as? MainActivity)?.replaceFragment(HomeFr())
        }
    }

    override fun onPause() {
        textRecognizer = null
        translator = null
        super.onPause()
    }

    companion object {
        private const val ARG_KEY_GALLERY = "Key_Gallery"

        fun newInstance(argument: Uri): ScanFr {
            val fragment = ScanFr()
            val args = Bundle()
            args.putParcelable(ARG_KEY_GALLERY, argument)
            fragment.arguments = args
            return fragment
        }
    }
}


