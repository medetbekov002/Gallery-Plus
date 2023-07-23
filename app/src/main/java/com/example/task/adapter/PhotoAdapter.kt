package com.example.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.task.model.Photo
import com.example.task.utils.toFormattedDateTime
import com.example.task.databinding.ItemPhotoBinding

class PhotoAdapter(private var photos: List<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    private lateinit var binding: ItemPhotoBinding

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding.root)
    }

    /**
     * лучше производительность RecyclerView при обновлении списка.
     */
    private val differCallback = object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            // Check if the items have the same ID (or unique identifier)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

//    fun setPhotos(photos: List<Photo>) {
//        this.photos = photos
//        notifyDataSetChanged()
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val photo = photos[position]
        val photo = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(photo.path).centerCrop().into(binding.ivPhoto)
        }

        binding.tvDate.text = photo.creationDate.toFormattedDateTime()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}