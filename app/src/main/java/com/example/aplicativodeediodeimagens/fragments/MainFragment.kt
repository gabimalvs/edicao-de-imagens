package com.example.aplicativodeediodeimagens.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aplicativodeediodeimagens.R
import com.example.aplicativodeediodeimagens.databinding.FragmentMainBinding
import com.example.aplicativodeediodeimagens.viewmodel.MainViewModel
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.core.content.FileProvider

//data
import com.example.aplicativodeediodeimagens.data.PhotoHistory
import com.example.aplicativodeediodeimagens.data.PhotoHistoryViewModel
import com.example.aplicativodeediodeimagens.data.PhotoHistoryViewModelFactory
import com.example.aplicativodeediodeimagens.data.AppDatabase
import com.example.aplicativodeediodeimagens.data.PhotoHistoryRepository


//compose
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fragment Result Listeners
        parentFragmentManager.setFragmentResultListener("lightResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("editedImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap)
            }
        }

        parentFragmentManager.setFragmentResultListener("cropResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("croppedImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap)
            }
        }

        parentFragmentManager.setFragmentResultListener("filterResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("filteredImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap) // Updates the image on ViewModel
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

        viewModel.image.observe(viewLifecycleOwner) { bitmap: Bitmap? ->
            bitmap?.let {
                binding.imageView2.setImageBitmap(it)
            }
        }

        binding.buttonLoad.setOnClickListener { openPhotoPicker() }
        binding.buttonCrop.setOnClickListener { navigateToCropFragment() }
        binding.buttonLight.setOnClickListener { navigateToLightFragment() }
        binding.buttonFilters.setOnClickListener { navigateToFiltersFragment() }
        binding.buttonSave.setOnClickListener { saveCurrentImage() }
        binding.buttonShare.setOnClickListener { shareCurrentImage() }
        binding.buttonHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_historyFragment)
        }
    }

    //Saving edited image
    private fun saveCurrentImage() {
        val image = viewModel.image.value
        if (image != null) {
            val path = saveImageToDevice(image, requireContext()) // Image's Path
            if (path != null) {
                val history = PhotoHistory(imagePath = path.toString(), creationDate = System.currentTimeMillis())
                historyViewModel.insertPhoto(history)

                // Debug: Check if the images are being saved
                Toast.makeText(requireContext(), "Image saved in History!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No image to save.", Toast.LENGTH_SHORT).show()
        }
    }

    private val historyViewModel: PhotoHistoryViewModel by viewModels {
        PhotoHistoryViewModelFactory(PhotoHistoryRepository(AppDatabase.getDatabase(requireContext()).photoHistoryDao()))
    }


    // Sharing the saved image
    private fun shareCurrentImage() {
        val image = viewModel.image.value
        if (image != null) {
            val imageUri = saveImageTemporarily(image, requireContext())
            if (imageUri != null) {
                shareImage(imageUri)
            } else {
                Toast.makeText(requireContext(), "Error while sharing", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No image to share", Toast.LENGTH_SHORT).show()
        }
    }

    // Applying the intent to share
    private fun shareImage(imageUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share image via"))
    }

    // Temporary bitmap to temporary cache (parenting)
    private fun saveImageTemporarily(bitmap: Bitmap, context: Context): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_image.png")
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Return the URI safely using FileProvider
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            viewModel.changeImage(bitmap)
        }
    }

    private fun openPhotoPicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun navigateToCropFragment() {
        val image = viewModel.image.value
        if (image != null) {
            val action = MainFragmentDirections.actionMainFragmentToCropFragment(image)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLightFragment() {
        val image = viewModel.image.value
        if (image != null) {
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

    //Saving the image in the Gallery
    private fun saveImageToDevice(bitmap: Bitmap, context: Context) {
        val filename = "EditedImage_${System.currentTimeMillis()}.png"

        val fos: OutputStream? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let { context.contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val file = File(imagesDir, filename)
            FileOutputStream(file)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "Image saved!", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        return correctBitmapOrientation(uri, originalBitmap)
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
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @Composable
    fun MyAnimationOverlay() {
        var visible by remember { mutableStateOf(false) }
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 1000)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha))
                .clickable { visible = !visible }
        )
    }

}