package com.example.aplicativodeediodeimagens.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativodeediodeimagens.databinding.ItemPhotoHistoryBinding
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

class PhotoHistoryAdapter(private var photos: List<PhotoHistory>) :
    RecyclerView.Adapter<PhotoHistoryAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemPhotoHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: PhotoHistory) {
            binding.imagePath.text = photo.imagePath
            binding.creationDate.text = formatDate(photo.creationDate)

            // Loads the saved image
            val imgFile = File(photo.imagePath)
            if (imgFile.exists()) {
                val bitmap: Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                binding.imageViewHistory.setImageBitmap(bitmap)
            } else {
                binding.imageViewHistory.setImageResource(android.R.drawable.ic_menu_report_image)
                // Error icon when the image can't be found
            }
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