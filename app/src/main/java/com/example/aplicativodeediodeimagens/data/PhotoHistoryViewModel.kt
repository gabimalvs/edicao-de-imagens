package com.example.aplicativodeediodeimagens.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PhotoHistoryViewModel(private val repository: PhotoHistoryRepository) : ViewModel() {

    private val _photoHistory = MutableLiveData<List<PhotoHistory>>()
    val photoHistory: LiveData<List<PhotoHistory>> get() = _photoHistory

    fun insertPhoto(photo: PhotoHistory) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
            loadPhotos() // Loads the data after insertion
        }
    }

    fun getAllPhotos(callback: (List<PhotoHistory>) -> Unit) {
        viewModelScope.launch {
            callback(repository.getAllPhotos())
        }
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            _photoHistory.postValue(repository.getAllPhotos())
        }
    }
}

class PhotoHistoryViewModelFactory(private val repository: PhotoHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhotoHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}