package com.example.aplicativodeediodeimagens.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.aplicativodeediodeimagens.R
import com.example.aplicativodeediodeimagens.databinding.FragmentMainBinding
import androidx.activity.result.contract.ActivityResultContracts
import com.example.aplicativodeediodeimagens.viewmodel.MainViewModel

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    // Instances the MainViewModel
    private val viewModel: MainViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            viewModel.changeImage(bitmap) // Updates the image in ViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fragment Result Listener for Light Feature
        parentFragmentManager.setFragmentResultListener("lightResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("editedImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap) // Update the image in ViewModel
            }
        }

        // Fragment Result Listener for Crop Feature
        parentFragmentManager.setFragmentResultListener("cropResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("croppedImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap)
            }
        }

        // Listener for Filter Feature
        parentFragmentManager.setFragmentResultListener("filterResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("filteredImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe
        viewModel.image.observe(viewLifecycleOwner) { bitmap: Bitmap? ->
            bitmap?.let {
                binding.imageView2.setImageBitmap(it) // Receives the new image from the Live Data
            }
        }

        // Load Button to PhotoPicker
        binding.buttonLoad.setOnClickListener {
            openPhotoPicker()
        }

        // Crop Button to Fragment
        binding.buttonCrop.setOnClickListener {
            navigateToCropFragment()
        }

        // Light Button to Fragment
        binding.buttonLight.setOnClickListener {
            navigateToLightFragment()
        }

        binding.buttonFilters.setOnClickListener {
            navigateToFiltersFragment()
        }

        // Other buttons go to Fragment Feature
        binding.buttonColors.setOnClickListener { navigateToFeatureFragment("COLOR") }
    }

    private fun openPhotoPicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun navigateToFeatureFragment(featureType: String) {
        // Nav Controller conected to Features
        val action = MainFragmentDirections.actionMainFragmentToFeatureFragment(featureType)
        findNavController().navigate(action)
    }

    private fun navigateToCropFragment() {
        val image = viewModel.image.value

        if (image != null) {
            // Converts the Bitmap
            val action = MainFragmentDirections.actionMainFragmentToCropFragment(image)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "No image Loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLightFragment(){
        val image = viewModel.image.value
        if (image != null){
            val action = MainFragmentDirections.actionMainFragmentToLightFragment(image)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToFiltersFragment() {
        val image = viewModel.image.value
        if (image != null) {
            val action = MainFragmentDirections.actionMainFragmentToFiltersFragment(image)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Corrects the orientation of the Bitmap
        val correctedBitmap = correctBitmapOrientation(uri, originalBitmap)
        return correctedBitmap
    }

    private fun correctBitmapOrientation(uri: Uri, bitmap: Bitmap): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val exif = androidx.exifinterface.media.ExifInterface(inputStream!!)

        val orientation = exif.getAttributeInt(
            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
        )

        val matrix = android.graphics.Matrix()
        when (orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(
                90f
            )

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(
                180f
            )

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(
                270f
            )
        }

        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return rotatedBitmap
    }
}