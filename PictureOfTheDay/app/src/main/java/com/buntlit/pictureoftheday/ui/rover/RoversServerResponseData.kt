package com.buntlit.pictureoftheday.ui.rover

import com.google.gson.annotations.SerializedName

data class RoversServerResponseData(
    @field:SerializedName("rovers") val rover: MutableList<RoversServerResponseDataItem>?
)

data class RoversServerResponseDataItem(
    @field:SerializedName("name") val name: String?,
    @field:SerializedName("landing_date") val landingDate: String?,
    @field:SerializedName("launch_date") val launchDate: String?,
    @field:SerializedName("status") val status: String?,
    @field:SerializedName("max_date") val maxDate: String?,
    @field:SerializedName("total_photos") val totalPhotos: String?,
    @field:SerializedName("cameras") val cameras: MutableList<RoverServerResponseDataItemCameras>
)

data class RoverServerResponseDataItemCameras(
    @field:SerializedName("name") val name: String?,
    @field:SerializedName("full_name") val fullName: String?
)
