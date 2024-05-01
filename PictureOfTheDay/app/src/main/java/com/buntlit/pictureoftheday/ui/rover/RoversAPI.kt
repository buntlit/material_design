package com.buntlit.pictureoftheday.ui.rover

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoversAPI {
    @GET("mars-photos/api/v1/rovers")
    fun getListOfRovers(@Query("api_key") apiKey: String): Call<RoversServerResponseData>

    @GET("mars-photos/api/v1/rovers/{rover_name}/photos")
    fun getListOfPhotos(@Path("rover_name") roverName: String, @Query("api_key") apiKey: String,
                        @Query("earth_date") date: String): Call<PhotoServerResponseData>
}