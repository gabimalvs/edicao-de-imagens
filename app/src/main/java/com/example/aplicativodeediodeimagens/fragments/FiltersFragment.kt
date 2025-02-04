package com.example.aplicativodeediodeimagens.fragments

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicativodeediodeimagens.adapter.FilterAdapter
import com.example.aplicativodeediodeimagens.databinding.FragmentFiltersBinding

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private var originalBitmap: Bitmap? = null
    private var filteredBitmap: Bitmap? = null

    private val filterNames = listOf(
        "B&W", "Sepia", "Negative", "Contrast", "Darken",
        "Cyan", "Blue", "Red", "Green", "High Contrast"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Bitmap>("image")?.let { bitmap ->
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredBitmap = originalBitmap
            binding.imageViewFilter.setImageBitmap(originalBitmap)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = FilterAdapter(filterNames) { filterName ->
            applyFilter(filterName)
        }
        binding.recyclerViewFilters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewFilters.adapter = adapter
    }

    private fun applyFilter(filterName: String) {
        if (originalBitmap == null) return

        val colorMatrix = ColorMatrix()

        when (filterName) {
            "B&W" -> colorMatrix.setSaturation(0f)
            "Sepia" -> {
                colorMatrix.set(floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            "Negative" -> {
                colorMatrix.set(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
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
            "Darken" -> colorMatrix.setScale(0.7f, 0.7f, 0.7f, 1f)
            "Cyan" -> colorMatrix.setScale(0f, 1f, 1f, 1f)
            "Blue" -> colorMatrix.setScale(0f, 0f, 1f, 1f)
            "Red" -> colorMatrix.setScale(1f, 0f, 0f, 1f)
            "Green" -> colorMatrix.setScale(0f, 1f, 0f, 1f)
            "High Contrast" -> {
                colorMatrix.setSaturation(0f)
                val contrast = 2f
                val translate = (-0.5f * contrast + 0.5f) * 255
                val contrastMatrix = ColorMatrix(floatArrayOf(
                    contrast, 0f, 0f, 0f, translate,
                    0f, contrast, 0f, 0f, translate,
                    0f, 0f, contrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                ))
                colorMatrix.postConcat(contrastMatrix)
            }
        }

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        filteredBitmap = Bitmap.createBitmap(originalBitmap!!.width, originalBitmap!!.height, originalBitmap!!.config)
        val canvas = Canvas(filteredBitmap!!)
        canvas.drawBitmap(originalBitmap!!, 0f, 0f, paint)

        binding.imageViewFilter.setImageBitmap(filteredBitmap)
    }
}