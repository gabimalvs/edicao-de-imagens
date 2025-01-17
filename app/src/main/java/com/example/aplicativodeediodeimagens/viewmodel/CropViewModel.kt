package com.example.aplicativodeediodeimagens.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CropViewModel : ViewModel() {

    // LiveData privada para armazenar a imagem
    private val _image: MutableLiveData<Bitmap> = MutableLiveData()

    // LiveData pública
    val image: LiveData<Bitmap> = _image

    // Atualiza a imagem em edição
    fun changeImage(bitmap: Bitmap) {
        _image.postValue(bitmap)
    }
}