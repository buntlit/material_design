package com.buntlit.pictureoftheday.ui.favorite

import android.app.Application
import androidx.lifecycle.*
import com.buntlit.pictureoftheday.ui.picture.PODServerResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val liveDataFavorites: MediatorLiveData<List<RoomFavoritesPictures>>
    private val repository: FavoritesRepository
    private var isItemLiked: MutableLiveData<RoomFavoritesPictures?> = MutableLiveData()
    private var position = 0

    init {
        val dao = Database.getDatabase(application).favoritesDAO
        repository = FavoritesRepository(dao)
        liveDataFavorites = repository.getLiveData()
    }

    fun getData(): LiveData<List<RoomFavoritesPictures>>{
        return liveDataFavorites
    }

    fun getIsItemLikedData(): LiveData<RoomFavoritesPictures?> {
        return isItemLiked
    }

    fun insertToFavorite(picture: PODServerResponseData) =
        viewModelScope.launch(Dispatchers.IO){
            repository.insert(picture.toFavoritesDBEntity())
        }

    fun deleteFromFavorite(picture: PODServerResponseData) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(picture.toFavoritesDBEntity())
        }

    fun deleteFromFavorite(picture: RoomFavoritesPictures) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(picture)
        }

    fun isItemLiked(picture: PODServerResponseData) =
        viewModelScope.launch(Dispatchers.IO){
          isItemLiked.postValue(repository.getIfLiked(picture.date!!))
        }

    fun getFavoritesSortDefault() =
        viewModelScope.launch(Dispatchers.IO) {
            liveDataFavorites.postValue(repository.getAllSortDefault())
        }

    fun getFavoritesSortDate() =
        viewModelScope.launch(Dispatchers.IO) {
            liveDataFavorites.postValue(repository.getAllSortDate())
        }

    fun getFavoritesSortName() =
        viewModelScope.launch(Dispatchers.IO) {
            liveDataFavorites.postValue(repository.getAllSortName())
        }

    fun getFavoritesSortType() =
        viewModelScope.launch(Dispatchers.IO) {
            liveDataFavorites.postValue(repository.getAllSortType())
        }

    fun setPosition(position: Int){
        this.position = position
    }

    fun getPosition(): Int = position


}