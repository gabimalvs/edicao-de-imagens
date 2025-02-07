package com.example.aplicativodeediodeimagens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicativodeediodeimagens.data.AppDatabase
import com.example.aplicativodeediodeimagens.data.PhotoHistoryAdapter
import com.example.aplicativodeediodeimagens.data.PhotoHistoryRepository
import com.example.aplicativodeediodeimagens.data.PhotoHistoryViewModel
import com.example.aplicativodeediodeimagens.data.PhotoHistoryViewModelFactory
import com.example.aplicativodeediodeimagens.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PhotoHistoryAdapter

    private val viewModel: PhotoHistoryViewModel by viewModels {
        PhotoHistoryViewModelFactory(PhotoHistoryRepository(AppDatabase.getDatabase(requireContext()).photoHistoryDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PhotoHistoryAdapter(emptyList())
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter

        // Shows the ROOM's History
        viewModel.getAllPhotos { photos ->
            if (photos.isNotEmpty()) {
                adapter.updateData(photos)
            } else {
                Toast.makeText(requireContext(), "History Empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}