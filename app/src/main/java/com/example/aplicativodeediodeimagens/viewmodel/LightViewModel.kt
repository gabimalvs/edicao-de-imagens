package com.example.aplicativodeediodeimagens.viewmodel

import androidx.lifecycle.ViewModel
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LightViewModel : ViewModel(){
    private val _image: MutableLiveData<Bitmap> = MutableLiveData()
    val image: LiveData<Bitmap> get() = _image

    fun changeImage(bitmap: Bitmap){
        _image.postValue(bitmap)
    }
}