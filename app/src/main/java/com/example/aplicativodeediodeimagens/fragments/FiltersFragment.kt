package com.example.aplicativodeediodeimagens.fragments

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.aplicativodeediodeimagens.databinding.FragmentFiltersBinding

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private var originalBitmap: Bitmap? = null
    private var filteredBitmap: Bitmap? = null

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

        binding.buttonFilterBW.setOnClickListener { applyFilter(FilterType.BLACK_WHITE) }
        binding.buttonFilterSepia.setOnClickListener { applyFilter(FilterType.SEPIA) }
        binding.buttonFilterNegative.setOnClickListener { applyFilter(FilterType.NEGATIVE) }

        binding.buttonSave.setOnClickListener {
            saveFilteredImage()
        }
    }

    private fun applyFilter(filter: FilterType) {
        if (originalBitmap == null) return

        val colorMatrix = ColorMatrix()

        when (filter) {
            FilterType.BLACK_WHITE -> colorMatrix.setSaturation(0f)
            FilterType.SEPIA -> {
                colorMatrix.set(floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            FilterType.NEGATIVE -> {
                colorMatrix.set(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
        }

        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        filteredBitmap = Bitmap.createBitmap(originalBitmap!!.width, originalBitmap!!.height, originalBitmap!!.config)
        val canvas = Canvas(filteredBitmap!!)
        canvas.drawBitmap(originalBitmap!!, 0f, 0f, paint)

        binding.imageViewFilter.setImageBitmap(filteredBitmap)
    }

    private fun saveFilteredImage() {
        if (filteredBitmap == null) return

        val result = Bundle().apply {
            putParcelable("filteredImage", filteredBitmap)
        }
        setFragmentResult("filterResult", result)

        // Close the fragment
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class FilterType {
        BLACK_WHITE, SEPIA, NEGATIVE
    }
}