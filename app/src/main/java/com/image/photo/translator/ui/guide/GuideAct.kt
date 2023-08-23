package com.image.photo.translator.ui.guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import com.image.photo.translator.R
import com.image.photo.translator.base.activities.BaseActivity
import com.image.photo.translator.databinding.ActivityGuideBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuideAct : BaseActivity() {
    private lateinit var binding: ActivityGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindAction()
    }

    private fun bindAction() {
        binding.apply {
            ivNextGuide.setOnClickListener {
                txtGuide4.isGone = false
                txtGuide5.isGone = false
                imgGuide.isGone = true
                ivNextGuide.isGone = true
                ivGuideCamera.isGone = false
                ivGuideLibrary.isGone = false
                ivGuideExit.isGone = false
                titleGuide.text = getString(R.string.title_guide_step2)
                txtGuide1.text = getString(R.string.text_guide_1_step2)
                txtGuide2.text = getString(R.string.text_guide_2_step2)
                txtGuide3.text = getString(R.string.text_guide_3_step2)
                txtGuide4.text = getString(R.string.text_guide_4_step2)
                txtGuide5.text = getString(R.string.text_guide_5_step2)
            }
            ivGuideExit.setOnClickListener {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, GuideAct::class.java)
        }
    }
}