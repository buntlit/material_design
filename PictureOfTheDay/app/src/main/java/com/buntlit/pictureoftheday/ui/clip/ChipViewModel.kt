package com.buntlit.pictureoftheday.ui.clip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChipViewModel(private val liveDataSettings: MutableLiveData<ChipData> = MutableLiveData()) :
    ViewModel() {

    fun getData(): LiveData<ChipData> {
        return liveDataSettings
    }

    fun setData(chipId: Int, themeId: Int, isNewTheme: Boolean) {
        liveDataSettings.postValue(ChipData(chipId, themeId, isNewTheme))
    }

    fun getChipId(): Int?{
        return liveDataSettings.value?.chipId
    }
    fun getThemeId(): Int?{
        return liveDataSettings.value?.themeId
    }
}