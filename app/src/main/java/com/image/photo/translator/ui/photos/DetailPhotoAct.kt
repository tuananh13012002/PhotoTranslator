package com.image.photo.translator.ui.photos

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.image.photo.translator.R
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.data.models.DbConstants
import com.image.photo.translator.databinding.ActivityDetailPhotoBinding
import com.image.photo.translator.ui.adapters.DetailPhotoAdapter
import com.image.photo.translator.ui.adapters.Listener
import com.image.photo.translator.utils.SystemUtil
import com.nlbn.ads.util.Admob
import java.io.File


class DetailPhotoAct : BaseActivity() {
    private lateinit var binding: ActivityDetailPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binData()
        binding.imgBack.setOnClickListener {
            finish()
        }
        loadBannerRecent()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for storage access successful!
                // Read your files now
                binData()
            } else {
                // Allow permission for storage access!
            }
        }
    }

    private fun binData() {
        bindRecycleView()
        bindRecycleViewYesterday()
    }

    private fun bindRecycleView() {
        val listFile = loadData().toMutableList()
        binding.apply {
            rv.adapter = DetailPhotoAdapter(listFile, this@DetailPhotoAct, 1, object : Listener {
                override fun onClick(pos: Int, file: File) {
                    startActivity(PhotoViewAct.getIntent(this@DetailPhotoAct, file.absolutePath, 1))
                }

                override fun remove(file: File, pos: Int, view: View) {
                    showPopupDelete(listFile, pos, rv, view)
                }
            })
        }
    }

    private fun bindRecycleViewYesterday() {
        val listFile = loadData().toMutableList()
        binding.apply {
            rvYesterday.adapter =
                DetailPhotoAdapter(listFile, this@DetailPhotoAct, 2, object : Listener {
                    override fun onClick(pos: Int, file: File) {
                        startActivity(
                            PhotoViewAct.getIntent(
                                this@DetailPhotoAct, file.absolutePath, 1
                            )
                        )
                    }

                    override fun remove(file: File, pos: Int, view: View) {
                        showPopupDelete(listFile, pos, rvYesterday, view)
                    }
                })
        }
    }

    private fun showPopupDelete(
        listFile: MutableList<File>,
        pos: Int,
        recyclerView: RecyclerView,
        view: View
    ) {
        val wrapper = ContextThemeWrapper(this, R.style.BasePopupMenu)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    val item = listFile[pos]
                    if (item.exists()) {
                        item.delete()
                    }
                    listFile.removeAt(pos)
                    (recyclerView.adapter as? DetailPhotoAdapter)?.update(listFile)
                    binData()
                    popupMenu.dismiss()
                }

                else -> false
            }
            true
        }
        popupMenu.show()
    }


    private fun loadBannerRecent() {
        if (SystemUtil.haveNetworkConnection(this)) {
            Admob.getInstance().loadBanner(this, getString(R.string.banner_all))
        }else{
            binding.rlBannerList.visibility = View.GONE
        }
    }
    private fun loadData(): List<File> {
        val YOUR_FOLDER_NAME = DbConstants.getAppLable(this)
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

    override fun onResume() {
        super.onResume()
        binData()
    }
}