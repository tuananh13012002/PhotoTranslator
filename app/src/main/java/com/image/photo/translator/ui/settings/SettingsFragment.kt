package com.image.photo.translator.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.image.photo.translator.R
import com.image.photo.translator.base.fragment.BaseFragment
import com.image.photo.translator.databinding.FragmentSettingsBinding
import com.image.photo.translator.ui.dialog_rating.DialogRating
import com.google.android.play.core.tasks.Task
import com.image.photo.translator.activities.MainActivity
import com.image.photo.translator.ui.multi_lang.MultiLangAct
import com.image.photo.translator.utils.SharePrefUtils
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by hientx on 7/27/23.
 */

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

	//Rate
	private var manager: ReviewManager? = null
	private var reviewInfo: ReviewInfo? = null
	private lateinit var binding: FragmentSettingsBinding

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		super.onCreateView(inflater, container, savedInstanceState)
		binding = FragmentSettingsBinding.inflate(layoutInflater)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.imgBack.setOnClickListener {
		}
		binding.langSetting.setOnClickListener {
			startMultiLanguage()
		}
		binding.rateSetting.setOnClickListener {
			if (SharePrefUtils.isRated(appContext)) {
				Toast.makeText(context, getString(R.string.use_rate), Toast.LENGTH_SHORT).show()
			} else {
				showDialogRate()
			}
		}
		binding.privacySetting.setOnClickListener {
			startPolicy()
			AppOpenManager.getInstance().disableAppResumeWithActivity(MainActivity::class.java)
		}
	}

	private fun startMultiLanguage() {
		startActivity(MultiLangAct.getIntent(appContext, 2))
		(activity as? MainActivity)?.finish()
	}

	private fun showDialogRate() {
		val ratingDialog = DialogRating(requireContext())
		ratingDialog.init(
			appContext,
			object : DialogRating.OnPress {
				override fun sendThank() {
					SharePrefUtils.forceRated(
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
								SharePrefUtils.forceRated(
									appContext
								)
								ratingDialog.dismiss()

							}
						} else {
							ratingDialog.dismiss()
						}
					}
				}

				override fun cancel() {}
				override fun later() {
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

	private fun startPolicy() {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(this.getString(R.string.privacy_policy_link)))
		startActivity(intent)
	}

}