package com.buntlit.pictureoftheday.ui.rover

import com.google.gson.annotations.SerializedName

data class PhotoServerResponseData(
    @field:SerializedName("photos") val photo: MutableList<PhotoServerResponseDataPhotos>?
)

data class PhotoServerResponseDataPhotos(
    @field:SerializedName("img_src") val url: String?,
    @field:SerializedName("camera") val camera: PhotoServerResponseDataPhotosCamera?
)

data class PhotoServerResponseDataPhotosCamera(
    @field:SerializedName("name") val cameraName: String?
)