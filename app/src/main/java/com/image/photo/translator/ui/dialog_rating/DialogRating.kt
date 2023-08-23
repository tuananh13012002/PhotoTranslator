package com.image.photo.translator.ui.dialog_rating

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.image.photo.translator.R
import com.image.photo.translator.databinding.FragmentDialogRatingBinding


class DialogRating(private val context: Context) : Dialog(
    context, R.style.CustomAlertDialog
) {
    private var binding: FragmentDialogRatingBinding =
        FragmentDialogRatingBinding.inflate(layoutInflater)
    private var onPress: OnPress? = null
    private val btnRate: Button
    private val btnLater: Button

    init {
        setContentView(binding.root)
        val attributes = window!!.attributes
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = attributes
        window!!.setSoftInputMode(16)
        btnRate = findViewById<View>(R.id.btnRate) as Button
        btnLater = findViewById<View>(R.id.btnLater) as Button

        onclick()
        changeRating()

        setCancelable(false)

        btnRate.text = context.getString(R.string.title_dialog_rate)
        binding.imgIcon.setImageResource(R.drawable.rating_default)

    }

    interface OnPress {
        fun sendThank()
        fun rating()
        fun cancel()
        fun later()
    }

    fun init(context: Context?, onPress: OnPress?) {
        this.onPress = onPress
    }

    @SuppressLint("ClickableViewAccessibility")
    fun changeRating() {
        binding.rtb.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            when (rating.toString()) {
                "1.0" -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating)
                    binding.imgIcon.setImageResource(R.drawable.rating_1)
                }

                "2.0" -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating)
                    binding.imgIcon.setImageResource(R.drawable.rating_2)
                }

                "3.0" -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating)
                    binding.imgIcon.setImageResource(R.drawable.rating_3)
                }

                "4.0" -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating_4_5)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating_4_5)
                    binding.imgIcon.setImageResource(R.drawable.rating_4)
                }

                "5.0" -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating_4_5)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating_4_5)
                    binding.imgIcon.setImageResource(R.drawable.rating_5)
                }

                else -> {
                    binding.tvTitle.text = context.getString(R.string.title_dialog_rating_0)
                    binding.tvContent.text = context.getString(R.string.content_dialog_rating_0)
                    binding.imgIcon.setImageResource(R.drawable.rating_default)
                }
            }
        }
    }

    fun onclick() {
        btnRate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                SharePrefUtils.forceRated(context)
                if (binding.rtb.rating == 0f) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_feedback),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (binding.rtb.rating <= 4.0) {
                    binding.imageView.visibility = View.GONE
                    binding.imgIcon.visibility = View.VISIBLE
                    onPress!!.sendThank()
                } else {
                    //Edit
                    //imageView.setVisibility(View.VISIBLE);
                    binding.imageView.visibility = View.GONE
                    binding.imgIcon.visibility = View.VISIBLE
                    onPress!!.rating()
                }
            }
        })
        btnLater.setOnClickListener { onPress!!.later() }
    }

    private fun savePrefData() {
        val pref = context.getSharedPreferences("myPref", AppCompatActivity.MODE_PRIVATE)
        val editor = pref?.edit()
        editor?.putBoolean("isRate", true)
        editor?.commit()
    }
}