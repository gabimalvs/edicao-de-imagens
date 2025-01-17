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

    // ViewModel do fragmento
    private val viewModel: CropViewModel by viewModels()

    // Recupera os argumentos passados na navegação
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

        // Configura o observer para atualizar o CropImageView sempre que a imagem mudar no ViewModel
        viewModel.image.observe(viewLifecycleOwner, Observer { bitmap: Bitmap? ->
            bitmap?.let {
                binding.cropImageView.setImageBitmap(it)
            }
        })

        // Atualiza o ViewModel com a imagem recebida como argumento
        args.image?.let {
            viewModel.changeImage(it)
        }

        // Configura o botão SAVE
        binding.buttonSave.setOnClickListener {
            saveCroppedImage()
        }

        // Configura o botão ROTATE
        binding.buttonRotate.setOnClickListener {
            rotateCropImage()
        }
    }

    private fun rotateCropImage() {
        try {
            binding.cropImageView.rotateImage(90) // Rotaciona a imagem em 90 graus
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erro ao rotacionar a imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCroppedImage() {
        // Obtém o bitmap recortado da CropImageView
        val croppedBitmap: Bitmap? = binding.cropImageView.croppedImage

        // Atualiza o LiveData do ViewModel
        croppedBitmap?.let { viewModel.changeImage(it) }

        // Define o bitmap recortado como resultado para o MainFragment
        val result = Bundle().apply {
            putParcelable("croppedImage", croppedBitmap)
        }
        setFragmentResult("cropResult", result)

        // Fecha o fragmento
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Libera a referência do binding para evitar vazamento de memória
    }
}