package com.example.aplicativodeediodeimagens.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CropViewModel : ViewModel() {

    private val _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> = _image

    fun changeImage(bitmap: Bitmap) {
        _image.postValue(bitmap)
    }
}