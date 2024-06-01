package com.buntlit.pictureoftheday.ui.picture

import com.buntlit.pictureoftheday.ui.favorite.RoomFavoritesPictures
import com.google.gson.annotations.SerializedName

data class PODServerResponseData(
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("hdurl") val hdUrl: String?
) {

    fun toFavoritesDBEntity(): RoomFavoritesPictures = RoomFavoritesPictures(
        copyright = copyright,
        date = date!!,
        explanation = explanation,
        mediaType = mediaType,
        title = title,
        url = url,
        hdUrl = hdUrl
    )
}