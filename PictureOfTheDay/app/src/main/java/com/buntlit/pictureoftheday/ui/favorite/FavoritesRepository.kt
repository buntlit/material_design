package com.buntlit.pictureoftheday.ui.favorite

import androidx.lifecycle.MediatorLiveData

class FavoritesRepository(private val favoritesDAO: FavoritesDAO) {
    private val mediatorLiveData = MediatorLiveData<List<RoomFavoritesPictures>>()
    fun getLiveData(): MediatorLiveData<List<RoomFavoritesPictures>> {
        mediatorLiveData.addSource(favoritesDAO.getLiveData()) {
            mediatorLiveData.value = it
        }
        return mediatorLiveData
    }

    suspend fun getAllSortDefault(): List<RoomFavoritesPictures> = favoritesDAO.getAllSortDefault()

    suspend fun getAllSortDate(): List<RoomFavoritesPictures> = favoritesDAO.getAllSortDate()

    suspend fun getAllSortName(): List<RoomFavoritesPictures> = favoritesDAO.getAllSortName()

    suspend fun getAllSortType(): List<RoomFavoritesPictures> = favoritesDAO.getAllSortType()

    fun getIfLiked(date: String): RoomFavoritesPictures = favoritesDAO.getIfLiked(date)

    suspend fun insert(response: RoomFavoritesPictures) = favoritesDAO.insert(response)

    suspend fun delete(response: RoomFavoritesPictures) = favoritesDAO.delete(response)

}