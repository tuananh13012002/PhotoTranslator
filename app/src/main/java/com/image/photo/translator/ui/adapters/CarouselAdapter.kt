package com.image.photo.translator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.image.photo.translator.data.models.Intro
import com.image.photo.translator.databinding.CarouselItemBinding

class CarouselAdapter(private val items: ArrayList<Intro>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = CarouselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class CarouselViewHolder(val binding: CarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Intro) {
            binding.imageViewCarouselItem.setImageResource(model.icon ?: 0)
            binding.titleImageIntro.text = model.title?:""
            binding.contentImgIntro.text = model.content?:""
        }
    }
}