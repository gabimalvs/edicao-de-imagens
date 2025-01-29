package com.example.aplicativodeediodeimagens.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.aplicativodeediodeimagens.databinding.FragmentLightBinding
import com.example.aplicativodeediodeimagens.viewmodel.LightViewModel

class LightFragment : Fragment(), SeekBar.OnSeekBarChangeListener {

    // View Binding
    private var _binding: FragmentLightBinding? = null
    private val binding get() = _binding!!

    // Arguments sent via Navigation Component
    private val args: LightFragmentArgs by navArgs()

    // ViewModel
    private val viewModel: LightViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtains the Bitmap from the arguments and shows them on the ImageView
        args.image?.let { bitmap ->
            viewModel.changeImage(bitmap)
        }

        // Observe the changes in the image
        viewModel.image.observe(viewLifecycleOwner, Observer { bitmap ->
            bitmap?.let {
                binding.imageView.setImageBitmap(it)
            }
        })

        // Configures the SeekBar
        binding.seekBar.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // Updates TextView with the number from the SeekBar
        binding.text.text = "Brightness: $progress"
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}