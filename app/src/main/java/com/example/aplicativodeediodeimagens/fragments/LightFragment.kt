package com.example.aplicativodeediodeimagens.fragments

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.aplicativodeediodeimagens.databinding.FragmentLightBinding
import com.example.aplicativodeediodeimagens.viewmodel.LightViewModel

class LightFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentLightBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LightViewModel by viewModels()
    private var originalBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Bitmap>("image")?.let { bitmap ->
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            viewModel.changeImage(originalBitmap!!)
        }

        viewModel.image.observe(viewLifecycleOwner) { bitmap ->
            bitmap?.let {
                binding.imageView.setImageBitmap(it)
            }
        }

        binding.seekBarBrightness.setOnSeekBarChangeListener(this)
        binding.seekBarContrast.setOnSeekBarChangeListener(this)
        binding.seekBarSaturation.setOnSeekBarChangeListener(this)

        binding.buttonBrightness.setOnClickListener { showSeekBar(binding.seekBarBrightness, "Brightness") }
        binding.buttonContrast.setOnClickListener { showSeekBar(binding.seekBarContrast, "Contrast") }
        binding.buttonSaturation.setOnClickListener { showSeekBar(binding.seekBarSaturation, "Saturation") }

        binding.buttonSave.setOnClickListener {
            saveEditedImage()
        }
    }

    private fun saveEditedImage() {
        if (originalBitmap == null) return

        val brightness = binding.seekBarBrightness.progress
        val contrast = binding.seekBarContrast.progress
        val saturation = binding.seekBarSaturation.progress

        val editedBitmap = applyImageEffects(originalBitmap!!, brightness, contrast, saturation)

        // Define the edited image as a result for MainFragment
        val result = Bundle().apply {
            putParcelable("editedImage", editedBitmap)
        }
        setFragmentResult("lightResult", result)

        // Close the Fragment (simulate back button press)
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    private fun showSeekBar(selectedSeekBar: SeekBar, type: String) {
        binding.seekBarBrightness.visibility = View.GONE
        binding.seekBarContrast.visibility = View.GONE
        binding.seekBarSaturation.visibility = View.GONE

        selectedSeekBar.visibility = View.VISIBLE
        binding.text.text = "$type: ${selectedSeekBar.progress}"
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (originalBitmap == null) return

        val brightness = binding.seekBarBrightness.progress
        val contrast = binding.seekBarContrast.progress
        val saturation = binding.seekBarSaturation.progress

        val updatedImage = applyImageEffects(originalBitmap!!, brightness, contrast, saturation)
        binding.imageView.setImageBitmap(updatedImage)

        when (seekBar) {
            binding.seekBarBrightness -> binding.text.text = "Brightness: $brightness"
            binding.seekBarContrast -> binding.text.text = "Contrast: $contrast"
            binding.seekBarSaturation -> binding.text.text = "Saturation: $saturation"
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyImageEffects(bitmap: Bitmap, brightness: Int, contrast: Int, saturation: Int): Bitmap {
        val paint = Paint()
        val colorMatrix = ColorMatrix()

        val brightnessOffset = brightness * 2.55f
        val brightnessMatrix = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, brightnessOffset,
            0f, 1f, 0f, 0f, brightnessOffset,
            0f, 0f, 1f, 0f, brightnessOffset,
            0f, 0f, 0f, 1f, 0f
        ))

        val contrastScale = (contrast + 100) / 100f
        val contrastTranslate = (-0.5f * contrastScale + 0.5f) * 255
        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrastScale, 0f, 0f, 0f, contrastTranslate,
            0f, contrastScale, 0f, 0f, contrastTranslate,
            0f, 0f, contrastScale, 0f, contrastTranslate,
            0f, 0f, 0f, 1f, 0f
        ))

        val saturationScale = (saturation + 100) / 100f
        val saturationMatrix = ColorMatrix()
        saturationMatrix.setSaturation(saturationScale)

        colorMatrix.postConcat(brightnessMatrix)
        colorMatrix.postConcat(contrastMatrix)
        colorMatrix.postConcat(saturationMatrix)

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        val modifiedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(modifiedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return modifiedBitmap
    }
}