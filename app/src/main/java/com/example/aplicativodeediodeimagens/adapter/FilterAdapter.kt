package com.example.aplicativodeediodeimagens.adapter

import android.content.Context
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicativodeediodeimagens.Cartoonizer
import com.example.aplicativodeediodeimagens.R
import kotlinx.coroutines.*

class FilterAdapter(
    private val context: Context,
    private val filterList: List<String>,
    private val originalBitmap: Bitmap,
    private val onFilterSelected: (String) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val filterPreviews: MutableMap<String, Bitmap> = mutableMapOf()
    private val cartoonizer = Cartoonizer(context) //Created the Cartoon Instance

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_preview, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filterName = filterList[position]

        // If it already processed this filter, it uses the saved version. If not, creates a new one.
        val filteredBitmap = filterPreviews.getOrPut(filterName) {
            if (filterName == "Cartoon") {
                runBlocking {
                    withContext(Dispatchers.Default){
                        cartoonizer.processImage(originalBitmap)
                    }
                }
            } else{
                applyFilter(filterName, originalBitmap.copy(Bitmap.Config.ARGB_8888, true))
            }
        }

        holder.bind(filterName, filteredBitmap)
    }

    override fun getItemCount(): Int = filterList.size

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)
        private val filterName: TextView = itemView.findViewById(R.id.filterName)

        fun bind(filterNameText: String, filteredImage: Bitmap) {
            filterName.text = filterNameText
            imagePreview.setImageBitmap(filteredImage)

            itemView.setOnClickListener { onFilterSelected(filterNameText) }
        }
    }
    private fun applyFilter(filterName: String, bitmap: Bitmap): Bitmap {
        val colorMatrix = ColorMatrix()

        when (filterName) {
            "B&W" -> colorMatrix.setSaturation(0f)

            "Sepia" -> colorMatrix.set(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))

            "Negative" -> {
                val negativeMatrix = ColorMatrix(floatArrayOf(
                    -1f,  0f,  0f,  0f, 255f,
                    0f, -1f,  0f,  0f, 255f,
                    0f,  0f, -1f,  0f, 255f,
                    0f,  0f,  0f,  1f,   0f
                ))
                colorMatrix.set(negativeMatrix)
            }

            "Contrast" -> {
                val contrast = 1.5f
                val translate = (-0.5f * contrast + 0.5f) * 255
                colorMatrix.set(floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
            }

            "Darken" -> {
                val darkenMatrix = ColorMatrix()
                darkenMatrix.setScale(0.5f, 0.5f, 0.5f, 1f) // Reduz brilho
                colorMatrix.set(darkenMatrix)
            }

            "Cyan" -> {
                val cyanMatrix = ColorMatrix(floatArrayOf(
                    0f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.set(cyanMatrix)
            }

            "Blue" -> colorMatrix.setScale(0f, 0f, 1f, 1f)
            "Red" -> colorMatrix.setScale(1f, 0f, 0f, 1f)
            "Green" -> colorMatrix.setScale(0f, 1f, 0f, 1f)

            "High Contrast" -> {
                colorMatrix.setSaturation(0f)
                val contrast = 2.0f
                val translate = (-0.5f * contrast + 0.5f) * 255
                val contrastMatrix = ColorMatrix(floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(contrastMatrix)
            }

            "Cartoon" -> return cartoonizer.processImage(bitmap)
        }

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val filteredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(filteredBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return filteredBitmap
    }
}