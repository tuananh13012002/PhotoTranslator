package com.image.photo.translator.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.image.photo.translator.R
import com.image.photo.translator.databinding.ItemPhotoBinding
import com.image.photo.translator.databinding.ItemPlacerBinding
import java.io.File

class PhotoAdapter(
    private var list: List<File>,
    private val context: Context,
    val callBack: (Int, File) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val hasData = list.isNotEmpty()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (hasData) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPhotoBinding.inflate(layoutInflater, parent, false)
            DataViewHolder(binding, context)
        } else {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemPlacerBinding.inflate(layoutInflater, parent, false)
            PlaceholderViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return if (hasData)
            if (list.size >= 9) 9 else list.size
        else
            1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DataViewHolder -> {
                val item = list[position]
                holder.bind(item, position)
                holder.binding.root.setOnClickListener {
                    callBack(position, item)
                }
            }

            is PlaceholderViewHolder -> {
                holder.bind()
            }
        }
    }

    fun update(data: List<File>?) {
        list = data ?: listOf()
        notifyDataSetChanged()
    }

    inner class PlaceholderViewHolder(val binding: ItemPlacerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.imgPhoto.setOnClickListener {
                Toast.makeText(context, context.getString(R.string.sample_file), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    inner class DataViewHolder(val binding: ItemPhotoBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: File, position: Int) {
            binding.apply {
                Glide.with(context).load(item).placeholder(R.drawable.baseline_folder_off_24)
                    .into(imgPhoto)
            }
        }
    }
}