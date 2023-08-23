package com.image.photo.translator.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ap_translator.models.LanguageTrans
import com.image.photo.translator.R
import com.image.photo.translator.databinding.ItemLangToTransBinding

public class ItemLanguageTransAdapter(
    private var list: List<LanguageTrans>,
    private val context: Context,
    var selectedPosition: Int,
    val callBack: (Int, LanguageTrans) -> Unit
) : RecyclerView.Adapter<ItemLanguageTransAdapter.ViewHolder>() {

    companion object {
        val dummyData = listOf(
                LanguageTrans(name = "Albanian", code = "sq", avatar = R.drawable.color_albania),
                LanguageTrans(name = "Azerbaijani", code = "az", avatar = R.drawable.color_azerbaijan),
                LanguageTrans(name = "Belarusian", code = "be", avatar = R.drawable.color_belarus),
                LanguageTrans(name = "Chinese", code = "zh", avatar = R.drawable.color_china),
                LanguageTrans(name = "Czech", code = "cs", avatar = R.drawable.color_czech_republic),
                LanguageTrans(name = "English", code = "en", avatar = R.drawable.color_united_kingdom),
                LanguageTrans(name = "French", code = "fr", avatar = R.drawable.color_france),
                LanguageTrans(name = "German", code = "de", avatar = R.drawable.color_germany),
                LanguageTrans(name = "Greek", code = "el", avatar = R.drawable.color_greece),
                LanguageTrans(name = "Hindi", code = "hi", avatar = R.drawable.color_india),
                LanguageTrans(name = "Italian", code = "it", avatar = R.drawable.color_italy),
                LanguageTrans(name = "Japanese", code = "ja", avatar = R.drawable.color_japan),
                LanguageTrans(name = "Russian", code = "ru", avatar = R.drawable.color_russia),
                LanguageTrans(name = "Spanish", code = "es", avatar = R.drawable.color_spain),
                LanguageTrans(name = "Thai", code = "th", avatar = R.drawable.color_thailand),
                LanguageTrans(name = "Vietnamese", code = "vi", avatar = R.drawable.color_vietnam),
        )
    }

    class ViewHolder(val binding: ItemLangToTransBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
 
        fun bind(item: LanguageTrans, isSelected: Boolean) {
            binding.apply {
                if (isSelected) {
                    itemLangToTransBackgroundView.setBackgroundResource(R.drawable.bg_change_lang_click)
                } else {
                    itemLangToTransBackgroundView.setBackgroundResource(R.drawable.bg_change_lang_normal)
                }
                itemLangToTransTvName.text = item.name
                item.avatar?.let { itemLangToTransImgAva.setImageResource(it) }
            }
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLangToTransBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, context)
            }
        }
    }

    fun update(data: List<LanguageTrans>?) {
        list = data ?: listOf()
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(viewGroup, context)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = list[position]
        viewHolder.bind(item, position == selectedPosition)
        viewHolder.binding.root.setOnClickListener {
            list[selectedPosition].isSelected = false
            selectedPosition = position
            list[selectedPosition].isSelected = true
            notifyDataSetChanged()
            callBack(position, item)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.count()

}
