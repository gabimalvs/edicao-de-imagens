package com.example.aplicativodeediodeimagens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicativodeediodeimagens.databinding.FragmentMainBinding
import androidx.activity.result.contract.ActivityResultContracts
import com.example.aplicativodeediodeimagens.R

class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            binding.imageView2.setImageURI(it) // Exibir a imagem selecionada no ImageView
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

        // Configurar o botão LOAD para abrir o PhotoPicker
        binding.buttonLoad.setOnClickListener {
            openPhotoPicker()
        }

        // Configurar os botões inferiores para navegar para o FeatureFragment com argumentos
        binding.buttonCrop.setOnClickListener { navigateToFeatureFragment("CROP") }
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
}