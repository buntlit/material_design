package com.buntlit.pictureoftheday.ui.rover

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buntlit.pictureoftheday.BuildConfig
import com.buntlit.pictureoftheday.ui.picture.RetrofitImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoversViewModel(
    private val liveDataRoversToObserve: MutableLiveData<RoversData> = MutableLiveData(),
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private var liveDataRoverToObserve: MutableLiveData<RoversServerResponseDataItem> = MutableLiveData(),
    private val liveDataCamerasAndPhotosToObserve: MutableLiveData<MutableMap<
            RoverServerResponseDataItemCameras, MutableList<String>
            >> = MutableLiveData()
) : ViewModel() {

    private var map: MutableMap<RoverServerResponseDataItemCameras, MutableList<String>> =
        mutableMapOf()
    private var oldDate = ""

    fun getData(): MutableLiveData<RoversData> {
        if (!liveDataRoversToObserve.isInitialized) {
            sendRoversServerRequest()
        }
        return liveDataRoversToObserve
    }

    private fun sendRoversServerRequest() {
        liveDataRoversToObserve.value = RoversData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY

        if (apiKey.isBlank()) {
            liveDataRoversToObserve.value =
                RoversData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRoversRetrofitImpl().getListOfRovers(apiKey)
                .enqueue(object : Callback<RoversServerResponseData> {
                    override fun onResponse(
                        call: Call<RoversServerResponseData>,
                        response: Response<RoversServerResponseData>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            liveDataRoversToObserve.value = RoversData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataRoversToObserve.value =
                                    RoversData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataRoversToObserve.value =
                                    RoversData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<RoversServerResponseData>,
                        t: Throwable
                    ) {
                        liveDataRoversToObserve.value = RoversData.Error(t)
                    }

                })
        }
    }

    private fun sendRoverPhotoRequest(roverName: String, date: String) {
        val apiKey: String = BuildConfig.NASA_API_KEY

        val camerasList = getRoversData().value?.cameras!!
        camerasList.forEach {
            map[it] = mutableListOf()
        }

        if (apiKey.isNotBlank()) {
            retrofitImpl.getRoversRetrofitImpl().getListOfPhotos(roverName, apiKey, date)
                .enqueue(object : Callback<PhotoServerResponseData> {
                    override fun onResponse(
                        call: Call<PhotoServerResponseData>,
                        response: Response<PhotoServerResponseData>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            val photoList = response.body()?.photo!!
                            photoList.forEach { photo ->
                                map.forEach { (key, value) ->
                                    if (key.name == photo.camera?.cameraName) {
                                        value.add(photo.url!!)
                                    }
                                }
                            }
                            liveDataCamerasAndPhotosToObserve.value = map
                        }
                    }

                    override fun onFailure(call: Call<PhotoServerResponseData>, t: Throwable) {}

                })
        }
    }


    fun setRoverData(data: RoversServerResponseDataItem) {
        liveDataRoverToObserve.value = data
        clearPhotosData()
    }

    fun getRoversData(): MutableLiveData<RoversServerResponseDataItem> {
        return liveDataRoverToObserve
    }

    fun getPhotosData(): MutableLiveData<MutableMap<RoverServerResponseDataItemCameras, MutableList<String>>> {
        return liveDataCamerasAndPhotosToObserve
    }

    fun updatePhotosData(roverName: String, date: String) {
        if (date != oldDate) {
            map.values.clear()
            sendRoverPhotoRequest(roverName, date)
        }
        oldDate = date
    }

    private fun clearPhotosData() {
        map.clear()
        liveDataCamerasAndPhotosToObserve.value = map

    }
}