package com.example.aplicativodeediodeimagens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplicativodeediodeimagens.R
import com.example.aplicativodeediodeimagens.databinding.FragmentFeatureBinding

class FeatureFragment : Fragment() {

    private lateinit var binding: FragmentFeatureBinding
    private lateinit var featureName: String // Variável global para armazenar o argumento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar o argumento "feature" passado via Safe Args
        arguments?.let {
            val args = FeatureFragmentArgs.fromBundle(it)
            featureName = args.feature // Salvar o valor do argumento na variável global
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Atualizar o TextView com o valor do argumento "feature"
        binding.textViewFeatureName.text = featureName
    }

}