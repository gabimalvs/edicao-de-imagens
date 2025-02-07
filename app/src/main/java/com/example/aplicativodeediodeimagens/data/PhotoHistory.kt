package com.example.aplicativodeediodeimagens.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_history")
data class PhotoHistory(
    @PrimaryKey(autoGenerate = true)
    //Primary Key
    val id: Int = 0,
    //Edited Image Path
    val imagePath: String,
    //Creation Date
    val creationDate: Long
)