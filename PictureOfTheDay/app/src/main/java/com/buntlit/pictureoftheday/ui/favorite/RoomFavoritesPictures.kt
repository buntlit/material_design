package com.buntlit.pictureoftheday.ui.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomFavoritesPictures(
    val copyright: String?,
    @PrimaryKey val date: String,
    val explanation: String?,
    val mediaType: String?,
    val title: String?,
    val url: String?,
    val hdUrl: String?
)