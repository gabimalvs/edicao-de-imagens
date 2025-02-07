package com.example.aplicativodeediodeimagens.data

class PhotoHistoryRepository(private val photoHistoryDao: PhotoHistoryDao) {

    suspend fun insertPhoto(photo: PhotoHistory) {
        photoHistoryDao.insertPhoto(photo)
    }

    suspend fun getAllPhotos(): List<PhotoHistory> {
        return photoHistoryDao.getAllPhotos()
    }
}