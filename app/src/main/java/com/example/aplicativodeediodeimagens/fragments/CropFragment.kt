package com.example.aplicativodeediodeimagens.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.aplicativodeediodeimagens.databinding.FragmentCropBinding
import com.example.aplicativodeediodeimagens.viewmodel.CropViewModel
import com.canhub.cropper.CropImageView

class CropFragment : Fragment() {

    // View Binding
    private var _binding: FragmentCropBinding? = null
    private val binding get() = _binding!!

    // Fragment's ViewModel
    private val viewModel: CropViewModel by viewModels()

    // Returns the args sent in navigation
    private val args: CropFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configures the Observer to update the CropImageView when the image changes on ViewModel
        viewModel.image.observe(viewLifecycleOwner, Observer { bitmap: Bitmap? ->
            bitmap?.let {
                binding.cropImageView.setImageBitmap(it)
            }
        })

        // Updates the ViewModel with the received image as an argument
        args.image?.let {
            viewModel.changeImage(it)
        }

        // Enabling the Save Button
        binding.buttonSave.setOnClickListener {
            saveCroppedImage()
        }

        // Enabling the Rotate Button
        binding.buttonRotate.setOnClickListener {
            rotateCropImage()
        }
    }

    private fun rotateCropImage() {
        try {
            binding.cropImageView.rotateImage(90) // Rotate the image in 90°
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error in Rotation", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCroppedImage() {
        // Obtain the bitmap cropped from CropImageView
        val croppedBitmap: Bitmap? = binding.cropImageView.croppedImage

        // Update the LiveData in ViewModel
        croppedBitmap?.let { viewModel.changeImage(it) }

        // Defines the cropped bitmap as result for the MainFragment
        val result = Bundle().apply {
            putParcelable("croppedImage", croppedBitmap)
        }
        setFragmentResult("cropResult", result)

        // Closes the Fragment
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Frees the reference
    }
}