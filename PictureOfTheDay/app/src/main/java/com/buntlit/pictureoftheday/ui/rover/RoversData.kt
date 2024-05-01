package com.buntlit.pictureoftheday.ui.rover

sealed class RoversData{
    data class Success(val serverResponseData: RoversServerResponseData) : RoversData()
    data class Error(val error: Throwable) : RoversData()
    data class Loading(val progress: Int?) : RoversData()
}
