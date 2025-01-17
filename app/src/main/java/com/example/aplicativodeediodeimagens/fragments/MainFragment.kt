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

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    // Instancia o MainViewModel
    private val viewModel: MainViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            viewModel.changeImage(bitmap) // Atualiza a imagem no ViewModel
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o Fragment Result Listener
        parentFragmentManager.setFragmentResultListener("cropResult", this) { _, bundle ->
            val bitmap = bundle.getParcelable<Bitmap>("croppedImage")
            if (bitmap != null) {
                viewModel.changeImage(bitmap) // Atualiza o ViewModel com a imagem recortada
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

        // Observa mudanças no LiveData do ViewModel
        viewModel.image.observe(viewLifecycleOwner) { bitmap: Bitmap? ->
            bitmap?.let {
                binding.imageView2.setImageBitmap(it) // Atualiza o ImageView com o Bitmap do LiveData
            }
        }

        // Configurar o botão LOAD para abrir o PhotoPicker
        binding.buttonLoad.setOnClickListener {
            openPhotoPicker()
        }

        // Configura o botão Crop para navegar para o CropFragment
        binding.buttonCrop.setOnClickListener {
            navigateToCropFragment()
        }

        // Configurar os outros botões inferiores para navegar para o FeatureFragment
        binding.buttonLight.setOnClickListener { navigateToFeatureFragment("LIGHT") }
        binding.buttonColors.setOnClickListener { navigateToFeatureFragment("COLOR") }
        binding.buttonFilters.setOnClickListener { navigateToFeatureFragment("FILTERS") }
    }

    private fun openPhotoPicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun navigateToFeatureFragment(featureType: String) {
        // Passar o argumento para o FeatureFragment via NavController
        val action = MainFragmentDirections.actionMainFragmentToFeatureFragment(featureType)
        findNavController().navigate(action)
    }

    private fun navigateToCropFragment() {
        // Obtém a imagem do MainViewModel
        val image = viewModel.image.value

        if (image != null) {
            // Converte o Bitmap em um argumento serializável
            val action = MainFragmentDirections.actionMainFragmentToCropFragment(image)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Nenhuma imagem carregada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Corrige a orientação do bitmap com base nos metadados EXIF
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

class MainViewModel : ViewModel() {
    // Armazenando a imagem editada
    private var _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> = _image

    fun changeImage(bitmap: Bitmap) {
        _image.postValue(bitmap)
    }
}