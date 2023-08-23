package com.image.photo.translator.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.image.photo.translator.databinding.ItemPhotoRowBinding
import java.io.File

public class SavedPhotoAdapter(
    private var list: List<File>,
    private val context: Context,
    val callBack: (Int, File) -> Unit
) : RecyclerView.Adapter<SavedPhotoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPhotoRowBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: File) {
            binding.apply {
                itemLangToTransTvName.text = item.name
                Glide.with(context).load(item).into(itemLangToTransImgAva)
            }
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPhotoRowBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding, context)
            }
        }
    }

    fun update(data: List<File>?) {
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
        viewHolder.bind(item)
        viewHolder.binding.root.setOnClickListener {
            callBack(position, item)
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.count()

}
