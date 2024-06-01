package com.buntlit.pictureoftheday.ui.rover

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SaveStateViewModel (private val savedStateHandle: SavedStateHandle): ViewModel(){
    private fun getStateRecycler(): LiveData<List<Boolean>> {
        return savedStateHandle.getLiveData("RECYCLER")
    }

    fun getRecyclerIsOpens(): List<Boolean>?{
        return getStateRecycler().value
    }

    fun saveStateRecycler(listIsItemOpen: List<Boolean>){
        savedStateHandle["RECYCLER"] = listIsItemOpen
    }

    private fun getStateFragment(): LiveData<Boolean> {
        return savedStateHandle.getLiveData("FRAGMENT")
    }

    fun getFragmentIsFirstTime(): Boolean?{
        return getStateFragment().value
    }

    fun saveStateFragment(isFirstTimeOpen: Boolean){
        savedStateHandle["FRAGMENT"] = isFirstTimeOpen
    }

    private fun getStateCalendar(): LiveData<Boolean> {
        return savedStateHandle.getLiveData("CALENDAR")
    }

    fun getCalendarIsOpen(): Boolean?{
        return getStateCalendar().value
    }

    fun saveStateCalendar(isCalendarOpen: Boolean){
        savedStateHandle["CALENDAR"] = isCalendarOpen
    }

    private fun getStateAdapter(): LiveData<Int> {
        return savedStateHandle.getLiveData("ADAPTER")
    }

    fun getAdapterPosition(): Int?{
        return getStateAdapter().value
    }

    fun saveStateAdapter(position: Int){
        savedStateHandle["ADAPTER"] = position
    }

    private fun getStatePhotosAdapter(): LiveData<Int> {
        return savedStateHandle.getLiveData("PHOTO")
    }

    fun getPhotosAdapterPosition(): Int?{
        return getStatePhotosAdapter().value
    }

    fun saveStatePhotosAdapter(position: Int){
        savedStateHandle["PHOTO"] = position
    }
}