package com.example.aplicativodeediodeimagens.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicativodeediodeimagens.databinding.ItemPhotoHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class PhotoHistoryAdapter(private var photos: List<PhotoHistory>) :
    RecyclerView.Adapter<PhotoHistoryAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemPhotoHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PhotoHistory) {
            binding.imagePath.text = photo.imagePath
            binding.creationDate.text = formatDate(photo.creationDate)

            // Glide Application
            Glide.with(binding.imageViewHistory.context)
                .load(photo.imagePath)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.imageViewHistory)
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size

    fun updateData(newPhotos: List<PhotoHistory>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}