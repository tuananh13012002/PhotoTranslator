package com.image.photo.translator.ui.photos

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.PopupMenu
import com.image.photo.translator.R
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityPhotoViewBinding
import com.image.photo.translator.utils.SharePrefUtils
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.util.Admob
import java.io.File


@Suppress("CAST_NEVER_SUCCEEDS")
class PhotoViewAct : BaseActivity() {
    private lateinit var binding: ActivityPhotoViewBinding
    private var data: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = intent.getStringExtra(DATA_PHOTO)
        bindPhoto()
        binding.btnDelete.setOnClickListener {
            showPopupDelete(it)
        }
        loadBannerScreenDetail()
    }

    private fun showPopupDelete(view: View) {
        val popupMenu = PopupMenu(this@PhotoViewAct, view)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    if (deleteFile().exists()) {
                        deleteFile().delete()
                    }
                    popupMenu.dismiss()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                else -> false
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteFile(): File {
        val filePath = data
        return File(filePath ?: "")
    }

    private fun bindPhoto() {
        val bitmap = BitmapFactory.decodeFile(data)
        binding.photoView.setImageBitmap(bitmap)
        val type = intent.getIntExtra(TYPE_BACK, 0)

        if (type == 3) {
            binding.progress.progressBar.visibility=View.VISIBLE
            binding.mainDetail.visibility = View.GONE
            binding.layoutAdmob.visibility = View.GONE
            Handler().postDelayed({
                binding.progress.progressBar.visibility=View.GONE
                binding.mainDetail.visibility = View.VISIBLE
                binding.layoutAdmob.visibility = View.VISIBLE
            }, 2000L)
        }

        binding.btnBack.setOnClickListener {
            if (type == 1) {
                checkOpenFr = true
                SharePrefUtils.increaseCountBackHome(this@PhotoViewAct)
                finish()
            } else {
                checkOpenFr = true
                SharePrefUtils.increaseCountBackHome(this@PhotoViewAct)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
    private fun loadBannerScreenDetail() {
        if (SystemUtil.haveNetworkConnection(this)) {
            Admob.getInstance().loadBanner(this, getString(R.string.banner_all))
        } else {
            binding.rlBannerList.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        checkOpenFr = true
        SharePrefUtils.increaseCountBackHome(this@PhotoViewAct)
        startActivity(Intent(this, MainActivity::class.java))
    }

    companion object {
        private const val DATA_PHOTO = "PhotoViewAct_Data"
        private const val TYPE_BACK = "PhotoViewAct_Type"
        var checkOpenFr = false
        fun getIntent(context: Context, data: String, type: Int): Intent {
            val intent = Intent(context, PhotoViewAct::class.java)
            intent.putExtra(DATA_PHOTO, data)
            intent.putExtra(TYPE_BACK, type)
            return intent
        }
    }
}