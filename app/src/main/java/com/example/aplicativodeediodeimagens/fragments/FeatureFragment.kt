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
    private var featureName: String = "Feature Default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Acess the argument sent by Safe Args
        arguments?.let {
            val args = FeatureFragmentArgs.fromBundle(it)
            featureName = args.feature // Saves the argument in the Global Variable
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

        // Update the TextView with args "feature" value
        binding.textViewFeature.text = featureName ?: "No value input"
    }

}