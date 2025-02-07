package com.example.aplicativodeediodeimagens.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoHistoryDao {

    @Insert
    //Adds a new register
    suspend fun insertPhoto(photo: PhotoHistory)

    @Query("SELECT * FROM photo_history ORDER BY creationDate DESC")
    //Returns all the sorted images
    suspend fun getAllPhotos(): List<PhotoHistory>
}