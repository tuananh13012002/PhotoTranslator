package com.image.photo.translator.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.image.photo.translator.R
import com.image.photo.translator.databinding.ItemDetailPhotoBinding
import com.image.photo.translator.databinding.ItemNoImageAllRecentBinding
import com.image.photo.translator.databinding.ItemPlacerBinding
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects

class DetailPhotoAdapter(
    private var list: List<File>,
    private val context: Context,
    private val type:Int,
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val hasData = list.isNotEmpty()
    inner class PlaceholderViewHolder(val binding: ItemNoImageAllRecentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind() {
            binding.imgPhoto.setOnClickListener {
                Toast.makeText(context, context.getString(R.string.sample_file), Toast.LENGTH_SHORT).show()
            }
        }
    }
    inner class MyViewHolder(val binding: ItemDetailPhotoBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindToday(item: File, position: Int) {
            binding.apply {
                val result=isDateTodayOrYesterday(checkTime(item.path))
                if (Objects.equals(result,"Today")){
                    Glide.with(context).load(item).placeholder(R.drawable.baseline_folder_off_24)
                        .into(imgPhoto)
                }else{
                    binding.root.visibility= View.GONE
                    binding.root.layoutParams=RecyclerView.LayoutParams(0,0)
                }
            }
        }
        fun bindYesterday(item: File, position: Int) {
            binding.apply {
                val result=isDateTodayOrYesterday(checkTime(item.path))
                if (Objects.equals(result,"Yesterday")||Objects.equals(result,"Neither Today nor Yesterday")){
                    Glide.with(context).load(item).placeholder(R.drawable.baseline_folder_off_24)
                        .into(imgPhoto)
                }else{
                    binding.root.visibility= View.GONE
                    binding.root.layoutParams=RecyclerView.LayoutParams(0,0)
                }
            }
        }


       private fun checkTime(inputPath: String):LocalDate{
            val filePath = Paths.get(inputPath)
            val attributes: BasicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
            val lastModifiedTime = attributes.lastModifiedTime()
            val dateItem=lastModifiedTime.toString().substring(0,10)
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val inputDate = LocalDate.parse(dateItem, dateFormatter)
            val result = isDateTodayOrYesterday(inputDate)
            Log.i("TuanAnh", "Input date $inputDate is $result")
            return  inputDate
        }
        private fun isDateTodayOrYesterday(inputDate: LocalDate): String {
            val currentDate = LocalDate.now()
            val yesterdayDate = currentDate.minusDays(1)

            return when (inputDate) {
                currentDate -> "Today"
                yesterdayDate -> "Yesterday"
                else -> "Neither Today nor Yesterday"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (hasData) {
            val binding = ItemDetailPhotoBinding.inflate(layoutInflater, parent, false)
            MyViewHolder(binding, context)
        } else {
            val binding = ItemNoImageAllRecentBinding.inflate(layoutInflater, parent, false)
            PlaceholderViewHolder(binding)
        }
    }


    override fun getItemCount() :Int{
        return if (hasData)
           list.size
        else
            1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MyViewHolder->{
                val item = list[position]
                if(type==1){
                    holder.bindToday(item, position)
                }else{
                    holder.bindYesterday(item, position)
                }
                holder.binding.root.setOnClickListener {
                    listener.onClick(position, item)
                }
                holder.binding.delete.setOnClickListener {
                    listener.remove(item,position,it)
                }
            }
            is PlaceholderViewHolder->{
                holder.bind()
            }
        }
    }

    fun update(data: List<File>?) {
        list = data ?: listOf()
        notifyDataSetChanged()
    }
}
interface Listener{
    fun onClick(pos:Int,file:File)
    fun remove(file:File,pos:Int,view: View)
}