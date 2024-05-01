package com.buntlit.pictureoftheday.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel(private val liveDataSettings: MutableLiveData<SettingsData> = MutableLiveData()) :
    ViewModel() {

    fun getData(): LiveData<SettingsData> {
        return liveDataSettings
    }

    fun setData(chipId: Int, themeId: Int, isNewTheme: Boolean) {
        liveDataSettings.postValue(SettingsData(chipId, themeId, isNewTheme))
    }

    fun getChipId(): Int?{
        return liveDataSettings.value?.chipId
    }
    fun getThemeId(): Int?{
        return liveDataSettings.value?.themeId
    }
}